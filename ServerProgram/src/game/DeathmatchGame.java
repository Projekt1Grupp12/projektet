package game;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.UDPServer;

public class DeathmatchGame extends Game {
	private int[] position;
	private int[] firerate;
	
	public DeathmatchGame(Player[] players, UDPServer server) {
		super(players, server);
		realTime = true;
		position = new int[]{0, 0};
		firerate = new int[2];
	}

	public void sendBadFeedback(int index) throws IOException {
		sendToPhone("BAD MOVE! " +  getPlayers()[index].getScore(), getPlayers()[index].getId());
	}

	public void sendGoodFeedback(int index) throws IOException {
		getPlayers()[index].addScore();
		sendToPhone("GOOD MOVE! " +  getPlayers()[index].getScore(), getPlayers()[index].getId());
	}
	
	public void movePlayer(int index) throws IOException {
		sendToArdurino((index == 0) ? "0" + getPlayers()[1].getScreen() : getPlayers()[0].getScreen() + "0");
		getPlayers()[index].flushScreen();
		getPlayers()[index].setScreenBit(position[index]);
	
		String tmp = "0";	
		sendToArdurino(((setupFullScreen() < 10) ? tmp + setupFullScreen() : setupFullScreen()+"") + "");
	}
	
	public void update(String input) throws IOException {
		setInput(input);
		
		for(int i = 0; i < getPlayers().length; i++) {
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

	public boolean checkGoodInput(int index) {
		if(yellowPressed(index) && position[index] == position[index == 0 ? 1 : 0]) {
			return true;
		}
		
		return false;
	}

	public String getName() {
		return "Deathmatch Game";
	}
	
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
