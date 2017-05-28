package game;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.BetterRandom;
import core.UDPServer;

/**
 * Duel game that inherints from the game class
 * @author tom.leonardsson
 *
 */
public class DualGame extends Game {
	private int state;
	private int delay;
	private int maxDelay;
	private int lightUp;
	
	private boolean shot;
	
	private boolean blink;
	
	/**
	 * create a duel game with a set of players and server
	 * @param players the set of players
	 * @param server the server
	 */
	public DualGame(Player[] players, UDPServer server) {
		super(players, server);
		state = -1;
		realTime = true;
		
		maxDelay = 128+96;
		
		setMaxScore(1);
	}
	
	/**
	 * Send bad move and the score to the player
	 * @param index the player to send bad feedback to
	 */
	public void sendBadFeedback(int index) throws IOException {
		sendToPhone("BAD MOVE! " +  getPlayer(index).getScore(), index);
	}

	/**
	 * Raise the player score, send good move and score and move the engine one step
	 * @param index the player to send good feedback to
	 */
	public void sendGoodFeedback(int index) throws IOException {
		sendToPhone("GOOD MOVE! " +  getPlayer(index).getScore(), index);
	}
	
	/**
	 * update the game loop, blink the lights and light up the "target"
	 * @param the input from the server
	 */
	public void update(String input) throws IOException {
		setInput(input);
		
		delay += 1;
		
		if(delay >= maxDelay) {
			if(state >= 3) {
				shot = false;
				state = -1;
				for(int i = 0; i < getPlayerAmount(); i++) {
					getPlayer(i).flushScreen();
				}
				maxDelay = 128+96;
			}
			
			state += 1;
			delay = 0;
			
			lightUp = BetterRandom.random(0, 3);
			for(int i = 0; i < getPlayerAmount(); i++) {
				if(state == 2) {
					getPlayer(i).setScreenBit(lightUp); 
					sendToArdurino(setupFullScreen() + "");
				}
				else {
					sendToArdurino(blink ? "63" : "00");
					blink = !blink;
				}
			}
		}
		
		checkIfWon();
		
		for(int i = 0; i < getPlayerAmount(); i++) {
			checkGoodInput(i);
			getPlayer(i).setAmountPressed(0);
			getPlayer(i).flushColorsPressed();
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
	 * Check which player shot who first
	 * @param index the player to check
	 * @return if the player hit the other or if the miss fired
	 */
	public boolean checkGoodInput(int index) {
		if(!shot && (getPlayer(index).lightsOn()[lightUp] && colorsPressed(index)[lightUp])  && state == 2) {
			shot = true;
			try {
				sendGoodFeedback(index);
				if(getPlayer(index).getId() == 0) {
					sendBadFeedback(1);
				} else {
					sendBadFeedback(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		if((redPressed(index) || yellowPressed(index) || greenPressed(index)) && state != 2) {
			try {
				sendBadFeedback(index);
				if(getPlayer(index).getId() == 0) {
					sendGoodFeedback(1);
				} else {
					sendGoodFeedback(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

	/**
	 * Get the name of the game
	 * @return the name 
	 */
	public String getName() {
		return "DuelGame";
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

	@Override
	public void onGameOver() {
		// TODO Auto-generated method stub
		
	}
}
