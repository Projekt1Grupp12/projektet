package game;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import core.BetterRandom;
import core.UDPServer;

/**
 * Puzzle game that inherints the game class
 * @author tom.leonardsson
 *
 */
public class PuzzelGame extends Game {
	private int delay;
	private int maxDelay;
	private int levelCount;
	private int level;
	
	private boolean hasStarted;
	private boolean startButtonBlink = true;
	
	Random random = new Random();
	
	/**
	 * create a puzzel game with a set of players and server
	 * @param players the set of players
	 * @param server the server
	 */
	public PuzzelGame(Player[] players, UDPServer server) {
		super(players, server);
		maxDelay = 128*2;

		setMaxScore(1);
	}
	
	/**
	 * Send bad move and the score to the player
	 * @param player the player to send bad feedback to
	 */
	public void sendBadFeedback(int index) throws IOException {
		sendToPhone("BAD MOVE! " +  getPlayer(index).getScore(), index);
	}

	/**
	 * Raise the player score, send good move and score and move the engine one step
	 * @param player the player to send good feedback to
	 */
	public void sendGoodFeedback(int index) throws IOException {
		getPlayer(index).addScore();
		sendToPhone("GOOD MOVE! " +  getPlayer(index).getScore(), index);
		takeProgressStep(index);
	}
	
	/**
	 * Check if the player has done a move, i.e press a light that is lit up, send badfeed back if anything else is pressed
	 * @param player the player to check
	 * @return if the player has pressed a lit up color
	 */
	public boolean checkGoodInput(int index) {
		for(int i = 0; i < getPlayer(index).lightsOn().length; i++) {
			if(getPlayer(index).lightsOn()[i] && colorsPressed(index)[i] && !getPlayer(index).getColorsPressed()[i]) {
				getPlayer(index).setAmountPressed(getPlayer(index).getAmountPressed()+1);
				getPlayer(index).setColorsPressed(true, i);
				getPlayer(index).clearMaskBit(i);
				try {
					sendToArdurino(setupFullScreen() + "");
					sendGoodFeedback(index);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if(!getPlayer(index).lightsOn()[i] && colorsPressed(index)[i] || colorsPressed(index)[i] && getPlayer(index).getColorsPressed()[i]) {
					try {
						sendBadFeedback(index);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return getPlayer(index).getAmountPressed() == getPlayer(index).amountLightsOn() && getPlayer(index).amountLightsOn() != 0;
	}
	
	/**
	 * Give both players new lights to press and update the screen
	 * @throws IOException
	 */
	public void changeLights() throws IOException {
		flushFullScreen();

		int[] amountToLightUp = new int[getPlayerAmount()];
		
		for(int i = 0; i < amountToLightUp.length; i++) {
			amountToLightUp[i] = random.nextInt(2)+1;
		}
		
		for(int i = 0; i < getPlayerAmount(); i++) {
			getPlayer(i).flushScreen();
			for(int j = 0; j < amountToLightUp[i]; j++) {
				getPlayer(i).setScreenBit(BetterRandom.random(0, 3));
			}
		}
		
		sendToArdurino(setupFullScreen() + "");
	}
	
	/**
	 * update the screen and lights for only one specfic player
	 * @param index the index of the specfic player
	 * @throws IOException
	 */
	public void changeLights(int index) throws IOException {
		sendToArdurino((index == 0) ? "0" + getPlayer(1).getScreen() : getPlayer(0).getScreen() + "0");
		getPlayer(index).flushMask();
		int amountToLightUp = 0;

		amountToLightUp = random.nextInt(2)+1;
		
		getPlayer(index).flushScreen();
		for(int i = 0; i < amountToLightUp; i++) {
			getPlayer(index).setScreenBit(BetterRandom.random(0, 3));
		}
		
		String tmp = "0";
		sendToArdurino(((setupFullScreen() < 10) ? tmp + setupFullScreen() : setupFullScreen()+"") + "");
	}
	
	/**
	 * The game loop that updates the game and takes in input from the server
	 * @param the input from the server
	 */
	public void update(String input) throws IOException {
		setInput(input);
		
		levelCount += 1;
		
		if(levelCount >= level*32) {
			level += 1;
			levelCount = 0;
			maxDelay -= 2;
		}
		
		delay += 1;
		
		if(delay == maxDelay) {
			//changeLights();
			//delay = 0;
		}
		
		if(delay == 1) changeLights();
		
		checkIfWon();
		
		for(int i = 0; i < getPlayerAmount(); i++) {
			if(getPlayer(i).amountLightsOn() == 0) {
				changeLights(i);
				break;
			}
			if(!gameOver && checkGoodInput(i)) {
				getPlayer(i).setAmountPressed(0);
				getPlayer(i).flushColorsPressed();
				changeLights(i);
			}
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the name of the game
	 * @return the name of the game
	 */
	public String getName() {
		return "PuzzleGame";
	}
	
	public void run() {
		
	}

	public void onGameOver() {
		delay = 0;
	}
}
