package xyz.mangostudio.mangoscript.runtime.execution;

import java.util.HashMap;
import java.util.Map;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.binary.func.GuestFunction;
import xyz.mangostudio.mangoscript.binary.stmt.ConditionalStatement;
import xyz.mangostudio.mangoscript.binary.stmt.DefineVariableStatement;
import xyz.mangostudio.mangoscript.binary.stmt.EvaluateStatement;
import xyz.mangostudio.mangoscript.binary.stmt.ReturnStatement;
import xyz.mangostudio.mangoscript.binary.stmt.ScopeStatement;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.DoWhileStatement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.ForStatement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.LoopControlStatement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.WhileStatement;
import xyz.mangostudio.mangoscript.runtime.execution.result.ControlResult;
import xyz.mangostudio.mangoscript.runtime.execution.result.ExecutionResult;
import xyz.mangostudio.mangoscript.runtime.execution.result.ReturnResult;
import xyz.mangostudio.mangoscript.runtime.host.NativeFunction;
import xyz.mangostudio.mangoscript.runtime.value.Value;

/**
 * <p>
 * Executor executes statements and functions and manipulate the execution
 * context accordingly. The execution context is where all local variables are
 * stored.
 * </p>
 */
public class Executor {
	@SuppressWarnings("rawtypes")
	private static final Map<Class<?>, StatementExecutor> EXECUTORS = new HashMap<>();

	/**
	 * <p>
	 * Register an executor to execute custom statement (which implements
	 * {@link Statement}). This can be useful if you are writing your own optimizer.
	 * </p>
	 * 
	 * @param <T>            The type parameter for statement class.
	 * @param statementClass The statement class to target.
	 *                       {@link #execute(ExecutionContext, Statement)} checks
	 *                       {@link Statement#getClass()} instead of traditional
	 *                       {@code instanceof}, so the class must be exact and no
	 *                       superclasses are allowed.
	 * @param executor       The executor to execute your custom statement.
	 */
	public static <T extends Statement> void registerExecutor(Class<T> statementClass, StatementExecutor<T> executor) {
		if (statementClass == null) throw new NullPointerException("Statement class can't be null");
		if (executor == null) throw new NullPointerException("Statement executor can't be null");
		if (EXECUTORS.containsKey(statementClass))
			throw new IllegalArgumentException("Already registered: " + statementClass.getCanonicalName());
		EXECUTORS.put(statementClass, (StatementExecutor<?>) executor);
	}

	static {
		registerExecutor(LoopControlStatement.class, (context, statement) -> switch (statement) {
		case BREAK -> ControlResult.BREAK;
		case CONTINUE -> ControlResult.CONTINUE;
		default -> throw new RuntimeException("Not yet implemented: " + statement);
		});

		registerExecutor(ReturnStatement.class,
			(context, statement) -> new ReturnResult(Evaluator.evaluate(context, statement.expression())));
		registerExecutor(ScopeStatement.class, (context, scope) -> {
			ExecutionContext newScope = context.newScope();

			for (Statement s : scope.children()) {
				ExecutionResult result = execute(newScope, s);
				if (result != ControlResult.NORMAL) return result;
			}

			return ControlResult.NORMAL;
		});
		registerExecutor(DefineVariableStatement.class, (context, define) -> {
			Value value = define.expression() != null
				? Evaluator.evaluate(context, define.expression())
				: Value.createDefault(define.type());
			context.defineLocal(define.name(), value);
			return ControlResult.NORMAL;
		});
		registerExecutor(ForStatement.class, (context, forLoop) -> {
			for (execute(context, forLoop.initial()); Evaluator.evaluate(context, forLoop.condition())
				.asBoolean(); Evaluator.evaluate(context, forLoop.next())) {
				ExecutionResult result = execute(context, forLoop.whileTrue());
				if (result == ControlResult.BREAK) break;
				if (result == ControlResult.CONTINUE) continue;
				if (result instanceof ReturnResult) return result;
			}

			return ControlResult.NORMAL;
		});
		registerExecutor(WhileStatement.class, (context, whileLoop) -> {
			while (Evaluator.evaluate(context, whileLoop.condition()).asBoolean()) {
				ExecutionResult result = execute(context, whileLoop.whileTrue());
				if (result == ControlResult.BREAK) break;
				if (result == ControlResult.CONTINUE) continue;
				if (result instanceof ReturnResult) return result;
			}

			return ControlResult.NORMAL;
		});
		registerExecutor(DoWhileStatement.class, (context, whileLoop) -> {
			do {
				ExecutionResult result = execute(context, whileLoop.whileTrue());
				if (result == ControlResult.BREAK) break;
				if (result == ControlResult.CONTINUE) continue;
				if (result instanceof ReturnResult) return result;
			} while (Evaluator.evaluate(context, whileLoop.condition()).asBoolean());

			return ControlResult.NORMAL;
		});
		registerExecutor(ConditionalStatement.class, (context, conditional) -> {
			if (Evaluator.evaluate(context, conditional.condition()).asBoolean())
				return execute(context, conditional.ifTrue());
			if (conditional.ifFalse() != null)
				return execute(context, conditional.ifFalse());
			return ControlResult.NORMAL;
		});
		registerExecutor(EvaluateStatement.class, (context, evaluate) -> {
			Evaluator.evaluate(context, evaluate.expression());
			return ControlResult.NORMAL;
		});
	}

