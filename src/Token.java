import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Token {
    public enum State {
        NONE,
        VAR,
        KF,
        OPERATOR,
        BRACKET
    }

    static class Tokenizer {
        private static final Map<Character, Integer> operators = new HashMap<>();

        static {
            operators.put('+', 1);
            operators.put('-', 1);
            operators.put('*', 2);
            operators.put('/', 2);
            operators.put('^', 3);
        }

        private void updateLastTokenName(ArrayList<Token> tokens, char s) {
            int lastIndex = tokens.size() - 1;
            Token lastToken = tokens.get(lastIndex);
            if(lastToken.name.charAt(0) == 'i' && lastToken.state == State.KF)
                lastToken.state = State.VAR;
            lastToken.name += s;
            tokens.set(lastIndex, lastToken);
        }

         public ArrayList<Token> run(final String input) throws Exception {
            ArrayList<Token> tokens = new ArrayList<>();

            State lastState = State.NONE;
            for (int i = 0; i < input.length(); i++) {
                char s = input.charAt(i);
                State currentState = getCharState(s, i + 1);
                if (currentState != State.NONE)
                    switch (lastState) {
                        case NONE -> {
                            if (s == '-' && tokens.isEmpty()) {
                                tokens.add(new Token(State.KF, "-1"));
                                tokens.add(new Token(State.OPERATOR, '*'));
                            } else
                                tokens.add(new Token(currentState, s));
                        }
                        case VAR -> {
                            // Update previous VAR
                            if (currentState == State.VAR || currentState == State.KF) {
                                updateLastTokenName(tokens, s);
                                currentState = State.VAR;
                            }
                            // Start the new VAR
                            else
                                tokens.add(new Token(currentState, s));
                        }
                        case KF -> {
                            if (currentState == State.KF || s == 'i')
                                updateLastTokenName(tokens, s);
                            else if (currentState == State.VAR || tokens.getLast().name.charAt(0) == 'i')
                                updateLastTokenName(tokens, s);
                            else
                                tokens.add(new Token(currentState, s));
                        }

                        case OPERATOR -> {
                            if (currentState == State.VAR && s == 'T') {
                                updateLastTokenName(tokens, s);
                                currentState = State.OPERATOR;
                            } else
                                tokens.add(new Token(currentState, s));
                        }
                        case BRACKET -> {
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

    static public double isDefinedVar(String name) {
        return varValues.getOrDefault(name, NULL_VALUE);
    }

    static public void defineVar(String name, double value) {
        varValues.put(name, value);
    }

    static public void clearVars() {
        varValues.clear();
    }

    public Token(State state, String name, int priority, double value) {
        if(state == State.OPERATOR)
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
