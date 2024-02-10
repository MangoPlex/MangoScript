package xyz.mangostudio.mangoscript.runtime.execution;

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

public class Executor {
	public static ExecutionResult execute(ExecutionContext context, Statement statement) {
		if (statement instanceof LoopControlStatement control) return switch (control) {
		case BREAK -> ControlResult.BREAK;
		case CONTINUE -> ControlResult.CONTINUE;
		default -> throw new RuntimeException("Not yet implemented: " + control);
		};

		if (statement instanceof ReturnStatement returner)
			return new ReturnResult(Evaluator.evaluate(context, returner.expression()));

		if (statement instanceof ScopeStatement scope) {
			ExecutionContext newScope = context.newScope();

			for (Statement s : scope.children()) {
				ExecutionResult result = execute(newScope, s);
				if (result != ControlResult.NORMAL) return result;
			}

			return ControlResult.NORMAL;
		}

		if (statement instanceof DefineVariableStatement define) {
			Value value = define.expression() != null
				? Evaluator.evaluate(context, define.expression())
				: Value.createDefault(define.type());
			context.defineLocal(define.name(), value);
			return ControlResult.NORMAL;
		}

		if (statement instanceof ForStatement forLoop) {
			for (execute(context, forLoop.initial()); Evaluator.evaluate(context, forLoop.condition())
				.asBoolean(); Evaluator.evaluate(context, forLoop.next())) {
				ExecutionResult result = execute(context, forLoop.whileTrue());
				if (result == ControlResult.BREAK) break;
				if (result == ControlResult.CONTINUE) continue;
				if (result instanceof ReturnResult) return result;
			}

			return ControlResult.NORMAL;
		}

		if (statement instanceof WhileStatement whileLoop) {
			while (Evaluator.evaluate(context, whileLoop.condition()).asBoolean()) {
				ExecutionResult result = execute(context, whileLoop.whileTrue());
				if (result == ControlResult.BREAK) break;
				if (result == ControlResult.CONTINUE) continue;
				if (result instanceof ReturnResult) return result;
			}

			return ControlResult.NORMAL;
		}

		if (statement instanceof DoWhileStatement whileLoop) {
			do {
				ExecutionResult result = execute(context, whileLoop.whileTrue());
				if (result == ControlResult.BREAK) break;
				if (result == ControlResult.CONTINUE) continue;
				if (result instanceof ReturnResult) return result;
			} while (Evaluator.evaluate(context, whileLoop.condition()).asBoolean());

			return ControlResult.NORMAL;
		}

		if (statement instanceof ConditionalStatement conditional) {
			if (Evaluator.evaluate(context, conditional.condition()).asBoolean())
				return execute(context, conditional.ifTrue());
			if (conditional.ifFalse() != null)
				return execute(context, conditional.ifFalse());
			return ControlResult.NORMAL;
		}

		if (statement instanceof EvaluateStatement evaluate) {
			Evaluator.evaluate(context, evaluate.expression());
			return ControlResult.NORMAL;
		}

		throw new RuntimeException("Not yet implemented: " + statement);
	}

	public static Value executeFunction(ExecutionContext context, Function function, Value... args) {
		if (function instanceof NativeFunction nativeFunc) return nativeFunc.callNative(context.getThis(), args);

		if (function instanceof GuestFunction guest) {
			FunctionSignature signature = context.getModule().resolveSignature(guest.getFunctionSignature());
			Value[] passArgs = AutoCast.castToFunctionSignature(signature, args);
			return executeFunction(context, guest, passArgs);
		}

		throw new RuntimeException("Unknown function variant: " + function.getClass());
	}

	public static Value executeFunction(ExecutionContext context, GuestFunction function, Value... args) {
		function.getArgumentsMapping().forEach((name, position) -> context.defineLocal(name, args[position]));
		ExecutionResult result = execute(context, function.getCode());
		if (result == ControlResult.BREAK || result == ControlResult.CONTINUE)
			throw new RuntimeException(result + " can only be used in loops");
		if (result instanceof ReturnResult ret) return ret.value();
		return null;
	}
}
