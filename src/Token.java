import exceptions.MethodNotSupportedException;

/**
 * Class for working with tokens
 */
public class Token implements Cloneable {
    @Override
    public Token clone() {
        try {
            return (Token) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

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
        throw new MethodNotSupportedException("getStringValue()");
    }

    public void initValue() {
        throw new MethodNotSupportedException("initValue()");
    }

    public void add(final Token arg2) {
        throw new MethodNotSupportedException("add()");
    }

    public void sub(final Token arg2) {
        throw new MethodNotSupportedException("sub()");
    }

    public void multi(final Token arg2) {
        throw new MethodNotSupportedException("multi()");
    }

    public void div(final Token arg2) {
        throw new MethodNotSupportedException("div()");
    }
}
