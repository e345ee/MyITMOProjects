package requests;

import commandLogic.CommandDescription;

//Представляет клиентский запрос на выполнение команды.
public class CommandClientRequest extends AbsRequest {
    private final CommandDescription command;
    private final String[] lineArgs;

    public CommandClientRequest(CommandDescription command, String[] lineArgs) {
        this.command = command;
        this.lineArgs = lineArgs;
    }

    public CommandDescription getCommandDescription() {
        return command;
    }

    public String[] getLineArgs() {
        return lineArgs;
    }
}