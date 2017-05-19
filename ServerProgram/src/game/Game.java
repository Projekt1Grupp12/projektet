package game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import core.ServerController;
import core.Timer;
import core.UDPServer;

/**
 * Super class for all games that handels graphics and input and communication
 * @author tom.leonardsson
 *
 */
public abstract class Game implements Runnable {
	public final int MAX_STEPS = 9;
	
	private Player[] players;
	
	private int fullScreen;
	private int winningPlayer;
	private int maxScore;
	private int[] stepParts;
	private int[] stepsCount;
	
	private UDPServer server;
	
	private String input;
	
	public boolean realTime;
	
	public boolean closeGame;
	
	public boolean gameOver;
	
	private Timer timer;
	
	private HighscoreList highscoreList;
	
	/**
	 * Create game with a specfic set of players and server
	 * @param players the players
	 * @param server the server
	 */
	public Game(Player[] players, UDPServer server) {
		this.players = players;
		this.server = server;
		input = "";
		closeGame = false;
		
		stepParts = new int[2];
		stepsCount = new int[2];
		
		timer = new Timer();
		
		try {
			setupHighscoreFile();
			highscoreList = new HighscoreList(getHighscoreFileName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reset the game
	 */
	public void reset() {
		input = "";
		for(int i = 0; i < players.length; i++) {
			players[i] = new Player(players[i].getId());
		}
		closeGame = false;
		gameOver = false;
		try {
			highscoreList.save(getHighscoreFileName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Put togheter the data from the players into a correctly formated
	 * six-bit number
	 * @return the screen to display
	 */
	public int setupFullScreen() {
	    String tmp = "";
	    String maskTmp = "";
	    
	    for(int i = 0; i < players.length; i++) {
	    	String pad = Integer.toBinaryString(players[i].getScreen());
	    	pad = (pad.length() == 1) ? "00" + pad : pad;
	    	pad = (pad.length() == 2) ? "0" + pad : pad;
	    	
	    	tmp += pad;
	    }
	    
	    for(int i = 0; i < players.length; i++) {
	    	String pad = Integer.toBinaryString(players[i].getMaskScreen());
	    	pad = (pad.length() == 1) ? "00" + pad : pad;
	    	pad = (pad.length() == 2) ? "0" + pad : pad;
	    	
	    	maskTmp += pad;
	    }
	    
	    int r = Integer.parseInt(tmp, 2);

	    if(!tmp.equals("")) 
	    	fullScreen = r&Integer.parseInt(maskTmp, 2);
	    else
	    	return 0;
	    
		return fullScreen;
	}
	
	/**
	 * Show nothing on the screen
	 * @throws IOException
	 */
	public void flushFullScreen() throws IOException {
		fullScreen = 0;
		server.sendToArdurino("00");
	} 
	
	/**
	 * Make the engine take a step
	 * @param index the player's engine that takes a step
	 * @throws IOException
	 */
	public void takeProgressStep(int index) throws IOException {
		for(int i = 0; i < players.length; i++) {
			stepsCount[index] += 1;
			if(stepsCount[index] >= maxScore / MAX_STEPS) {
				server.sendToArdurino(UDPServer.ENGINE_INSTRUCTION[index]);
				server.sendToArdurino(setupFullScreen() + "");
				stepsCount[index] = 0;
			}
			break;
		}
	}
	
	/**
	 * Send a message to the embeddedsystem
	 * @param message the message
	 * @throws IOException
	 */
	public void sendToArdurino(String message) throws IOException {
		server.sendToArdurino(message);
	}
	
	/**
	 * send a message to a phone/client
	 * @param message the message
	 * @param index the phone/client that is getting a message
	 * @throws IOException
	 */
	public void sendToPhone(String message, int index) throws IOException {
		server.sendToPhone(message, index);
		if(ServerController.hasCreatedClient && server.isLocalAddress(index)) server.sendToClientSimulator(message, index);
	}
	
	/**
	 * Send bad feedback to a player
	 * @param player the player that is getting the feedback
	 * @throws IOException
	 */
	public abstract void sendBadFeedback(Player player) throws IOException;
	
	/**
	 * Send good feedback to a player
	 * @param player the player that is getting the feedback
	 * @throws IOException
	 */
	public abstract void sendGoodFeedback(Player player) throws IOException;
	
	/**
	 * The main game loop, where game logic is updated
	 * @param input the data that the server recsives
	 * @throws IOException
	 */
	public abstract void update(String input) throws IOException;
	
	/**
	 * Check the input of the player is good and return true if it is
	 * @param player the player to check
	 * @return the players preformance
	 */
	public abstract boolean checkGoodInput(Player player);
	
	/**
	 * Get the colors pressed
	 * @param player the players button to check
	 * @return the pressed colors
	 */
	public boolean[] colorsPressed(Player player) {
		return new boolean[]{greenPressed(player), yellowPressed(player), redPressed(player)};
	}
	
	/**
	 * Check if red button is pressed
	 * @param player the player to check
	 * @return if red is pressed
	 */
	public boolean redPressed(Player player) {
		return input.equals("3;"+player.getId());
	}
	
	/**
	 * Check if green button is pressed
	 * @param player the player to check
	 * @return if green is pressed
	 */
	public boolean greenPressed(Player player) {
		return input.equals("1;"+player.getId());
	}
	
	/**
	 * Check if yellow button is pressed
	 * @param player the player to check
	 * @return if yellow is pressed
	 */
	public boolean yellowPressed(Player player) {
		return input.equals("2;"+player.getId());
	}
	
	/**
	 * Get the players in the game
	 * @return the  players
	 */
	public Player[] getPlayers() {
		return players;
	}
	
	/**
	 * set the input to the game logic(i.e what the server resvices)
	 * @param input the input
	 */
	public void setInput(String input) {
		this.input = input;
	}
	
	/**
	 * get the input to the game logic(i.e what the server recives)
	 * @return
	 */
	public String getInput() {
		return input;
	}
	
	/**
	 * Set the timer to start
	 */
	public void setTimer() {
		timer.start();
	}
	
	/**
	 * end the timer and return the delta
	 * @return the diffrence between start and end
	 */
	public long endTimer() {
		return timer.end();
	}
	
	/**
	 * Set the player who won the game
	 * @param winningPlayer the player who won
	 */
	public void setWinningPlayer(int winningPlayer) {
		this.winningPlayer = winningPlayer;
	}
	
	/**
	 * Check if a player has won, set game over if so
	 */
	public void checkIfWon() {
		for(int i = 0; i < getPlayers().length; i++) {
			if(getPlayers()[i].getScore() >= getMaxScore()-1) {
				winningPlayer = i;
				try {
					sendToPhone("WIN!", i);
					if(i == 0) sendToPhone("LOSE!", 1);
					else sendToPhone("LOSE!", 0);
					onGameOver();
					setGameOver();
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	/**
	 * Method to run on game over
	 */
	public abstract void onGameOver();
	
	/**
	 * Sets the game to game over and update highscore list
	 */
	public void setGameOver() {
		long result = endTimer();
		if(!gameOver) {
			HighscoreEntry h = new HighscoreEntry(players[winningPlayer].getName(), result + "");
			highscoreList.tryAdd(h);
			server.hasStartedGame = false;
			for(int i = 0; i < stepsCount.length; i++)
				stepsCount[i] = 0;
		}
		for(int i = 0; i < players.length; i++)
			players[i].onReset();
		//server.resetSession();
		gameOver = true;
	}
	
	/**
	 * Check if there are files for the highscore lists, else create file for the highscore lists
	 * @throws IOException
	 */
	public void setupHighscoreFile() throws IOException {
		if(!new File(getHighscoreFileName()).exists()) {
			 File file = new File(getHighscoreFileName());
			 file.createNewFile();
		}
	}
	
	/**
	 * Remove spaces and special charcahters from the game name so that it can be used for a file name
	 * @return the game name in file name format
	 */
	public String getHighscoreFileName() {
		String tmp = "";
		for(int i = 0; i < getName().length(); i++) {
			if(getName().charAt(i) != '!') tmp += (getName().charAt(i) == ' ') ? '_' : getName().charAt(i);
		}
		
		return tmp.toLowerCase() + ".txt";
	}
	
	/**
	 * Get the game name, abstract so that every class has to define it's name
	 * @return the name of the game
	 */
	public abstract String getName();
	
	/**
	 * Just returns the name of the game
	 * @return the game name
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * Get the max score to win the game
	 * @return the max score
	 */
	public int getMaxScore() {
		return maxScore;
	}
	
	/**
	 * Set the max score to win the game to a specifc number
	 * @param maxScore the max amount of scores to win
	 */
	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore*MAX_STEPS;
		//stepParts = (this.maxScore / MAX_STEPS);
	}
	
	public String getHighscoreList() {
		return highscoreList.toString();
	}
}
