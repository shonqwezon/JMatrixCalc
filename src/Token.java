import exceptions.MethodNotSupportedException;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for working with tokens
 */
public class Token {
    public enum State {
        NONE, VAR, KF, OPERATOR, BRACKET
    }

    protected final State state;
    protected String name;
    final protected int priority;

    public Token(State state, String name, int priority) {
        this.state = state;
        this.name = name;
        this.priority = priority;
    }

    public Token(State state, String name) {
        this(state, name, 0);
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

    public void appendToName(char c) {
        name += c;
    }

    public String getStringValue() {
        throw new MethodNotSupportedException();
    }

    public void initValue() throws Exception {
        throw new MethodNotSupportedException();
    }

    public void add(final Token arg2) {
        throw new MethodNotSupportedException();
    }

    public void sub(final Token arg2) {
        throw new MethodNotSupportedException();
    }

    public void multi(final Token arg2) {
        throw new MethodNotSupportedException();
    }

    public void div(final Token arg2) throws CloneNotSupportedException {
        throw new MethodNotSupportedException();
    }
}
