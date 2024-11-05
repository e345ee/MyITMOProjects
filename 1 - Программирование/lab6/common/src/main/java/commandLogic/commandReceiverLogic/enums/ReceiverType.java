package commandLogic.commandReceiverLogic.enums;

//Определяет типы обработчиков команд:
//NoArgs: для команд без аргументов.
//ArgumentRoute: общие команды с аргументом.
//ArgumentRouteP: команды с аргументами типа Product.
//ArgumentRouteO: команды с аргументами типа Organization.
public enum ReceiverType {
    NoArgs,
    ArgumentRoute,
    ArgumentRouteP,
    ArgumentRouteO
}
