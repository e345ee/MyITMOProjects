package managers.commandManger;

/**
 * Содержит информацию о ввиде поведения команд.
 */
public enum InputMode {
    /**
     * Команды буду использовать ввод пользователя из командной строки.
     */
    USER_INPUT,
    /**
     * Команды будут считываться из файла.
     */
    NON_USER_INPUT
}
