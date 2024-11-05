package managers.commandManger.commands;

import managers.commandManger.Command;

import java.util.LinkedList;

/**
 * Класс представляющий реализацию команды учета и вывода истории вводимых команд.
 */
public class History extends Command {

    /**
     * Размер истории.
     */
    private static final int QUEUE_SIZE = 13;

    /**
     * Список вводимых команд.
     */
    private static LinkedList<String> commandHistory;

    /**
     *  Инициализация истории.
     */
    static {
        commandHistory = new LinkedList<>();
    }


    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public History() {
        super(false);
    }


    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName(){
        return "history";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription(){
        return "Выводит историю вводимых команд.";
    }

    /**
     * Выводит содержимое истории команд без их аргументов.
     */
    @Override
    public void execute() {
        if (checkArgument(getArgument())) {
            int i = 1;
            for (String value : commandHistory) {
                System.out.println(i + ") "+value);
                i++;
            }
        }
    }

    /**
     * Команда добавления считаной команды в историю.
     * @param value команда.
     */
    public static void addToHistory(String value){
        if (commandHistory.size() >= QUEUE_SIZE) {
            commandHistory.removeFirst();
        }
        commandHistory.addLast(value);
    }

    /**
     * Проверяет переданный аргумент.
     * @param inputArgument аргумент команды.
     * @return возвращает true, если
     */
    @Override
    public boolean checkArgument(Object inputArgument) {
        if (inputArgument == null)
            return true;
        else {
            System.out.println("history не имеет аргументов!");

            return false;
        }
    }

}
