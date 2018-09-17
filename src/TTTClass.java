import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

class Game{
	Integer player1;
	Integer player2;
	int whoseTurn;
	int numPlays;
	char board[][] = {
			{ '1', '2', '3' }, /* Initial values are reference numbers */
			{ '4', '5', '6' }, /* used to select a vacant square for */
			{ '7', '8', '9' } /* a turn. */
	};
	boolean ready;
	// Checking if draw, someone is winner or game is still not finished
	// 1/2 : winner, 0 : draw, -1 : game on
	int currentState;
	boolean timeout;

	//constructor
	public Game(Integer player) {
		this.player1 = player;
		this.player2 = 0;
		this.numPlays = 0;
		this.whoseTurn = 1;
		this.currentState = -1;
		this.ready = false;
		this.timeout = false;

	}

	//adding another player to the game
	public void addPlayer2(Integer player) {
		this.player2 = player;
	}

	//assign player 1/2
	public void assignPlayerRandomly() {
		int random = (int )(Math.random() * 50 + 1);
		if (random % 2 == 0) {
			Integer temp = this.player1;
			this.player1 = this.player2;
			this.player2 = temp;
		}
		this.ready = true;
	}

	//changing the state of the game
	public boolean playGame(int pos, int whoPlaying) {
		if ((pos > 9 && pos < 1)
				|| this.board[pos%3 == 0 ? pos/3 - 1 : pos/3][pos%3 == 0 ? 2 : pos%3 - 1] == 'X'
				|| this.board[pos%3 == 0 ? pos/3 - 1 : pos/3][pos%3 == 0 ? 2 : pos%3 - 1] == 'O')
			return false;
		this.board[pos%3 == 0 ? pos/3 - 1 : pos/3][pos%3 == 0 ? 2 : pos%3 - 1] = whoPlaying == 1 ? 'X' : 'O';
		this.whoseTurn = whoPlaying == 1 ? 2 : 1;
		this.numPlays += 1;
		return true;
	}

	//checking the game status
	public synchronized void checkWinner() throws RemoteException {
		int i;
		/* Check for a winning line - diagonals first */
		if ((this.board[0][0] == this.board[1][1] && this.board[0][0] == this.board[2][2])
				|| (this.board[0][2] == this.board[1][1] && this.board[0][2] == this.board[2][0])) {
			if (this.board[1][1] == 'X') {
				this.currentState = 1;
				return;
			}
			else {
				this.currentState = 2;
				return;
			}

		}
		else
			/* Check rows and columns for a winning line */
			for (i = 0; i <= 2; i++) {
				if ((this.board[i][0] == this.board[i][1] && this.board[i][0] == this.board[i][2])) {
					if (this.board[i][0] == 'X') {
						this.currentState = 1;
						return;
					}
					else {
						this.currentState = 2;
						return;
					}
				}

				if ((this.board[0][i] == this.board[1][i] && this.board[0][i] == this.board[2][i])) {
					if (this.board[0][i] == 'X') {
						this.currentState = 1;
						return;
					}
					else {
						this.currentState = 2;
						return;
					}
				}
			}
		if (this.numPlays == 9)
			this.currentState = 0; /* A draw! */
		else
			this.currentState = -1; /* Game is not over yet */
	}

}

public class TTTClass extends UnicastRemoteObject implements TTTInterface {

	HashMap<Integer, Game> user2game = new HashMap<>();

	Queue<Game> gameQueue = new LinkedList<>();

	Integer userIDCounter = 0;


	public TTTClass() throws RemoteException{
		super();
	}

	/** Registers a new player and assigns her a new/existing game. **/
	@Override
	public Integer registerPlayer() throws RemoteException {
		userIDCounter++;
		if (gameQueue.isEmpty()) {
			Game newGame = new Game(userIDCounter);
			user2game.put(userIDCounter, newGame);
			gameQueue.add(newGame);
		}
		else {
			Game createdGame = gameQueue.remove();
			createdGame.addPlayer2(userIDCounter);
			createdGame.assignPlayerRandomly();
			user2game.put(userIDCounter, createdGame);
		}
		return userIDCounter;
	}

	/** Checking if the game for the current user is ready **/
	@Override
	public boolean isGameReady(Integer player) throws  RemoteException {
		return user2game.get(player).ready;
	}

	/** Checking if the user **/
	@Override
	public int getSymbol(Integer player) throws  RemoteException {
		return (user2game.get(player).player1.equals(player)) ? 1 : 2;
	}

	/** Checking whose turn in the game **/
	@Override
	public int whoseTurn(Integer player) throws  RemoteException {
		return user2game.get(player).whoseTurn;
	}

	/** Return a textual representation of the current game board. */
	@Override
	public String currentBoard(Integer player) throws RemoteException{
		Game currentGame = user2game.get(player);
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n ");

		// acquire lock for current object
		synchronized (this) {
			sb.append(currentGame.board[0][0]).append(" | ");
			sb.append(currentGame.board[0][1]).append(" | ");
			sb.append(currentGame.board[0][2]).append(" ");
			sb.append("\n---+---+---\n ");
			sb.append(currentGame.board[1][0]).append(" | ");
			sb.append(currentGame.board[1][1]).append(" | ");
			sb.append(currentGame.board[1][2]).append(" ");
			sb.append("\n---+---+---\n ");
			sb.append(currentGame.board[2][0]).append(" | ");
			sb.append(currentGame.board[2][1]).append(" | ");
			sb.append(currentGame.board[2][2]).append(" \n");
		}
		// release lock

		return sb.toString();
	}

	/** Make a game play on behalf of provided player. */
	@Override
	public boolean play(int pos, int player, int whoPlaying) throws RemoteException{
		return user2game.get(player).playGame(pos, whoPlaying);
	}

	/**
	 * Check if there is a game winner. Synchronized keyword means that the lock
	 * of the object is acquired when the method is called and released on
	 * return.
	 */
	@Override
	public void checkWinner(Integer player) throws RemoteException {
		user2game.get(player).checkWinner();
	}

	/** Check the status of the game board **/
	@Override
	public int checkStatus(Integer player) throws RemoteException {
		return user2game.get(player).currentState;
	}


}
