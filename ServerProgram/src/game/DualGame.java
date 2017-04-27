package game;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.BetterRandom;
import core.UDPServer;

public class DualGame extends Game {
	private int state;
	private int delay;
	private int maxDelay;
	
	private boolean shot;

	public DualGame(Player[] players, UDPServer server) {
		super(players, server);
		state = -1;
		realTime = true;
		
		maxDelay = 64;
	}
	
	public void sendBadFeedback(Player player) throws IOException {
		sendToPhone("DEAD! " +  player.getScore(), player.getId());
	}

	public void sendGoodFeedback(Player player) throws IOException {
		sendToPhone("YOU WIN! " +  player.getScore(), player.getId());
	}

	public void update(String input) throws IOException {
		setInput(input);
		
		delay += 1;
		
		if(delay >= maxDelay) {
			if(state >= 3) {
				state = -1;
				for(int i = 0; i < getPlayers().length; i++) {
					getPlayers()[i].flushScreen();
				}
			}
			
			state += 1;
			maxDelay += BetterRandom.random(8, 32);
			delay = 0;
			
			for(int i = 0; i < getPlayers().length; i++) {
				getPlayers()[i].setScreenBit(state);
				sendToArdurino(setupFullScreen() + "");
			}
		}
		
		for(int i = 0; i < getPlayers().length; i++) {
			checkGoodInput(getPlayers()[i]);
			getPlayers()[i].setAmountPressed(0);
			getPlayers()[i].flushColorsPressed();
		}
		
		input = "";
		setInput(input);
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean checkGoodInput(Player player) {
		if(!shot && (redPressed(player) || yellowPressed(player) || greenPressed(player)) && state == 2) {
			shot = true;
			try {
				sendGoodFeedback(player);
				if(player.getId() == 0) {
					sendBadFeedback(getPlayers()[1]);
				} else {
					sendBadFeedback(getPlayers()[0]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		return false;
	}

	public String getName() {
		return "Dual Game";
	}
	
	public void run() {
		try {
			while(!closeGame)
				update(getInput());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
