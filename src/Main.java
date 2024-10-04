import exceptions.ExpressionException;
import exceptions.MethodNotSupportedException;
import exceptions.TokenException;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Scanner;

public class Main {
    static final String HELLO = "Кальулятор матриц!\nДоступные операции: ( ), +, -, *, /, | |, ^T\n";
    static final String INPUT_EXP = "Введите выражение:";
    static final String RESULT = "Результат:\n";

    public static void main(String[] args) {
        System.out.println(HELLO);
        Scanner sc = new Scanner(System.in);
        String input;
        Expression expression = new Expression();
        // Run, while input != exit
        while (true) {
            System.out.println(INPUT_EXP);
            input = sc.nextLine();
            if (input.equalsIgnoreCase("exit"))
                break;
            try {
                // String tokenization
                ArrayList<Token> tokens = Tokenizer.run(input);
                // Printing tokens
//                for (Token token : tokens)
//                    if(token.getState() != Token.State.NONE)
//                        System.out.printf("State: %s\t\t\tName: %s\t\tPriority: %d\n", token.getState(), token.getName(), token.getPriority());

                expression.loadTokens(tokens);
                System.out.println(RESULT + expression.calc().getStringValue());

            } catch (MethodNotSupportedException ex) {
                System.out.println("Ошибка: Токен не поддерживает метод " + ex.getMessage());
            } catch (TokenException ex) {
                if (!ex.getMessage().equals("skip"))
                    System.out.println("Ошибка токена: " + ex.getMessage());
            } catch (ExpressionException | EmptyStackException ex) {
                System.out.println("Ошибка в процессе вычисления: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Непредвиденная ошибка: " + ex.getMessage());
            } finally {
                TokenMatrix.clearValues();
            }
        }
    }
}