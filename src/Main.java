import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static final String HELLO = "Кальулятор матриц!\nДоступные операции: ( ), +, -, *, /, ^, | |, ^T\n";
    static final String INPUT_EXP = "Введите выражение:";
    static final String INPUT_DEF = "\nВведите значение для матрицы '%s':\n";
    static final String RESULT = "Результат: ";

    public static void main(String[] args) {
        System.out.println(HELLO);
        Scanner sc = new Scanner(System.in);
        String input;
        while (true) {
            System.out.println(INPUT_EXP);
            input = sc.nextLine();
            try {
                // String tokenization
                ArrayList<Token> tokens = new Token.Tokenizer().run(input);
                for (Token token : tokens) {
                    System.out.printf("State: %s\t\t\tName: %s\t\tPriority: %d\n", token.getState(), token.getName(), token.getPriority());
                }
                // Getting matrix values for VAR
                for (Token token : tokens) {
                    if (token.getState() != Token.State.VAR) continue;
                    if (Token.isDefinedVar(token.getName()) != Token.NULL_VALUE) {
                        token.setValue(Token.isDefinedVar(token.getName()));
                        continue;
                    }
                    System.out.printf(INPUT_DEF, token.getName());
                    token.setValue(sc.nextInt());
                    Token.defineVar(token.getName(), token.getValue());
                    sc.nextLine();
                }
                System.out.println(RESULT + Expression.calc(tokens));
            } catch (Exception ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
            finally {
                Token.clearVars();
            }
        }
    }
}