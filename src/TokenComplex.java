import exceptions.TokenException;

import java.util.ArrayList;

public class TokenComplex extends Token implements Messages {
    public TokenComplex(State state, String name) {
        super(state, name);

        real = 0;
        imaginary = 0;
    }

    public TokenComplex(String name) throws Exception {
        super(State.KF, name);

        ArrayList<Token> tokens = Tokenizer.run(name);
        for (Token token : tokens) {
            if (token.getState() == State.VAR)
                throw new TokenException(BAD_NUM);
        }

        Expression expression = new Expression();
        expression.loadTokens(tokens);

        final TokenComplex complex = (TokenComplex) expression.calc();

        real = complex.real;
        imaginary = complex.imaginary;
    }

    public TokenComplex(double real, double imaginary) {
        this(State.KF, "0");
        this.real = real;
        this.imaginary = imaginary;
    }

    public TokenComplex() {
        this(0, 0);
    }

    @Override
    public TokenComplex clone() {
        return (TokenComplex) super.clone();
    }

    private double real;
    private double imaginary;

    @Override
    public String getStringValue() {
        String value = "";
        final double t_real = Math.round(real * 100.0) / 100.0;
        final double t_imaginary = Math.round(imaginary * 100.0) / 100.0;
        if (t_real != 0)
            value += (t_real % 1 == 0) ? String.format("%.0f", t_real) : String.format("%.2f", t_real);
        if (t_imaginary != 0) {
            if (t_imaginary > 0 && !value.isEmpty()) value += "+";
            else if (t_imaginary < 0) value += "-";
            if (Math.abs(t_imaginary) == 1) value += "i";
            else
                value += ((t_imaginary % 1 == 0) ? String.format("%.0f", Math.abs(t_imaginary)) : String.format("%.2f", Math.abs(t_imaginary))) + "i";
        }
        return value.isEmpty() ? "0" : value;
    }

    @Override
    public void initValue() {
        try {
            if (name.charAt(name.length() - 1) == 'i') {
                if (name.length() == 1) this.imaginary = 1;
                else this.imaginary = Double.parseDouble(name.substring(0, name.length() - 1));
            } else
                real = Double.parseDouble(name);
        } catch (NumberFormatException ex) {
            throw new TokenException(BAD_FORMAT_NUM);
        }
    }

    @Override
    public void add(final Token token) {
        if (token.getState() != state)
            throw new TokenException(String.format("Операция %s + %s не поддерживается", state, token.getState()));
        final TokenComplex arg = (TokenComplex) token;
        real += arg.real;
        imaginary += arg.imaginary;
    }

    @Override
    public void sub(final Token token) {
        if (token.getState() != state)
            throw new TokenException(String.format("Операция %s - %s не поддерживается", state, token.getState()));
        final TokenComplex arg = (TokenComplex) token;
        real -= arg.real;
        imaginary -= arg.imaginary;
    }

    @Override
    public void multi(final Token token) {
        if (token.getState() != state)
            throw new TokenException(String.format("Операция %s * %s не поддерживается", state, token.getState()));
        final TokenComplex arg = (TokenComplex) token;
        double t_real = real;
        double t_imaginary = imaginary;
        real = real * arg.real - imaginary * arg.imaginary;
        imaginary = t_real * arg.imaginary + arg.real * t_imaginary;
    }

    public TokenComplex getMulti(final Token token) {
        TokenComplex tokenComplex = this.clone();
        tokenComplex.multi(token);
        return tokenComplex;
    }

    @Override
    public void div(final Token token) {
        if (token.getState() != state)
            throw new TokenException(String.format("Операция %s / %s не поддерживается", state, token.getState()));
        TokenComplex t_conj = ((TokenComplex) token).clone();
        if(t_conj.isNull())
            throw new TokenException(DIV_ZERO);
        t_conj.imaginary *= -1;
        multi(t_conj);
        TokenComplex denom = (TokenComplex) token.clone();
        denom.multi(t_conj);
        real /= denom.real;
        imaginary /= denom.real;
    }

    public boolean isNull() {
        return real == 0 && imaginary == 0;
    }
}
