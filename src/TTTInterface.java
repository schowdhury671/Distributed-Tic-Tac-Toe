import java.rmi.Remote;
import java.rmi.RemoteException;
public interface TTTInterface extends Remote {
    Integer registerPlayer() throws RemoteException;

    boolean isGameReady(Integer player) throws RemoteException;

    int getSymbol(Integer player) throws  RemoteException;

    int whoseTurn(Integer player) throws  RemoteException;

    boolean play(int pos, int player, int whoPlaying) throws RemoteException;

    void checkWinner(Integer player) throws RemoteException;

    int checkStatus(Integer player) throws RemoteException;

    String currentBoard(Integer player) throws RemoteException;

}
