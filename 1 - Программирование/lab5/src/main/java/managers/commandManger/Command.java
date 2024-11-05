package managers.commandManger;

import exceptions.BuildObjectException;

/**
 *Абстрактный класс для создания команд на его основе.
 */
public abstract class  Command implements CommandInterface{

    /**
     * True если у переменной должен быть аргумент. False если нет.
     */
    private  final boolean hasArgumet;

    /**
     * Аргумент функции
     */
    private Object argument;

    /**
     * Конструктор задающий есть ли в команде аргумент.
     * @param hasArgumet Есть ли аргумент
     */
    public Command(boolean hasArgumet){
        this.hasArgumet = hasArgumet;
    }

    /**
     *
     * @throws BuildObjectException
     */
    @Override
    public void execute() throws BuildObjectException {

    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * Проверяет переданный аргумент.
     * @param argument аргумент команды.
     * @return возвращает true, если
     */
    @Override
    public boolean checkArgument(Object argument) {
        return false;
    }

    /**
     * Проверяет должен ли быть в команде аргумент.
     * @return True если у переменной должен быть аргумент. False если нет.
     */
    public  boolean isHasArgumet(){
        return hasArgumet;
    }

    /**
     * Возвращает аргумент.
     * @return аргумент команды.
     */
    public Object getArgument(){
        return argument;
    }

    /**
     * Задает аргумент.
     * @param argument Аргумент, который мы хотим задать команде.
     */
    public  void setArgument(String argument){
        this.argument = argument;
    }
}
