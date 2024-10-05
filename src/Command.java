public enum Command {
    EXIT("exit", "Выход из калькулятора"),
    SKIP("skip", "Сбросить ввод выражения и начать сначала");

    private final String command;
    private final String description;

    public static void printAll() {
        System.out.println("Доступные команды:");
        for (Command cmd : values())
            System.out.printf("%s — %s\n", cmd, cmd.description);
        System.out.println();
    }

    Command(String command, String description) {
        this.command = command;
        this.description = description;
    }

    @Override
    public String toString() {
        return command;
    }
}
