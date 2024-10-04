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

    private TokenComplex[][] matrix;
    private int rows;
    private int cols;

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
                String[] dims = sc.nextLine().split(" ");
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
                    final String[] cols_values = sc.nextLine().split(" ");
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

    }

    public void det() {

    }

    @Override
    public String getStringValue() {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                value.append(matrix[i][j].getStringValue()).append("\t");
            value.append("\n");
        }

        return value.toString();
    }

    @Override
    public void add(final Token arg2) {
        if (arg2.getState() != state)
            throw new TokenException(String.format("Операция %s + %s не поддерживается", state, arg2.getState()));
    }

    @Override
    public void sub(final Token arg2) {
        if (arg2.getState() != state)
            throw new TokenException(String.format("Операция %s - %s не поддерживается", state, arg2.getState()));
    }

    @Override
    public void multi(final Token arg2) {

    }

    @Override
    public void div(final Token arg2) throws CloneNotSupportedException {

    }
}
