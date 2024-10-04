import exceptions.MethodNotSupportedException;
import exceptions.TokenException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TokenMatrix extends Token {
    private final String INPUT_DIM = "\nВведите размерность матрицы '%s':\n";
    private final String INPUT_FORMAT = "Положительные числа <кол-во строк> <кол-во столбцов> через пробел:";
    private final String BAD_FORMAT = "Неправильный формат.";
    private final String INPUT_VALUES = "Введите матрицу '%s' БЕЗ ПРОБЕЛОВ в комплексных числах:\n";
    private final String BAD_VALUES = "Вы превысили кол-во столбцов или ввели некорректное число. Вводите заново:";

    static private Map<String, TokenComplex[][]> matrixValues = new HashMap<>();

    static private final Scanner sc = new Scanner(System.in);

    static public void clearValues() {
        matrixValues.clear();
    }

    public TokenMatrix(State state, String name) {
        super(state, name);
    }

    @Override
    public TokenMatrix clone() {
        return (TokenMatrix) super.clone();
    }

    private TokenComplex[][] matrix;
    private int rows;
    private int cols;

    String getLineOrSkip() {
        final String line = sc.nextLine();
        if (line.equalsIgnoreCase("skip"))
            throw new TokenException("skip");
        return line;
    }

    @Override
    public void initValue() {
        if (matrixValues.containsKey(name)) {
            matrix = matrixValues.get(name).clone();
            return;
        }

        System.out.printf(INPUT_DIM, name);
        while (!(rows > 0 && cols > 0)) {
            System.out.println(INPUT_FORMAT);
            try {
                String[] dims = getLineOrSkip().split(" ");
                rows = Integer.parseInt(dims[0]);
                cols = Integer.parseInt(dims[1]);
            } catch (Exception ex) {
                System.out.println(BAD_FORMAT);
            }
        }
        matrix = new TokenComplex[rows][cols];
        System.out.printf(INPUT_VALUES, name);
        boolean flag = true;
        while (flag) {
            try {
                for (int i = 0; i < rows; i++) {
                    final String[] cols_values = getLineOrSkip().split(" ");
                    for (int j = 0; j < cols; j++)
                        matrix[i][j] = new TokenComplex(cols_values[j]);
                }
                flag = false;
            } catch (Exception ex) {
                System.out.println(BAD_VALUES);
            }
        }
        matrixValues.put(name, matrix);
    }

    public void transpose() {
        TokenComplex[][] t_matrix = new TokenComplex[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                t_matrix[j][i] = matrix[i][j];
        // Swap dimensions
        final int t_rows = rows;
        rows = cols;
        cols = t_rows;

        matrix = t_matrix;
    }

    public TokenComplex det() {
        if (rows != cols)
            throw new TokenException("Невозможно вычислить детерминат — матрица не квадратная");
        if (rows == 1)
            return matrix[0][0];
        for (int i = 0; i < rows; i++) {

        }
    }

    @Override
    public String getStringValue() {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            value.append('|');
            for (int j = 0; j < cols; j++)
                value.append(matrix[i][j].getStringValue()).append('\t');
            value.setCharAt(value.length() - 1, '|');
            value.append('\n');
        }

        return value.toString();
    }

    @Override
    public void add(final Token token) {
        if (token.getState() != state)
            throw new TokenException(String.format("Операция %s + %s не поддерживается", state, token.getState()));
        final TokenMatrix arg = (TokenMatrix) token;
        if (rows != arg.rows || cols != arg.cols)
            throw new TokenException("Невозможно сложить матрицы разных размерностей");

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j].add(arg.matrix[i][j]);
    }

    @Override
    public void sub(final Token token) {
        if (token.getState() != state)
            throw new TokenException(String.format("Операция %s - %s не поддерживается", state, token.getState()));
        final TokenMatrix arg = (TokenMatrix) token;
        if (rows != arg.rows || cols != arg.cols)
            throw new TokenException("Невозможно вычесть матрицы разных размерностей");

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j].sub(arg.matrix[i][j]);
    }

    @Override
    public void multi(final Token token) {
        if (token.getState() == State.KF) {
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    matrix[i][j].multi(token);
        } else if (token.getState() == State.VAR) {
            final TokenMatrix arg = (TokenMatrix) token;
            if (cols != arg.rows)
                throw new TokenException("Невозможно перемножить матрицы разных размерностей");
            TokenComplex[][] t_matrix = new TokenComplex[rows][arg.cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < arg.cols; j++) {
                    TokenComplex complex = new TokenComplex();
                    for (int k = 0; k < cols; k++)
                        complex.add(matrix[i][k].getMulti(arg.matrix[k][j]));
                    t_matrix[i][j] = complex;
                }
            }

            matrix = t_matrix.clone();
        } else
            throw new TokenException(String.format("Операция %s * %s не поддерживается", state, token.getState()));
    }

    @Override
    public void div(final Token token) {

    }
}
