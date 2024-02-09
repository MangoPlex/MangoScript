package xyz.mangostudio.mangoscript.text.parser;

import java.util.ArrayList;
import java.util.List;

import xyz.mangostudio.mangoscript.binary.expr.BoolExpression;
import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.expr.LocalExpression;
import xyz.mangostudio.mangoscript.binary.expr.Operator;
import xyz.mangostudio.mangoscript.binary.expr.OperatorExpression;
import xyz.mangostudio.mangoscript.binary.expr.PropertyGetExpression;
import xyz.mangostudio.mangoscript.binary.expr.PropertySetExpression;
import xyz.mangostudio.mangoscript.binary.expr.PropertyUnaryModifyExpression;
import xyz.mangostudio.mangoscript.binary.expr.SubscriptExpression;
import xyz.mangostudio.mangoscript.binary.expr.SubscriptSetExpression;
import xyz.mangostudio.mangoscript.binary.expr.ThisExpression;
import xyz.mangostudio.mangoscript.binary.expr.Unary;
import xyz.mangostudio.mangoscript.binary.expr.UnaryExpression;
import xyz.mangostudio.mangoscript.binary.expr.literal.LiteralStringExpression;
import xyz.mangostudio.mangoscript.binary.expr.type.ArrayTypeExpression;
import xyz.mangostudio.mangoscript.binary.expr.type.OptionalTypeExpression;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.LiteralFloat;
import xyz.mangostudio.mangoscript.text.lexer.token.LiteralInteger;
import xyz.mangostudio.mangoscript.text.lexer.token.LiteralString;
import xyz.mangostudio.mangoscript.text.lexer.token.Symbol;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;

public class ExpressionChainParser implements Parser {
	public static final ExpressionChainParser PARSER = new ExpressionChainParser();

	private <T> T lastOf(List<T> list) {
		return list.size() > 0 ? list.get(list.size() - 1) : null;
	}

	private <T> void setLast(List<T> list, T value) {
		list.remove(list.size() - 1);
		list.add(value);
	}

	private Expression parseStart(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() instanceof LiteralString s) {
			tokens.skipToken();
			return new LiteralStringExpression(s.content());
		}

		if (tokens.getAhead() instanceof LiteralInteger i) {
			tokens.skipToken();
			return i.toExpression();
		}

		if (tokens.getAhead() instanceof LiteralFloat f) {
			tokens.skipToken();
			return f.toExpression();
		}

		if (tokens.getAhead() == Keyword.THIS) {
			tokens.skipToken();
			return ThisExpression.THIS;
		}

		if (tokens.getAhead() == Keyword.TRUE) {
			tokens.skipToken();
			return BoolExpression.TRUE;
		}

		if (tokens.getAhead() == Keyword.FALSE) {
			tokens.skipToken();
			return BoolExpression.FALSE;
		}

		if (tokens.getAhead() instanceof Symbol symbol) {
			tokens.skipToken();
			return LocalExpression.LOCAL.getProperty(symbol.name());
		}

