import java.net.MalformedURLException;
import java.net.SocketOption;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;


/** This is the client of the Tic Tac Toe game. */
public class Client {


    /** The program starts running in the main method. */
    public static void main(String[] args) {
        /* TO DO */
        try{
            TTTInterface ttt = (TTTInterface) Naming.lookup("rmi://localhost:3000/tictactoe");
            int play;

            Scanner keyboardSc = new Scanner(System.in);
            Integer playerID = ttt.registerPlayer();


            System.err.println("\nUser has been registered in server with user ID " + playerID + "\n");
            System.err.println("\nFinding a match\n");

//            while(true) {


                while (!ttt.isGameReady(playerID)) ;
                System.err.println("\nThe game is now ready to play\n");
                boolean gameEnded = false;
                int symbol = ttt.getSymbol(playerID);

                System.err.printf("\nYou will play with %c\n", symbol == 1 ? 'X' : 'O');
                System.err.println("\n'X' plays first\n");

                System.out.println("\nInitial board:");
                System.out.println(ttt.currentBoard(playerID));

                while (true) {

                    ttt.checkWinner(playerID);
                    boolean waitingMsgFlag = true;
                    while (ttt.whoseTurn(playerID) != symbol && ttt.checkStatus(playerID) == -1) {
                        if (waitingMsgFlag) {
                            System.out.println("\nWaiting for opponent's turn ...\n");
                            waitingMsgFlag = false;
                        }
                    }
                    ;

                    System.out.println("\nCurrent board after opponent's turn:");
                    System.out.println(ttt.currentBoard(playerID));

                    ttt.checkWinner(playerID);

                    if (ttt.checkStatus(playerID) == -1) {
                        /* Read Input */
                        do {
                            System.out.printf(
                                    "\nPlayer %d, please enter the number of the square "
                                            + "where you want to place your %c : \n>> ",
                                    symbol, (symbol == 1) ? 'X' : 'O');
                            play = keyboardSc.nextInt();
                        } while (play > 9 || play < 0);
                        while (!ttt.play(play, playerID, symbol))
                            System.out.println("\nPlease enter a valid empty position\n");
                        System.out.println("\nCurrent board after your turn:");
                        System.out.println(ttt.currentBoard(playerID));
                    } else break;

                }

                int status = ttt.checkStatus(playerID);
                if (status == symbol)
                    System.out.println("\nCongratulations, YOU ARE THE WINNER!\n");
                else if (status == 0)
                    System.out.println("\nIt's a draw!\n");
                else
                    System.out.println("\nBad luck! You have lost the game!\n");

//                System.out.println("\nDo you want to play another game with the opponent?\n" +
//                        "Enter 1 for another game and 0 to quit");
//                int choice = keyboardSc.nextInt();
//            }
        }
        catch (MalformedURLException e){ e.printStackTrace();}
        catch (RemoteException e){ e.printStackTrace();}
        catch (NotBoundException e){ e.printStackTrace();}
    }

}
