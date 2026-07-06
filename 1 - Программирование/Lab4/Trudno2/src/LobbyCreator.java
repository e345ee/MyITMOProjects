import MyNewException.ManAlreadyInLobby;
import MyNewException.NoSuchManInLobby;

public interface LobbyCreator {
    void setLobby(Abs_Human... obj) throws ManAlreadyInLobby;
    void addGuy(Abs_Human... obj) throws ManAlreadyInLobby;
    void showLobby();
    void removeGuy(Abs_Human obj) throws NoSuchManInLobby;
}
