package commandManager.commands;

import client.ClientHandler;
import responses.CommandStatusResponse;

public interface BaseCommand {


    String getName();


    String getDescr();


    default String getArgs() {
        return "";
    }


    void execute(String[] args) throws IllegalArgumentException;


    CommandStatusResponse getResponse();

    void setClient(ClientHandler clientManager);
}
