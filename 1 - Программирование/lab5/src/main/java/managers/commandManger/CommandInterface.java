package managers.commandManger;

import exceptions.BuildObjectException;

/**
 * Интерфейс для описывающий поведение команды.
 */
public interface CommandInterface {

    /**
     * Метод вызывающий выполнение команду.
     * @throws BuildObjectException Если в построении объекта возникла ошибка.
     */
    void execute() throws BuildObjectException;

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    String getName();

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    String getDescription();

    /**
     * Проверяет переданный аргумент.
     * @param argument аргумент команды.
     * @return возвращает true, если
     */
    boolean checkArgument(Object argument);
}
