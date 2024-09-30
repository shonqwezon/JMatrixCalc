import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 * Class for calculating expression of tokens
 */
public class Expression {
    private final Scanner sc = new Scanner(System.in);
    private final String INPUT_DEF = "\nВведите значение для матрицы '%s':\n";
    private ArrayList<Token> tokens;

    /**
     * @param tokens Array of tokens
     */
    public void loadTokens(ArrayList<Token> tokens) {
        // Getting matrix values for VAR
        for (Token token : tokens) {
            if (token.getState() != Token.State.VAR) continue;
            // Init token from hash table
            if (Token.getVarIfDefined(token.getName()) != Token.NULL_VALUE) {
                token.setValue(Token.getVarIfDefined(token.getName()));
                continue;
            }
            System.out.printf(INPUT_DEF, token.getName());
            token.setValue(sc.nextInt());
            Token.defineVar(token.getName(), token.getValue());
            sc.nextLine();
        }
        this.tokens = tokens;
    }


    /** Shunting Yard algorithm
     * @return Result
     * @throws Exception Error during calculating
     */
    public double calc() throws Exception {
        Stack<Token> opersStack = new Stack<>();
        Stack<Token> argsStack = new Stack<>();

        for (Token token : tokens) {
            switch (token.getState()) {
                case Token.State.VAR, Token.State.KF -> argsStack.push(token);
                case Token.State.BRACKET -> {
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
                }
                case Token.State.OPERATOR -> {
                    if (!opersStack.isEmpty() && opersStack.peek().getState() == Token.State.OPERATOR
                            && opersStack.peek().getPriority() >= token.getPriority()) {
                        Token token2 = argsStack.pop();
                        Token token1 = argsStack.pop();
                        argsStack.push(applyOperation(opersStack.pop(), token1, token2));
                    }
                    opersStack.push(token);
                }
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


    /**
     * @param operation
     * @param token1 First argument
     * @param token2 Second argument
     * @return Updated token
     */
    private Token applyOperation(Token operation, Token token1, Token token2) {
        double arg1 = (token1.getState() == Token.State.VAR) ? token1.getValue() : Double.parseDouble(token1.getName());
        double arg2 = (token2.getState() == Token.State.VAR) ? token2.getValue() : Double.parseDouble(token2.getName());
        double value = switch (operation.getName()) {
            case "+" -> arg1 + arg2;
            case "-" -> arg1 - arg2;
            case "*" -> arg1 * arg2;
            case "/" -> arg1 / arg2;
            case "^" -> Math.pow(arg1, arg2);
            default -> throw new IllegalStateException("Неизвестный оператор: " + operation.getName());
        };

        return new Token(value);
    }
}
