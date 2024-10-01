import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static final String HELLO = "Кальулятор матриц!\nДоступные операции: ( ), +, -, *, /, ^, | |, ^T\n";
    static final String INPUT_EXP = "Введите выражение:";
    static final String RESULT = "Результат: ";

    public static void main(String[] args) {
        System.out.println(HELLO);
        Scanner sc = new Scanner(System.in);
        String input;
        Expression expression = new Expression();
        // Run, while input != exit
        while (true) {
            System.out.println(INPUT_EXP);
            input = sc.nextLine();
            if(input.equalsIgnoreCase("exit"))
                break;
            try {
                // String tokenization
                ArrayList<Token> tokens = Tokenizer.run(input);
                for (Token token : tokens) {
                    System.out.printf("State: %s\t\t\tName: %s\t\tPriority: %d\n", token.getState(), token.getName(), token.getPriority());
                }
                expression.loadTokens(tokens);
                System.out.println(RESULT + expression.calc());
            } catch (Exception ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
            finally {
                Token.clearVars();
            }
        }
    }
}