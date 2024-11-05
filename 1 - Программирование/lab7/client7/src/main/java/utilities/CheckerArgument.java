package utilities;

import exceptions.WrongAmountOfArgumentsException;

public class CheckerArgument {
    public static void checkArgumentsOrThrow(int provided, int expected) throws WrongAmountOfArgumentsException {
        if (provided - 1 != expected)
            throw new WrongAmountOfArgumentsException("Предоставленные " + (provided - 1) + " аргументы, ожидалось " + expected);
    }
}
