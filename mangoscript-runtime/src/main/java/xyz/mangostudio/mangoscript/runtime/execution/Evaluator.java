package xyz.mangostudio.mangoscript.runtime.execution;

import java.util.HashMap;
import java.util.Map;

import xyz.mangostudio.mangoscript.binary.expr.BoolExpression;
import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.expr.InvokeExpression;
import xyz.mangostudio.mangoscript.binary.expr.LocalExpression;
import xyz.mangostudio.mangoscript.binary.expr.Operator;
import xyz.mangostudio.mangoscript.binary.expr.OperatorExpression;
import xyz.mangostudio.mangoscript.binary.expr.PropertyGetExpression;
import xyz.mangostudio.mangoscript.binary.expr.PropertySetExpression;
import xyz.mangostudio.mangoscript.binary.expr.PropertyUnaryModifyExpression;
import xyz.mangostudio.mangoscript.binary.expr.SubscriptExpression;
import xyz.mangostudio.mangoscript.binary.expr.SubscriptSetExpression;
import xyz.mangostudio.mangoscript.binary.expr.ThisExpression;
import xyz.mangostudio.mangoscript.binary.expr.UnaryExpression;
import xyz.mangostudio.mangoscript.binary.expr.literal.LiteralNumberExpression;
import xyz.mangostudio.mangoscript.binary.expr.literal.LiteralStringExpression;
import xyz.mangostudio.mangoscript.binary.expr.type.ArrayTypeExpression;
import xyz.mangostudio.mangoscript.binary.expr.type.OptionalTypeExpression;
import xyz.mangostudio.mangoscript.runtime.value.OptionalValue;
import xyz.mangostudio.mangoscript.runtime.value.TypeValue;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F64Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I64Value;
import xyz.mangostudio.mangoscript.runtime.value.object.StringValue;

/**
 * <p>
 * Evaluator evaluates expressions and returns value from that.
 * </p>
 */
public class Evaluator {
	@SuppressWarnings("rawtypes")
	private static final Map<Class<?>, ExpressionEvaluator> EVALUATORS = new HashMap<>();

	/**
	 * <p>
	 * Register an evaluator to evaluate custom expressions (which implements
	 * {@link Expression}). This can be useful if you are writing your own runtime
	 * optimizer.
	 * </p>
	 * 
	 * @param <T>             The type parameter for expression class.
	 * @param expressionClass The expression class whose expression will be
	 *                        evaluated by custom evaluator.
	 * @param executor        The custom expression evaluator.
	 */
	public static <T extends Expression> void registerEvaluator(Class<T> expressionClass, ExpressionEvaluator<T> executor) {
		if (expressionClass == null) throw new NullPointerException("Expression class can't be null");
		if (executor == null) throw new NullPointerException("Expression evaluator can't be null");
		if (EVALUATORS.containsKey(expressionClass))
			throw new IllegalArgumentException("Already registered: " + expressionClass.getCanonicalName());
		EVALUATORS.put(expressionClass, (ExpressionEvaluator<?>) executor);
	}