	/**
	 * <p>
	 * Execute a single statement and returns the execution result. The result can
	 * be normal (indicates the program should continue), {@code break},
	 * {@code continue} (for loops) or {@code return}.
	 * </p>
	 * 
	 * @param context   The execution context to execute statement.
	 * @param statement The statement to execute.
	 * @return The execution result.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ExecutionResult execute(ExecutionContext context, Statement statement) {
		StatementExecutor executor = EXECUTORS.get(statement.getClass());
		if (executor == null) throw new RuntimeException("No executor registered for statement: " + statement);
		return executor.execute(context, statement);
	}

	/**
	 * <p>
	 * Execute the function. The only 2 supported functions are
	 * {@link GuestFunction} (which came from user's code) and any class that
	 * implements {@link NativeFunction} (which came from host/embedder).
	 * </p>
	 * <p>
	 * The function implementation can be customized with {@link NativeFunction}
	 * interface, where what happens under the hood can't be controlled by
	 * MangoScript Runtime.
	 * </p>
	 * 
	 * @param context  The execution context to execute function. Note that this
	 *                 method also adds guest's parameters as local to this context.
	 * @param function The function to execute.
	 * @param args     An array of arguments to pass to function.
	 * @return Result of the function, or {@code null} if the output function
	 *         signature is {@code void}.
	 */
	public static Value executeFunction(ExecutionContext context, Function function, Value... args) {
		if (function instanceof NativeFunction nativeFunc) return nativeFunc.callNative(context.getThis(), args);

		if (function instanceof GuestFunction guest) {
			FunctionSignature signature = context.getModule().resolveSignature(guest.getFunctionSignature());
			Value[] passArgs = AutoCast.castToFunctionSignature(signature, args);
			return executeFunction(context, guest, passArgs);
		}

		throw new RuntimeException("Unknown function variant: " + function.getClass());
	}

	/**
	 * <p>
	 * Execute the guest function.
	 * </p>
	 * 
	 * @param context  The execution context to execute function.
	 * @param function The function to execute.
	 * @param args     An array of arguments to pass to function.
	 * @return Result of the function, or {@code null} if the output function
	 *         signature is {@code void}.
	 */
	public static Value executeFunction(ExecutionContext context, GuestFunction function, Value... args) {
		function.getArgumentsMapping().forEach((name, position) -> context.defineLocal(name, args[position]));
		ExecutionResult result = execute(context, function.getCode());
		if (result == ControlResult.BREAK || result == ControlResult.CONTINUE)
			throw new RuntimeException(result + " can only be used in loops");
		if (result instanceof ReturnResult ret) return ret.value();
		return null;
	}
}
