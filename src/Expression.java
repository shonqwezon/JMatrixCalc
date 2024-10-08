import exceptions.ExpressionException;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Class for calculating expression of tokens
 */
public class Expression implements Messages {
    private ArrayList<Token> tokens;

    /**
     * @param tokens Array of tokens
     */
    public void loadTokens(ArrayList<Token> tokens) throws Exception {
        for (Token token : tokens) {
            // Init only VARs and KFs
            if (token.getState() != Token.State.VAR && token.getState() != Token.State.KF)
                continue;
            token.initValue();
        }
        this.tokens = tokens;
    }


    /**
     * Shunting Yard algorithm
     *
     * @return Result
     */
    public Token calc() {
        Stack<Token> opersStack = new Stack<>();
        Stack<Token> argsStack = new Stack<>();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            switch (token.getState()) {
                case Token.State.VAR, Token.State.KF -> argsStack.push(token);
                case Token.State.BRACKET -> {
                    if (token.getName().equals(")")) {
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
                        if (i > 0 && tokens.get(i - 1).getState() != Token.State.OPERATOR) {
                            Token oper = opersStack.pop();
                            while (!oper.getName().equals("|")) {
                                Token token2 = argsStack.pop();
                                Token token1 = argsStack.pop();
                                argsStack.push(applyOperation(oper, token1, token2));
                                oper = opersStack.pop();
                            }
                            Token token1 = argsStack.pop();
                            argsStack.push(applyOperation(token, token1, null));
                        } else opersStack.push(token);
                    }
                }
                case Token.State.OPERATOR -> {
                    if (token.getPriority() == 3) {
                        Token token1 = argsStack.pop();
                        argsStack.push(applyOperation(token, token1, null));
                        break;
                    }
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
        if (argsStack.size() != 1) throw new ExpressionException(BAD_EXPRESSION);
        return argsStack.peek();
    }


    /**
     * @param operation Operation
     * @param token1    First argument
     * @param token2    Second argument
     * @return Updated token
     */
    private Token applyOperation(Token operation, Token token1, Token token2) {
        switch (operation.getName()) {
            case "+" -> token1.add(token2);
            case "-" -> token1.sub(token2);
            case "*" -> {
                if (token1.getState() != Token.State.VAR && token2.state == Token.State.VAR) {
                    Token temp = token1;
                    token1 = token2;
                    token2 = temp;
                }
                token1.multi(token2);
            }
            case "/" -> token1.div(token2);
            case "^T" -> {
                if(token1.getState() != Token.State.VAR)
                    throw new ExpressionException(BAD_CAST_REVERSE);
                ((TokenMatrix) token1).transpose();
            }
            case "|" -> {
                if(token1.getState() != Token.State.VAR)
                    throw new ExpressionException(BAD_CAST_DET);
                token1 = ((TokenMatrix) token1).det();
            }
            default -> throw new ExpressionException(UNKNOWN_OPER + operation.getName());
        }

        return token1;
    }
}
