package commandManager;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.callers.ExternalArgumentReceiverCaller;
import commandLogic.commandReceiverLogic.callers.ExternalArgumentReceiverCallerO;
import commandLogic.commandReceiverLogic.callers.ExternalArgumentReceiverCallerP;
import commandLogic.commandReceiverLogic.callers.ExternalBaseReceiverCaller;
import products.Organization;
import products.Product;

import java.util.ArrayList;

public class CommandExporter {


    public static ArrayList<CommandDescription> getCommandsToExport() {


        ArrayList<CommandDescription> commands = new ArrayList<>();


        commands.add(new CommandDescription("help", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("info", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("show", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("filter_starts_with_part_number", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("filter_by_part_number", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("generate", new ExternalBaseReceiverCaller()));


        commands.add(new CommandDescription("update", new ExternalArgumentReceiverCallerP<>()));
        commands.add(new CommandDescription("clear", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("execute_script", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("exit", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("history", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("remove_by_id", new ExternalBaseReceiverCaller()));
        commands.add(new CommandDescription("add", new ExternalArgumentReceiverCallerP<>()));
        commands.add(new CommandDescription("add_if_min", new ExternalArgumentReceiverCallerP<>()));
        commands.add(new CommandDescription("remove_all_by_manufacturer", new ExternalArgumentReceiverCallerO<>()));

        return commands;
    }
}
