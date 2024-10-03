import exceptions.TokenException;

public class TokenComplex extends Token {
    public TokenComplex(State state, String name) {
        super(state, name);

        real = 0;
        imaginary = 0;
    }

    private double real;
    private double imaginary;

    @Override
    public String getStringValue() {
        String value = "";
        if(real != 0)
            value += real + " ";
        if(imaginary != 0) {
            if (imaginary > 0 && !value.isEmpty()) value += "+ ";
            else if(imaginary < 0 && !value.isEmpty()) value += "- ";
            else if (imaginary < 0 && value.isEmpty()) value += "-";
            if (Math.abs(imaginary) == 1) value += "i";
            else value += Math.abs(imaginary) + "i";
        }
        return value.isEmpty() ? "0" : value;
    }

    @Override
    public void initValue() throws Exception {
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
    public void add(final Token arg2) {
        if(arg2.getState() != state)
            throw new TokenException(String.format("Операция %s + %s не поддерживается", state, arg2.getState()));
        final TokenComplex arg = (TokenComplex) arg2;
        real += arg.real;
        imaginary += arg.imaginary;
    }

    @Override
    public void sub(final Token arg2) {
        if(arg2.getState() != state)
            throw new TokenException(String.format("Операция %s - %s не поддерживается", state, arg2.getState()));
        final TokenComplex arg = (TokenComplex) arg2;
        real -= arg.real;
        imaginary -= arg.imaginary;
    }

    @Override
    public void multi(final Token arg2) {
        if(arg2.getState() != state)
            throw new TokenException(String.format("Операция %s * %s не поддерживается", state, arg2.getState()));
        final TokenComplex arg = (TokenComplex) arg2;
        real *= arg.real;
        imaginary *= arg.real;
        double t_imaginary = real * arg.imaginary;
        real = -imaginary * arg.imaginary;
        imaginary = t_imaginary;
    }

    @Override
    public void div(final Token arg2) throws CloneNotSupportedException {
        if(arg2.getState() != state)
            throw new TokenException(String.format("Операция %s / %s не поддерживается", state, arg2.getState()));
        TokenComplex t_conj = (TokenComplex) ((TokenComplex)arg2).clone();
        t_conj.imaginary *= -1;
        multi(t_conj);
        TokenComplex denom = (TokenComplex) this.clone();
        denom.multi(t_conj);
        real /= denom.real;
        imaginary /= denom.real;
    }
}
