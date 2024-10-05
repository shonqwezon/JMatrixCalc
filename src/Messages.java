public interface Messages {
    // Main
    String HELLO = "Кальулятор матриц!\nДоступные операции: ( ), +, -, *, /, | |, ^T\n";
    String INPUT_EXP = "Введите выражение:";
    String RESULT = "Результат:\n";

    String EX_TOKEN_METHOD = "Ошибка (токен не поддерживает метод): ";
    String EX_TOKEN = "Ошибка токена: ";
    String EX_CALC = "Ошибка в процессе вычисления: ";
    String EX_UNEXPECTED = "Непредвиденная ошибка: ";

    // Expression
    String BAD_EXPRESSION = "Некорректное выражение";
    String UNKNOWN_OPER = "Неизвестный оператор: ";
    String BAD_CAST_REVERSE = "Нельзя траспонировать число";
    String BAD_CAST_DET = "Нельзя найти детерминант у числа";

    // TokenMatrix
    String INPUT_DIM = "\nВведите размерность матрицы '%s':\n";
    String INPUT_FORMAT = "Положительные числа <кол-во строк> <кол-во столбцов> через пробел:";
    String BAD_FORMAT = "Неправильный формат.";
    String INPUT_VALUES = "Введите матрицу '%s' БЕЗ ПРОБЕЛОВ в комплексных числах:\n";
    String BAD_VALUES = "Вы превысили кол-во столбцов или ввели некорректное число. Вводите заново:";

    String EX_DET = "Невозможно вычислить детерминат — матрица не квадратная";
    String EX_BAD_OPERATION = "Операция %s '%c' %s не поддерживается";
    String EX_BAD_DIMS = "Невозможно %s матрицы разных размерностей";
    String EX_DIV_SQUARE = "Невозможно разделить матрицы — они не квадратные";
    String EX_FIV_DET = "Невозможно делить вырожденые матрицы";

    // TokenComplex
    String BAD_NUM = "Некорректное число";
    String BAD_FORMAT_NUM = "Неверный формат числа";
    String DIV_ZERO = "Невозможно разделить на 0";
}