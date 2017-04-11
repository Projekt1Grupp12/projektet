package game;

import java.io.IOException;
import java.util.Random;

public class PuzzelGame extends Game {
	private int delay;
	private int maxDelay;
	private int levelCount;
	private int level;
	
	Random random = new Random();
	
	public PuzzelGame(Player[] player) {
		super(player);
		
		maxDelay = 128;
	}

	public void sendBadFeedback(Player player) {
		
	}

	public void sendGoodFeedback(Player player) {
		
	}
	
	public void changeLights() throws IOException {
		flushFullScreen();
		
		int[] amountToLightUp = new int[getPlayers().length];
		
		for(int i = 0; i < amountToLightUp.length; i++) {
			amountToLightUp[i] = random.nextInt(4);
		}
		
		for(int i = 0; i < getPlayers().length; i++) {
			for(int j = 0; j < amountToLightUp[i]; j++) {
				getPlayers()[i].setScreenBit(random.nextInt(4));
			}
		}
	}

	public void update() throws IOException {
		levelCount += 1;
		
		if(levelCount >= level*32) {
			level += 1;
			levelCount = 0;
			maxDelay -= 2;
		}
		
		delay += 1;
		
		if(delay >= maxDelay) {
			changeLights();
			delay = 0;
		}
	}
}
