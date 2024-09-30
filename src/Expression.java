import java.util.ArrayList;
import java.util.Stack;

public class Expression {
    static public double calc(ArrayList<Token> tokens) throws Exception {
        Stack<Token> opersStack = new Stack<>();
        Stack<Token> argsStack = new Stack<>();

        for (Token token : tokens) {
            Token.State state = token.getState();
            if (state == Token.State.VAR || state == Token.State.KF)
                argsStack.push(token);
            else if (state == Token.State.BRACKET) {
                if (token.getName().equals(")")) {
                    if (opersStack.isEmpty())
                        throw new Exception("Некорректное выражение");
                    Token oper = opersStack.pop();
                    while (!oper.getName().equals("(")) {
                        Token token2 = argsStack.pop();
                        Token token1 = argsStack.pop();
                        argsStack.push(applyOperation(oper, token1, token2));
                        oper = opersStack.pop();
                    }
                } else if (token.getName().equals("("))
                    opersStack.push(token);
                else {
                    // Handling "|"
                }
            } else if (state == Token.State.OPERATOR) {
                if (!opersStack.isEmpty() && opersStack.peek().getState() == Token.State.OPERATOR
                        && opersStack.peek().getPriority() >= token.getPriority()) {
                    Token token2 = argsStack.pop();
                    Token token1 = argsStack.pop();
                    argsStack.push(applyOperation(opersStack.pop(), token1, token2));
                }
                opersStack.push(token);
            }
        }

        while (!opersStack.isEmpty()) {
            Token token2 = argsStack.pop();
            Token token1 = argsStack.pop();
            argsStack.push(applyOperation(opersStack.pop(), token1, token2));
        }

        if (argsStack.size() != 1) throw new Exception("Некорректное выражение");
        Token token = argsStack.peek();
        return (token.getState() == Token.State.VAR) ? token.getValue() : Double.parseDouble(token.getName());
    }

    static private Token applyOperation(Token operation, Token token1, Token token2) {
        double arg1 = (token1.getState() == Token.State.VAR) ? token1.getValue() : Double.parseDouble(token1.getName());
        double arg2 = (token2.getState() == Token.State.VAR) ? token2.getValue() : Double.parseDouble(token2.getName());
        double value = switch (operation.getName()) {
            case "+" -> arg1 + arg2;
            case "-" -> arg1 - arg2;
            case "*" -> arg1 * arg2;
            case "/" -> arg1 / arg2;
            case "^" -> Math.pow(arg1, arg2);
            default -> throw new IllegalStateException("Unexpected value: " + operation.getName());
        };

        return new Token(value);
    }
}
