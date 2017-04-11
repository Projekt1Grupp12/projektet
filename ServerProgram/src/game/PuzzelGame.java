package game;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import core.UDPServer;

public class PuzzelGame extends Game {
	private int delay;
	private int maxDelay;
	private int levelCount;
	private int level;
	
	Random random = new Random();
	
	public PuzzelGame(Player[] players, UDPServer server) {
		super(players, server);
		
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
			amountToLightUp[i] = random.nextInt(3)+1;
		}
		
		for(int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i].flushScreen();
			for(int j = 0; j < amountToLightUp[i]; j++) {
				getPlayers()[i].setScreenBit(random.nextInt(4));
			}
		}
		
		sendToArdurino(setupFullScreen() + "");
	}

	public void update() throws IOException {
		levelCount += 1;
		
		if(levelCount >= level*32) {
			level += 1;
			levelCount = 0;
			maxDelay -= 2;
		}
		
		delay += 1;
		
		if(delay == maxDelay) {
			changeLights();
			delay = 0;
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
