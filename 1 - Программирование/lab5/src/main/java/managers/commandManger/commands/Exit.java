package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;
import managers.commandManger.InputMode;

/**
 * Класс представляющий реализацию команды выхода из приложения.
 */
public class Exit extends Command {
    InputMode mode;

    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public Exit(InputMode mode){
        super(false);
        this.mode = mode;
    }

    public Exit() {
        super(false);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName(){
        return "exit";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription(){
        return "Выход из приложения без сохранения.";
    }


    /**
     * Выходит из приложения.
     */
    @Override
    public void execute(){
        if(checkArgument(getArgument())){
            System.out.println("Выход...");
            System.exit(0);
        }
    }

    /**
     * Проверяет переданный аргумент.
     * @param inputArgument аргумент команды.
     * @return возвращает true, если
     */
    @Override
    public boolean checkArgument(Object inputArgument) {
        if (!(this.mode == InputMode.USER_INPUT)){
            System.out.println("Команда не поддерживается в " + mode.toString());
            return false;
        }
        if (inputArgument == null )
            return true;
        else {
            System.out.println("Exit не имеет аргументов!");
            return false;
        }
    }

}
