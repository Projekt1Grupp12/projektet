package game;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.UDPServer;

/**
 * A button-masher based on a traffic light that inherints from the game class
 * @author tom.leonardsson
 *
 */
public class TrafficGame extends Game {
	final int RED = 0, YELLOW = 1, GREEN = 2;
	
	private int lightState = 0;
	
	private int delay;
	private int maxDelay;
	private int levelCount;
	private int currentLevel;
	private int speed;
	private int updateMessageDelay;
	
	private boolean fromRed = true;
	private boolean[] hasPressedYellow;
	
	private String message = "STANDING STILL! ";
	
	/**
	 * create a puzzel game with a set of players and server
	 * @param players the set of players
	 * @param server the server
	 */
	public TrafficGame(Player[] players, UDPServer server) {
		super(players, server);
		delay = -1;
		maxDelay = 96;
		hasPressedYellow = new boolean[2];
		for(int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i].setScreenBit(0);
		}
		
		realTime = true;
		
		setMaxScore(4);
	}
	
	/**
	 * Update the light from {red} to {red, yellow} to {green} to {yellow} and then back to the start. 
	 */
	public void stepLight() {
		lightState += fromRed ? 1 : -1;
		
		for(int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i].flushScreen();
			hasPressedYellow[i] = false;
			
			if(lightState == RED) {
				getPlayers()[i].setScreenBit(0);
				speed = 0;
				fromRed = true;
			}
			
			if(lightState == YELLOW) {
				getPlayers()[i].setScreenBit(1);
				if(fromRed) {
					getPlayers()[i].setScreenBit(0);
				}
				speed = 2;
			}
			
			if(lightState == GREEN) {
				getPlayers()[i].setScreenBit(2);
				fromRed = false;
				speed = 0;
			}
		}
		
	}
	
	/**
	 * Send bad move and the score to the player
	 * @param player the player to send bad feedback to
	 */
	public void sendBadFeedback(Player player) throws IOException {
		sendToPhone("BAD MOVE!" +  player.getScore(), player.getId());
	}
	
	/**
	 * Raise the player score, send good move and score and move the engine one step and clear the input so that
	 * the player doesn't continuesly press after not pressing
	 * @param player the player to send good feedback to
	 */
	public void sendGoodFeedback(Player player) throws IOException {
		player.addScore();
		sendToPhone("GOOD MOVE!"  +  player.getScore(), player.getId());
		takeProgressStep(player.getId());
		setInput("");
	}
	
	/**
	 * Check if player is pressing when it's green or once when its {yellow, red} or if they are pressing
	 * at the wrong time
	 * @param the player to check
	 * @return if the player did good
	 */
	public boolean checkGoodInput(Player player) {
		boolean condition = (redPressed(player) || yellowPressed(player) || greenPressed(player));
	
		try {
			if(condition && (lightState == GREEN || (lightState == YELLOW && !hasPressedYellow[player.getId()] && fromRed))) {
				if(lightState == YELLOW)
					hasPressedYellow[player.getId()] = true;
				sendGoodFeedback(player);
			}
			else if(condition && (lightState != GREEN || (lightState == YELLOW && hasPressedYellow[player.getId()]) || lightState == RED))
				sendBadFeedback(player);
			else {
				
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		return condition;
	}
	
	/**
	 * The game loop that updates the game and takes in input from the server
	 * @param the input from the server
	 */
	public void update(String input) throws IOException {
		setInput(input);
		
		levelCount += 1;
		
		if(levelCount >= currentLevel*10) {
			currentLevel += 1;
			if(delay > 32)
				delay -= 4;
			levelCount = 0;
		}
		
		if(delay == -1) 
			sendToArdurino(((setupFullScreen() < 10) ? "0" + setupFullScreen() : setupFullScreen()+"") + "");
		delay += 1 + speed;
		
		if(delay >= maxDelay) {
			stepLight();
			sendToArdurino(((setupFullScreen() < 10) ? "0" + setupFullScreen() : setupFullScreen()+"") + "");
			delay = 0;
		}
		
		for(int i = 0; i < getPlayers().length; i++) {
			if(getPlayers()[i].getScore() >= getMaxScore()-1) {
				sendToPhone("WIN!", i);
				if(i == 0) sendToPhone("LOSE!", 1);
				else sendToPhone("LOSE!", 0);
				setGameOver();
			}
			
			checkGoodInput(getPlayers()[i]);
			getPlayers()[i].setAmountPressed(0);
			getPlayers()[i].flushColorsPressed();
		}
		
		updateMessageDelay += 1;
		
		try {
			TimeUnit.MILLISECONDS.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the game name
	 * @return the game name
	 */
	public String getName() {
		return "Traffic Game";
	}
	
	/**
	 * Run the game loop in a thread
	 */
	public void run() {
		try {
			while(!closeGame)
				update(getInput());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
