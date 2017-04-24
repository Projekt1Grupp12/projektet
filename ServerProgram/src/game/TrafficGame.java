package game;

import java.io.IOException;

import core.UDPServer;

public class TrafficGame extends Game {
	enum Lights {GREEN, YELLOW, RED};
	
	Lights currentLigt;
	
	private boolean fromRed;
	
	public TrafficGame(Player[] players, UDPServer server) {
		super(players, server);
		currentLigt = Lights.RED;
		fromRed = true;
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
	}

	public boolean checkGoodInput(Player player) {
		return false;
	}
}
