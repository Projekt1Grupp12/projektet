package game;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.UDPServer;

public class TrafficGame extends Game {
	final int RED = 0, YELLOW = 1, GREEN = 2;
	
	private int lightState = 0;
	
	private int delay;
	private int maxDelay;
	
	private boolean fromRed = true;
	
	public TrafficGame(Player[] players, UDPServer server) {
		super(players, server);
		maxDelay = 64;
		
		for(int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i].setScreenBit(0);
		}
	}
	
	public void stepLight() {
		lightState += fromRed ? 1 : -1;
		
		for(int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i].flushScreen();
			
			if(lightState == RED) {
				getPlayers()[i].setScreenBit(0);
				fromRed = true;
			}
			
			if(lightState == YELLOW) {
				getPlayers()[i].setScreenBit(1);
				if(fromRed) {
					getPlayers()[i].setScreenBit(0);
				}
			}
			
			if(lightState == GREEN) {
				getPlayers()[i].setScreenBit(2);
				fromRed = false;
			}
		}
		
	}

	public void sendBadFeedback(Player player) throws IOException {
		
	}

	public void sendGoodFeedback(Player player) throws IOException {
		
	}

	public void update(String input) throws IOException {
		setInput(input);
		delay += 1;
		
		for(int i = 0; i < getPlayers().length; i++)
			sendToPhone("Standing Still!", i);
		
		if(delay >= maxDelay) {
			stepLight();
			sendToArdurino(setupFullScreen() + "");
			delay = 0;
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean checkGoodInput(Player player) {
		return false;
	}
}