		Expression ret;
		if ((ret = parsePrefixUnary(allParsers, tokens)) != null) return ret;
		if ((ret = parsePrimitiveType(allParsers, tokens)) != null) return ret;
		return null;
	}

	private Expression parsePrefixUnary(Parser allParsers, TokenStream tokens) {
		Unary u;
		if (tokens.getAhead() instanceof SymbolicKeyword symbolic && (u = symbolicPrefixToUnary(symbolic)) != null) {
			tokens.skipToken();
			Expression expr = allParsers.parseExpression(tokens);
			if (expr == null) throw new ParserException("Expected Expression, but " + tokens.getAhead() + " found");

			if (u == Unary.NOT || u == Unary.NEGATE) {
				// Non-modify unary operators can be used on everything
				return new UnaryExpression(expr, u);
			}

			if (!(expr instanceof PropertyGetExpression getter))
				throw new ParserException("Unary " + u + " is unexpected for " + expr);

			// Operators that modify local can only be used on local
			return new PropertyUnaryModifyExpression(getter.target(), getter.name(), u);
		}

		return null;
	}

	private Unary symbolicPrefixToUnary(SymbolicKeyword s) {
		return switch (s) {
		case NOT -> Unary.NOT;
		case MINUS -> Unary.NEGATE;
		case INCREMENT -> Unary.ADD_BEFORE;
		case DECREMENT -> Unary.SUBTRACT_BEFORE;
		default -> null;
		};
	}

	private Expression parsePrimitiveType(Parser allParsers, TokenStream tokens) {
		Type type;
		if (tokens.getAhead() instanceof Keyword keyword && (type = TypeParser.keywordToType(keyword)) != null) {
			tokens.skipToken();
			return LocalExpression.LOCAL.getProperty(type.toString());
		}

		return null;
	}

	@Override
	public Expression parseExpression(Parser allParsers, TokenStream tokens) {
		List<Expression> exprs = new ArrayList<>();
		List<Operator> operators = new ArrayList<>();

		Token token = tokens.getAhead();
		if (token == SymbolicKeyword.OPEN_PARENTHESES) {
			tokens.skipToken();
			Expression expr = allParsers.parseExpression(tokens);
			if (expr == null) throw new ParserException("Expected expression, but found " + tokens.getAhead());

			if (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES)
				throw new ParserException("Expected ')', but found " + tokens.getAhead());
			tokens.skipToken();
			exprs.add(expr);
		} else {
			Expression start = parseStart(allParsers, tokens);
			if (start == null) return null;
			exprs.add(start);
		}

		while (true) {
			token = tokens.getAhead();

			// Access property
			if (token == SymbolicKeyword.DOT) {
				tokens.skipToken();

				if (!((token = tokens.getAhead()) instanceof Symbol nextSymbol))
					throw new ParserException("Expected Symbol, but " + token + " found");
				tokens.skipToken();

				setLast(exprs, lastOf(exprs).getProperty(nextSymbol.name()));
				continue;
			}

			// Change property
			if (token == SymbolicKeyword.ASSIGN) {
				if (lastOf(exprs) instanceof PropertyGetExpression getter) {
					tokens.skipToken();
					Expression expr = allParsers.parseExpression(tokens);
					if (expr == null) throw new ParserException("Expected expression, but found " + tokens.getAhead());
					setLast(exprs, new PropertySetExpression(getter.target(), getter.name(), expr));
					continue;
				}

				if (lastOf(exprs) instanceof SubscriptExpression subscript) {
					tokens.skipToken();
					Expression expr = allParsers.parseExpression(tokens);
					if (expr == null) throw new ParserException("Expected expression, but found " + tokens.getAhead());
					setLast(exprs, new SubscriptSetExpression(subscript.target(), subscript.args(), expr));
					continue;
				}

				throw new ParserException("Setter is unexpected for " + lastOf(exprs));
			}

			// Unary after
			if (token == SymbolicKeyword.INCREMENT) {
				if (lastOf(exprs) instanceof PropertyGetExpression getter) {
					tokens.skipToken();
					setLast(exprs, new PropertyUnaryModifyExpression(getter.target(), getter.name(), Unary.ADD_AFTER));
					continue;
				}

				throw new ParserException("Unary ++ is unexpected for " + lastOf(exprs));
			}
			if (token == SymbolicKeyword.DECREMENT) {
				if (lastOf(exprs) instanceof PropertyGetExpression getter) {
					tokens.skipToken();
					setLast(exprs,
						new PropertyUnaryModifyExpression(getter.target(), getter.name(), Unary.SUBTRACT_AFTER));
					continue;
				}

				throw new ParserException("Unary -- is unexpected for " + lastOf(exprs));
			}

			// Function call
			if (token == SymbolicKeyword.OPEN_PARENTHESES) {
				tokens.skipToken();

				List<Expression> args = new ArrayList<>();
				while (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES) {
					Expression arg = allParsers.parseExpression(tokens);
					if (arg == null) throw new ParserException("Expected expression, but found " + tokens.getAhead());
					args.add(arg);

					if (tokens.getAhead() == SymbolicKeyword.CLOSE_PARENTHESES) break;
					if ((token = tokens.getAhead()) != SymbolicKeyword.COMMA)
						throw new ParserException("Expected ',' or ')', but " + token + " found");
					tokens.skipToken();
				}

				tokens.skipToken();
				setLast(exprs, lastOf(exprs).call(args.toArray(Expression[]::new)));
				continue;
			}

			// Subscript access
			if (token == SymbolicKeyword.OPEN_SQUARE_BRACKET) {
				tokens.skipToken();

				List<Expression> args = new ArrayList<>();
				while (tokens.getAhead() != SymbolicKeyword.CLOSE_SQUARE_BRACKET) {
					Expression arg = allParsers.parseExpression(tokens);
					if (arg == null) throw new ParserException("Expected expression, but found " + tokens.getAhead());
					args.add(arg);

					if (tokens.getAhead() == SymbolicKeyword.CLOSE_SQUARE_BRACKET) break;
					if ((token = tokens.getAhead()) != SymbolicKeyword.COMMA)
						throw new ParserException("Expected ',' or ']', but " + token + " found");
					tokens.skipToken();
				}

				tokens.skipToken();
				setLast(exprs, lastOf(exprs).subscript(args.toArray(Expression[]::new)));
				continue;
			}

			// Array variant
			if (token == SymbolicKeyword.ARRAY) {
				tokens.skipToken();
				setLast(exprs, new ArrayTypeExpression(lastOf(exprs)));
				continue;
			}

			// Optional variant
			if (token == SymbolicKeyword.QUESTION_MARK) {
				tokens.skipToken();
				setLast(exprs, new OptionalTypeExpression(lastOf(exprs)));
				continue;
			}

			// Operators
			Operator op;
			if (token instanceof SymbolicKeyword keyword && (op = operatorFromKeyword(keyword)) != null) {
				tokens.skipToken();

				// Operate with group
				if (tokens.getAhead() == SymbolicKeyword.OPEN_PARENTHESES) {
					tokens.skipToken();

					Expression expr = allParsers.parseExpression(tokens);
					if (expr == null) throw new ParserException("Expected expression, but found " + tokens.getAhead());

					if (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES)
						throw new ParserException("Expected ')', but found " + tokens.getAhead());
					tokens.skipToken();

					exprs.add(expr);
					operators.add(op);
					continue;
				}

				// Normal
				Expression nextExpr = allParsers.parseExpression(tokens);
				if (nextExpr == null)
					throw new ParserException("Expected Expression, but " + tokens.getAhead() + " found");

				if (nextExpr instanceof OperatorExpression opExp) {
					// Flatten for Pemdas thing
					operators.add(op);
					flatten(exprs, operators, opExp);
				} else {
					exprs.add(nextExpr);
					operators.add(op);
				}

				continue;
			}

			return reduce(exprs, operators);
		}
	}

	private void flatten(List<Expression> exprs, List<Operator> operators, OperatorExpression expression) {
		if (expression.left() instanceof OperatorExpression leftOp) flatten(exprs, operators, leftOp);
		else exprs.add(expression.left());

		operators.add(expression.operator());

		if (expression.right() instanceof OperatorExpression rightOp) flatten(exprs, operators, rightOp);
		else exprs.add(expression.right());
	}

	private Operator operatorFromKeyword(SymbolicKeyword opKwd) {
		return switch (opKwd) {
		case PLUS -> Operator.ADD;
		case MINUS -> Operator.SUBTRACT;
		case MULTIPLY -> Operator.MULTIPLY;
		case DIVIDE -> Operator.DIVIDE;
		case AND -> Operator.AND;
		case OR -> Operator.OR;
		case XOR -> Operator.XOR;
		case LESS_THAN -> Operator.LESS_THAN;
		case GREATER_THAN -> Operator.GREATER_THAN;
		case LESS_THAN_OR_EQUALS -> Operator.LESS_THAN_OR_EQUALS;
		case GREATER_THAN_OR_EQUALS -> Operator.GREATER_THAN_OR_EQUALS;
		case EQUALS -> Operator.EQUALS;
		default -> null;
		};
	}

	private Expression reduce(List<Expression> expressions, List<Operator> operators) {
		while (expressions.size() > 1) {
			for (Operator[] set : Operator.ORDER_OF_OPERATIONS) {
				boolean applied = false;

				do {
					applied = false;

					for (int i = 1; i < expressions.size(); i++) {
						Expression left = expressions.get(i - 1);
						Expression right = expressions.get(i);
						Operator operator = operators.get(i - 1);

						if (containsIn(set, operator)) {
							expressions.remove(i);
							expressions.set(i - 1, left.op(operator, right));
							operators.remove(i - 1);
							applied = true;
							break;
						}
					}
				} while (applied);
			}
		}

		return expressions.get(0);
	}

	private <T> boolean containsIn(T[] arr, T target) {
		for (T obj : arr) if (obj == target) return true;
		return false;
	}
}