	static {
		registerEvaluator(ThisExpression.class, (context, expression) -> context.getThis());
		registerEvaluator(PropertyGetExpression.class, (context, getter) -> {
			Expression target = getter.target();
			if (target == LocalExpression.LOCAL) return context.getLocal(getter.name());
			return evaluate(context, target).getProperty(getter.name());
		});
		registerEvaluator(PropertySetExpression.class, (context, setter) -> {
			Expression target = setter.target();
			Value value = evaluate(context, setter.expression());
			if (target == LocalExpression.LOCAL) context.setLocal(setter.name(), value);
			else context.getThis().setProperty(setter.name(), value);
			return value;
		});
		registerEvaluator(PropertyUnaryModifyExpression.class, (context, unary) -> {
			Expression target = unary.target();
			Value value = context.getLocal(unary.name());
			Value result = value;

			switch (unary.type()) {
			case ADD_BEFORE:
				result = value = value.inc();
				break;
			case ADD_AFTER:
				value = value.inc();
				break;
			case SUBTRACT_BEFORE:
				result = value = value.dec();
				break;
			case SUBTRACT_AFTER:
				value = value.dec();
				break;
			default:
				throw new RuntimeException("Can't apply " + unary + " because " + unary.type()
					+ " is not allowed");
			}

			if (target == LocalExpression.LOCAL) context.setLocal(unary.name(), value);
			else context.getThis().setProperty(unary.name(), value);
			return result;
		});
		registerEvaluator(UnaryExpression.class, (context, unary) -> {
			Value value = evaluate(context, unary.expression());
			return switch (unary.type()) {
			case NEGATE -> value.negate();
			case NOT -> value.not();
			default -> throw new RuntimeException("Can't evaluate " + unary);
			};
		});
		registerEvaluator(OptionalTypeExpression.class, (context, optional) -> {
			Value source = evaluate(context, optional.parent());
			if (!(source instanceof TypeValue tv)) throw new RuntimeException(optional.parent() + " is not a type");
			return new TypeValue(tv.targetType().optionalVariant());
		});
		registerEvaluator(ArrayTypeExpression.class, (context, array) -> {
			Value source = evaluate(context, array.parent());
			if (!(source instanceof TypeValue tv)) throw new RuntimeException(array.parent() + " is not a type");
			return new TypeValue(tv.targetType().arrayVariant());
		});
		registerEvaluator(InvokeExpression.class, (context, invoke) -> {
			Value target = evaluate(context, invoke.target());
			Value[] args = new Value[invoke.args().length];
			for (int i = 0; i < args.length; i++) args[i] = evaluate(context, invoke.args()[i]);
			return target.call(args);
		});
		registerEvaluator(LiteralStringExpression.class, (context, s) -> new StringValue(s.content()));
		registerEvaluator(LiteralNumberExpression.class, (context, n) -> switch (n.type()) {
		case I32 -> new I32Value(n.asInt());
		case I64 -> new I64Value(n.value());
		case F32 -> new F32Value(n.asFloat());
		case F64 -> new F64Value(n.asDouble());
		default -> throw new RuntimeException("Invalid number type: " + n.type());
		});
		registerEvaluator(SubscriptExpression.class, (context, ss) -> {
			Value target = evaluate(context, ss.target());
			Value[] args = new Value[ss.args().length];
			for (int i = 0; i < args.length; i++) args[i] = evaluate(context, ss.args()[i]);
			return target.subscriptGet(args);
		});
		registerEvaluator(SubscriptSetExpression.class, (context, sss) -> {
			Value target = evaluate(context, sss.target());
			Value[] args = new Value[sss.args().length];
			for (int i = 0; i < args.length; i++) args[i] = evaluate(context, sss.args()[i]);
			target.subscriptSet(args, target);
			return target.subscriptGet(args);
		});
		registerEvaluator(OperatorExpression.class, (context, op) -> {
			Value left = evaluate(context, op.left());
			Value right = evaluate(context, op.right());

			if (op.operator() == Operator.EMPTY_COALESCING) {
				if (left instanceof OptionalValue optLeft) {
					if (!optLeft.isEmpty()) return optLeft.value();
					return right;
				}

				return left;
			}

			return switch (op.operator()) {
			case ADD -> left.add(right);
			case SUBTRACT -> left.subtract(right);
			case MULTIPLY -> left.multiply(right);
			case DIVIDE -> left.divide(right);
			case EQUALS -> left.valueEquals(right);
			case AND -> left.and(right);
			case OR -> left.or(right);
			case XOR -> left.xor(right);
			case LESS_THAN -> left.lessThan(right);
			case LESS_THAN_OR_EQUALS -> left.lessThanOrEquals(right);
			case GREATER_THAN -> left.greaterThan(right);
			case GREATER_THAN_OR_EQUALS -> left.greaterThanOrEquals(right);
			case SHIFT_LEFT -> left.shiftLeft(right);
			case SHIFT_RIGHT -> left.shiftRight(right);
			default -> throw new RuntimeException("Not yet implemented: " + op.operator());
			};
		});
		registerEvaluator(BoolExpression.class, (context, bool) -> switch (bool) {
		case TRUE -> I32Value.TRUE;
		case FALSE -> I32Value.FALSE;
		default -> throw new RuntimeException("Unknown boolean enum: " + bool);
		});
	}

	/**
	 * <p>
	 * Evaluate the expression and returns the output. This will only returns
	 * {@code null} if the expression calls a function whose result type is
	 * {@code PrimitiveType#VOID}.
	 * </p>
	 * 
	 * @param context    The execution context.
	 * @param expression The expression to evaluate.
	 * @return Value created from expression evaluation.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Value evaluate(ExecutionContext context, Expression expression) {
		ExpressionEvaluator evaluator = EVALUATORS.get(expression.getClass());
		if (evaluator == null) throw new RuntimeException("No evaluator registered for expression: " + expression);
		return evaluator.evaluate(context, expression);
	}
}
