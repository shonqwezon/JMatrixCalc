import exceptions.TokenException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TokenMatrix extends Token implements Messages, Commands {
    static private Map<String, TokenComplex[][]> matrixValues = new HashMap<>();

    static private final Scanner sc = new Scanner(System.in);

    static public void clearValues() {
        matrixValues.clear();
    }

    public TokenMatrix(State state, String name) {
        super(state, name);
    }

    private void setMatrix(TokenComplex[][] newMatrix) {
        matrix = newMatrix;
        rows = newMatrix.length;
        cols = newMatrix[0].length;
    }

    @Override
    public TokenMatrix clone() {
        TokenMatrix tokenMatrix = (TokenMatrix) super.clone();
        tokenMatrix.matrix = new TokenComplex[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                tokenMatrix.matrix[i][j] = matrix[i][j].clone();

        return tokenMatrix;
    }

    private TokenComplex[][] matrix;
    private int rows;
    private int cols;

    String getLineOrSkip() {
        final String line = sc.nextLine();
        if (line.equalsIgnoreCase(SKIP))
            throw new TokenException(SKIP);
        return line;
    }

    @Override
    public void initValue() {
        if (matrixValues.containsKey(name)) {
            setMatrix(matrixValues.get(name).clone());
            return;
        }

        System.out.printf(INPUT_DIM, name);
        while (!(rows > 0 && cols > 0)) {
            System.out.println(INPUT_FORMAT);
            try {
                String[] dims = getLineOrSkip().split(" ");
                rows = Integer.parseInt(dims[0]);
                cols = Integer.parseInt(dims[1]);
            } catch (TokenException ex) {
                throw ex;
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
            } catch (TokenException ex) {
                throw ex;
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

        setMatrix(t_matrix);
    }

    public TokenComplex det() {
        if (rows != cols)
            throw new TokenException(EX_DET);
        return getDeterminant(matrix);
    }

    private TokenComplex getDeterminant(final TokenComplex[][] matrix) {
        final int size = matrix.length;
        if (size == 1)
            return matrix[0][0].clone();
        TokenComplex complex = new TokenComplex();
        for (int col = 0; col < size; col++) {
            TokenComplex sign = new TokenComplex(Math.pow(-1, col), 0);
            TokenComplex addition = matrix[0][col].getMulti(sign);
            addition.multi(getDeterminant(minor(matrix, 0, col)));
            complex.add(addition);
        }

        return complex;
    }

    private TokenComplex[][] minor(final TokenComplex[][] matrix, int row, int col) {
        final int size = matrix.length;
        TokenComplex[][] minorMatrix = new TokenComplex[size - 1][size - 1];
        for (int j = 0, mj = 0; j < size; j++) {
            if (j == col) continue;
            for (int i = 0, mi = 0; i < size; i++) {
                if (i == row) continue;
                minorMatrix[mi++][mj] = matrix[i][j].clone();
            }
            mj++;
        }
        return minorMatrix;
    }

    @Override
    public String getStringValue() {
        StringBuilder value = new StringBuilder();
        int[] spaces = new int[cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                if(j + 1 == cols)
                    spaces[j] = Math.max(spaces[j], matrix[i][j].getStringValue().length());
                else
                    spaces[j] = Math.max(spaces[j], matrix[i][j].getStringValue().length() + 2);
            }
        for (int i = 0; i < rows; i++) {
            value.append('|');
            for (int j = 0; j < cols; j++)
                value.append(String.format("%-" + spaces[j] + "s", matrix[i][j].getStringValue()));
            value.append("|\n");
        }

        return value.toString();
    }

    @Override
    public void add(final Token token) {
        if (token.getState() != state)
            throw new TokenException(String.format(EX_BAD_OPERATION, state, '-', token.getState()));
        final TokenMatrix arg = (TokenMatrix) token;
        if (rows != arg.rows || cols != arg.cols)
            throw new TokenException(String.format(EX_BAD_DIMS, "сложить"));

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j].add(arg.matrix[i][j]);
    }

    @Override
    public void sub(final Token token) {
        if (token.getState() != state)
            throw new TokenException(String.format(EX_BAD_OPERATION, state, '-', token.getState()));
        final TokenMatrix arg = (TokenMatrix) token;
        if (rows != arg.rows || cols != arg.cols)
            throw new TokenException(String.format(EX_BAD_DIMS, "вычесть"));

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
                throw new TokenException(String.format(EX_BAD_DIMS, "перемножить"));
            TokenComplex[][] t_matrix = new TokenComplex[rows][arg.cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < arg.cols; j++) {
                    TokenComplex complex = new TokenComplex();
                    for (int k = 0; k < cols; k++)
                        complex.add(matrix[i][k].getMulti(arg.matrix[k][j]));
                    t_matrix[i][j] = complex;
                }
            }

            setMatrix(t_matrix);
        } else
            throw new TokenException(String.format(EX_BAD_OPERATION, state, '*', token.getState()));
    }

    @Override
    public void div(final Token token) {
        if (token.getState() == State.KF) {
            TokenComplex uno = new TokenComplex(1, 0);
            uno.div(token);
            multi(uno);
            return;
        }
        if (token.getState() != state)
            throw new TokenException(String.format(EX_BAD_OPERATION, state, '/', token.getState()));

        TokenMatrix reverseMatrix = getReverse((TokenMatrix) token);
        multi(reverseMatrix);
    }

    private TokenMatrix getReverse(final TokenMatrix tokenMatrix) {
        TokenComplex det;
        try {
            det = tokenMatrix.det();
        } catch (TokenException ex) {
            throw new TokenException(EX_DIV_SQUARE);
        }
        if (det.isNull())
            throw new TokenException(EX_FIV_DET);

        TokenMatrix reverseMatrix = tokenMatrix.clone();
        for (int i = 0; i < reverseMatrix.rows; i++) {
            for (int j = 0; j < reverseMatrix.cols; j++) {
                TokenComplex sign = new TokenComplex(Math.pow(-1, i + j), 0);
                TokenComplex addition = getDeterminant(minor(tokenMatrix.matrix, i, j));
                addition.multi(sign);
                addition.div(det);
                reverseMatrix.matrix[i][j] = addition;
            }
        }
        reverseMatrix.transpose();
        return reverseMatrix;
    }
}
