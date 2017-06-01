package game;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.UDPServer;

/**
 * Basic deathmatch game where players shoot eachother
 * @author tom.leonardsson
 *
 */
public class DeathmatchGame extends Game {
	private int[] position;
	private int[] firerate;
	
	/**
	 * create a duel game with a set of players and server
	 * @param players the set of players
	 * @param server the server
	 */
	public DeathmatchGame(Player[] players, UDPServer server) {
		super(players, server);
		realTime = true;
		position = new int[]{0, 0};
		firerate = new int[2];
	}

	/**
	 * send a bad move if shot
	 * @param index the player
	 */
	public void sendBadFeedback(int index) throws IOException {
		sendToPhone("BAD MOVE! " +  getPlayer(index).getScore(), index);
	}

	/**
	 * send a good move if good shot
	 * @param index the player
	 */
	public void sendGoodFeedback(int index) throws IOException {
		getPlayer(index).addScore();
		sendToPhone("GOOD MOVE! " +  getPlayer(index).getScore(), getPlayer(index).getId());
	}
	
	/**
	 * move a players position
	 * @param index the player
	 * @throws IOException
	 */
	public void movePlayer(int index) throws IOException {
		sendToArdurino((index == 0) ? "0" + getPlayer(1).getScreen() : getPlayer(0).getScreen() + "0");
		getPlayer(index).flushScreen();
		getPlayer(index).setScreenBit(position[index]);
	
		String tmp = "0";	
		sendToArdurino(((setupFullScreen() < 10) ? tmp + setupFullScreen() : setupFullScreen()+"") + "");
	}
	
	/**
	 * The game loop
	 * @param input the input from the server
	 */
	public void update(String input) throws IOException {
		setInput(input);
		
		for(int i = 0; i < getPlayerAmount(); i++) {
			if(redPressed(i) && position[i] < 2) {
				position[i] += 1;
				movePlayer(i);
			}
			
			if(greenPressed(i) && position[i] > 0) {
				position[i] -= 1;
				movePlayer(i);
			}
			
			if(checkGoodInput(i)) {
				sendGoodFeedback(i);
				sendBadFeedback(i == 0 ? 1 : 0);
			}
		}
		
		input = "";
		setInput(input);
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if shot player
	 * @param index the player
	 * @return if shot hit
	 */
	public boolean checkGoodInput(int index) {
		if(yellowPressed(index) && position[index] == position[index == 0 ? 1 : 0]) {
			return true;
		}
		
		return false;
	}

	/**
	 * Get name of game
	 * @return the name of the game
	 */
	public String getName() {
		return "DeathmatchGame";
	}
	
	/**
	 * The main loop of the game
	 */
	public void run() {
		try {
			while(!closeGame)
				update(getInput());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * What happens on game over
	 */
	public void onGameOver() {
		
	}
}
