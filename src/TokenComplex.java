import exceptions.TokenException;

import java.util.ArrayList;

public class TokenComplex extends Token {
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
                throw new TokenException("Некорректное число");
        }

        Expression expression = new Expression();
        expression.loadTokens(tokens);

        final TokenComplex complex = (TokenComplex) expression.calc();

        real = complex.real;
        imaginary = complex.imaginary;
    }

    private double real;
    private double imaginary;

    @Override
    public String getStringValue() {
        String value = "";
        if (real != 0)
            value += real + " ";
        if (imaginary != 0) {
            if (imaginary > 0 && !value.isEmpty()) value += "+ ";
            else if (imaginary < 0 && !value.isEmpty()) value += "- ";
            else if (imaginary < 0 && value.isEmpty()) value += "-";
            if (Math.abs(imaginary) == 1) value += "i";
            else value += Math.abs(imaginary) + "i";
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
            throw new TokenException("Неверный формат числа");
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

    @Override
    public void div(final Token token) throws CloneNotSupportedException {
        if (token.getState() != state)
            throw new TokenException(String.format("Операция %s / %s не поддерживается", state, token.getState()));
        TokenComplex t_conj = (TokenComplex) ((TokenComplex) token).clone();
        t_conj.imaginary *= -1;
        multi(t_conj);
        TokenComplex denom = (TokenComplex) this.clone();
        denom.multi(t_conj);
        real /= denom.real;
        imaginary /= denom.real;
    }
}
