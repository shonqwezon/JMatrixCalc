import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for working with tokens
 */
public class Token {
    public enum State {
        NONE,
        VAR,
        KF,
        OPERATOR,
        BRACKET
    }

    /**
     * Class for generating tokens
     */
    static class Tokenizer {
        /**
         * Map for storing operator's priority
         */
        private static final Map<Character, Integer> operators = new HashMap<>();
        private ArrayList<Token> tokens;

        static {
            operators.put('+', 1);
            operators.put('-', 1);
            operators.put('*', 2);
            operators.put('/', 2);
            operators.put('^', 3);
        }

        /**
         * @param s Symbol that is appended to a last token name
         */
        private void updateLastTokenName(char s) {
            int lastIndex = tokens.size() - 1;
            Token lastToken = tokens.get(lastIndex);
            // If the token starts with i, and the next symbol is appending, then it is a VAR now (instead of KF)
            if (lastToken.name.charAt(0) == 'i' && lastToken.state == State.KF)
                lastToken.state = State.VAR;
            lastToken.name += s;
            tokens.set(lastIndex, lastToken);
        }

        /**
         * @param input String to tokenize
         * @return ArrayList of tokens
         * @throws Exception An error in expression
         */
        public ArrayList<Token> run(final String input) throws Exception {
            tokens = new ArrayList<>();
            // Initial state
            State lastState = State.NONE;
            for (int i = 0; i < input.length(); i++) {
                char s = input.charAt(i);
                State currentState = getCharState(s, i + 1);
                switch (lastState) {
                    case NONE -> {
                        // Skip NULL symbols, except initial state
                        if (currentState == State.NONE) break;
                        // Add extra tokens for handling unary '-'
                        if (s == '-' && tokens.isEmpty()) {
                            tokens.add(new Token(State.KF, "-1"));
                            tokens.add(new Token(State.OPERATOR, '*'));
                        } else
                            tokens.add(new Token(currentState, s));
                    }
                    case VAR -> {
                        // Update previous VAR
                        if (currentState == State.VAR || currentState == State.KF) {
                            updateLastTokenName(s);
                            currentState = State.VAR;
                        }
                        // Start the new VAR
                        else
                            tokens.add(new Token(currentState, s));
                    }
                    case KF -> {
                        if (currentState == State.KF || s == 'i')
                            updateLastTokenName(s);
                        else if (currentState == State.VAR || tokens.getLast().name.charAt(0) == 'i')
                            updateLastTokenName(s);
                        else
                            tokens.add(new Token(currentState, s));
                    }

                    case OPERATOR -> {
                        // Add special operator for matrix
                        if (currentState == State.VAR && s == 'T') {
                            updateLastTokenName(s);
                            currentState = State.OPERATOR;
                        } else
                            tokens.add(new Token(currentState, s));
                    }
                    case BRACKET -> {
                        // Add extra tokens for handling unary '-'
                        if (s == '-') {
                            tokens.add(new Token(State.KF, "-1"));
                            tokens.add(new Token(State.OPERATOR, '*'));
                        } else
                            tokens.add(new Token(currentState, s));
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
        private State getCharState(char s, int index) throws Exception {
            if (Character.isSpaceChar(s)) return State.NONE;
            else if (Character.isDigit(s) || s == '.' || s == 'i') return State.KF;
            else if (Character.isLetter(s)) return State.VAR;
            else if (operators.containsKey(s)) return State.OPERATOR;
            else if (s == '(' || s == ')' || s == '|') return State.BRACKET;
            else throw new Exception(String.format("Неизвестный символ (%d)", index));
        }
    }

    private State state;
    private String name;
    final private int priority;
    private double value;

    static public Map<String, Double> varValues = new HashMap<>();
    static public double NULL_VALUE = -1_000_000_000;

    static public double getVarIfDefined(String name) {
        return varValues.getOrDefault(name, NULL_VALUE);
    }

    static public void defineVar(String name, double value) {
        varValues.put(name, value);
    }

    static public void clearVars() {
        varValues.clear();
    }

    public Token(State state, String name, int priority, double value) {
        if (state == State.OPERATOR)
            priority = Tokenizer.operators.get(name.charAt(0));
        this.state = state;
        this.name = name;
        this.priority = priority;
        this.value = value;
    }

    public Token(double value) {
        this(State.VAR, "", 0, value);
    }

    public Token(State state, String name) {
        this(state, name, 0, NULL_VALUE);
    }

    public Token(State state, char name) {
        this(state, Character.toString(name), 0, NULL_VALUE);
    }

    public int getPriority() {
        return priority;
    }

    public State getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
