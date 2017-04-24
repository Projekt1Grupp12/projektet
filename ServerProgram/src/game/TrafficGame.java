package game;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.UDPServer;

public class TrafficGame extends Game {
	enum Lights {GREEN, YELLOW, RED};
	
	Lights currentLigt;
	
	private boolean fromRed;
	
	private int delay;
	private int maxDelay;
	
	public TrafficGame(Player[] players, UDPServer server) {
		super(players, server);
		currentLigt = Lights.RED;
		fromRed = true;
		
		maxDelay = 128;
	}
	
	public void stepLight(int index) {
		if(currentLigt.equals(Lights.RED)) {
			currentLigt = Lights.YELLOW;
			getPlayers()[index].setScreenBit(0);
			getPlayers()[index].setScreenBit(1);
		}
		
		if(currentLigt.equals(Lights.YELLOW)) {
			currentLigt = fromRed ? Lights.GREEN : Lights.RED;
			getPlayers()[index].setScreenBit((fromRed) ? 2 : 0);
			fromRed = !fromRed;
		}
		
		if(currentLigt.equals(Lights.GREEN)) {
			currentLigt = Lights.YELLOW;
			getPlayers()[index].setScreenBit(1);
		}
	}

	public void sendBadFeedback(Player player) throws IOException {
		
	}

	public void sendGoodFeedback(Player player) throws IOException {
		
	}

	public void update(String input) throws IOException {
		setInput(input);
		
		delay += 1;
		
		if(delay >= maxDelay) {
			for(int i = 0; i < 2; i++)
				stepLight(i);
			sendToArdurino(setupFullScreen() + "");
			delay = 0;
		}
		
		System.out.println(delay);
		
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
