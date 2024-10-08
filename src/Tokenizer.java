import exceptions.ExpressionException;
import exceptions.TokenException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tokenizer implements Messages {
    /**
     * Map for storing operator's priority
     */
    private static final Map<Character, Integer> operators = new HashMap<>();
    private static ArrayList<Token> tokens;

    static {
        operators.put('+', 1);
        operators.put('-', 1);
        operators.put('*', 2);
        operators.put('/', 2);
        operators.put('^', 3);
    }

    private static Token createToken(Token.State currentState, char c) {
        return createToken(currentState, Character.toString(c));
    }

    private static Token createToken(Token.State currentState, String s) {
        return switch (currentState) {
            case VAR -> new TokenMatrix(currentState, s);
            case KF -> new TokenComplex(currentState, s);
            case OPERATOR -> new Token(currentState, s, operators.get(s.charAt(0)));
            default -> new Token(currentState, s);
        };
    }

    /**
     * @param s Symbol that is appended to a last token name
     */
    private static void updateLastTokenName(char s) {
        int lastIndex = tokens.size() - 1;
        Token lastToken = tokens.get(lastIndex);
        // If the token starts with i, and the next symbol is appending, then it is a VAR now (instead of KF)
        if (lastToken.getName().charAt(0) == 'i' && lastToken.getState() == Token.State.KF) {
            lastToken = createToken(Token.State.VAR, lastToken.getName());
            tokens.set(lastIndex, lastToken);
        }
        lastToken.appendToName(s);
    }

    /**
     * @param input String to tokenize
     * @return ArrayList of tokens
     * @throws TokenException An error in expression
     */
    public static ArrayList<Token> run(final String input) throws Exception {
        tokens = new ArrayList<>();
        // Initial state
        Token.State lastState = Token.State.NONE;
        for (int i = 0; i < input.length(); i++) {
            char s = input.charAt(i);
            Token.State currentState = getCharState(s, i + 1);
            if(currentState == Token.State.NONE) {
                lastState = currentState;
                continue;
            }
            switch (lastState) {
                case NONE, BRACKET -> {
                    // Add extra tokens for handling unary '-'
                    if (s == '-' && (tokens.isEmpty() || tokens.getLast().getName().equals("(") || tokens.getLast().getName().equals("|"))) {
                        if(tokens.size() >= 2 && tokens.get(tokens.size() - 2).getState() != Token.State.OPERATOR)
                            tokens.add(createToken(Token.State.OPERATOR, '+'));
                        tokens.add(createToken(Token.State.KF, "-1"));
                        tokens.add(createToken(Token.State.OPERATOR, '*'));
                    } else if (currentState == Token.State.OPERATOR && !tokens.isEmpty() && tokens.getLast().getName().equals("(")) {
                        throw new TokenException(String.format(WAITED_OPERAND, i));
                    } else
                        tokens.add(createToken(currentState, s));
                }
                case VAR -> {
                    // Update previous VAR
                    if (currentState == Token.State.VAR || currentState == Token.State.KF) {
                        updateLastTokenName(s);
                        currentState = Token.State.VAR;
                    }
                    // Start the new VAR
                    else
                        tokens.add(createToken(currentState, s));
                }
                case KF -> {
                    if (currentState == Token.State.KF || s == 'i')
                        updateLastTokenName(s);
                    else if (currentState == Token.State.VAR)
                        updateLastTokenName(s);
                    else
                        tokens.add(createToken(currentState, s));
                }

                case OPERATOR -> {
                    // Add special operator for matrix
                    if (currentState == Token.State.VAR && s == 'T') {
                        updateLastTokenName(s);
                        currentState = Token.State.NONE;
                    } else
                        tokens.add(createToken(currentState, s));
                }
            }
            lastState = currentState;
        }

        return tokens;
    }

    /**
     * @param s     Char to determine its type (state)
     * @param index Index in order to show position of an unexpected symbol
     * @return State of the char
     * @throws Exception Bumped into an unexpected symbol
     */
    private static Token.State getCharState(char s, int index) throws Exception {
        if (Character.isSpaceChar(s)) return Token.State.NONE;
        else if (Character.isDigit(s) || s == '.' || s == 'i') return Token.State.KF;
        else if (Character.isLetter(s)) return Token.State.VAR;
        else if (operators.containsKey(s)) return Token.State.OPERATOR;
        else if (s == '(' || s == ')' || s == '|') return Token.State.BRACKET;
        else throw new TokenException(String.format(UNKNOWN_CHAR, index));
    }
}

