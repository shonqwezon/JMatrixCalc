import exceptions.ExpressionException;
import exceptions.MethodNotSupportedException;
import exceptions.TokenException;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Scanner;

public class Main implements Messages, Commands {
    public static void main(String[] args) {
        System.out.println(HELLO);
        Scanner sc = new Scanner(System.in);
        String input;
        Expression expression = new Expression();
        // Run, while input != exit
        while (true) {
            System.out.println(INPUT_EXP);
            input = sc.nextLine();
            if (input.equalsIgnoreCase(EXIT))
                break;
            try {
                // String tokenization
                ArrayList<Token> tokens = Tokenizer.run(input);
                // Printing tokens
//                for (Token token : tokens)
//                    System.out.printf("State: %s\t\t\tName: %s\t\tPriority: %d\n", token.getState(), token.getName(), token.getPriority());

                expression.loadTokens(tokens);
                System.out.println(RESULT + expression.calc().getStringValue());

            } catch (MethodNotSupportedException ex) {
                System.out.println(EX_TOKEN_METHOD + ex.getMessage());
            } catch (TokenException ex) {
                if (!ex.getMessage().equals(SKIP))
                    System.out.println(EX_TOKEN + ex.getMessage());
            } catch (ExpressionException | EmptyStackException ex) {
                System.out.println(EX_CALC + ex.getMessage());
            } catch (Exception ex) {
                System.out.println(EX_UNEXPECTED + ex.getMessage());
            } finally {
                TokenMatrix.clearValues();
            }
        }
    }
}