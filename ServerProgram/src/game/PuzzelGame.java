package game;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import core.BetterRandom;
import core.UDPServer;

public class PuzzelGame extends Game {
	private final int MAX_SCORE = 5;
	
	private int delay;
	private int maxDelay;
	private int levelCount;
	private int level;
	
	private boolean hasStarted;
	private boolean startButtonBlink = true;
	
	Random random = new Random();
	
	public PuzzelGame(Player[] players, UDPServer server) {
		super(players, server);
		maxDelay = 128*2;
	}

	public void sendBadFeedback(Player player) throws IOException {
		sendToPhone("BAD MOVE! " +  player.getScore(), player.getId());
	}

	public void sendGoodFeedback(Player player) throws IOException {
		player.addScore();
		sendToPhone("GOOD MOVE! " +  player.getScore(), player.getId());
		takeProgressStep(player.getId());
	}
	
	public boolean checkGoodInput(Player player) {
		for(int i = 0; i < player.lightsOn().length; i++) {
			if(player.lightsOn()[i] && colorsPressed(player)[i] && !player.getColorsPressed()[i]) {
				player.setAmountPressed(player.getAmountPressed()+1);
				player.setColorsPressed(true, i);
				player.clearMaskBit(i);
				try {
					sendToArdurino(setupFullScreen() + "");
					sendGoodFeedback(player);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if(!player.lightsOn()[i] && colorsPressed(player)[i] || colorsPressed(player)[i] && player.getColorsPressed()[i]) {
					try {
						sendBadFeedback(player);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return player.getAmountPressed() == player.amountLightsOn() && player.amountLightsOn() != 0;
	}
	
	public void changeLights() throws IOException {
		flushFullScreen();

		int[] amountToLightUp = new int[getPlayers().length];
		
		for(int i = 0; i < amountToLightUp.length; i++) {
			amountToLightUp[i] = random.nextInt(2)+1;
		}
		
		for(int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i].flushScreen();
			for(int j = 0; j < amountToLightUp[i]; j++) {
				getPlayers()[i].setScreenBit(BetterRandom.random(0, 3));
			}
		}
		
		sendToArdurino(setupFullScreen() + "");
	}
	
	public void changeLights(int index) throws IOException {
		sendToArdurino((index == 0) ? "0" + getPlayers()[1].getScreen() : getPlayers()[0].getScreen() + "0");
		getPlayers()[index].flushMask();
		int amountToLightUp = 0;

		amountToLightUp = random.nextInt(2)+1;
		
		getPlayers()[index].flushScreen();
		for(int i = 0; i < amountToLightUp; i++) {
			getPlayers()[index].setScreenBit(BetterRandom.random(0, 3));
		}
		
		String tmp = "0";
		sendToArdurino(((setupFullScreen() < 10) ? tmp + setupFullScreen() : setupFullScreen()+"") + "");
	}
	
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
			delay = 0;
		}
		
		for(int i = 0; i < getPlayers().length; i++) {
			if(getPlayers()[i].getScore() >= MAX_SCORE-1) {
				sendToPhone("WIN!", i);
				if(i == 0) sendToPhone("LOSE!", 1);
				else sendToPhone("LOSE!", 0);
				setGameOver();
			}
			
			if(getPlayers()[i].amountLightsOn() == 0) {
				changeLights(i);
				break;
			}
			if(!gameOver && checkGoodInput(getPlayers()[i])) {
				getPlayers()[i].setAmountPressed(0);
				getPlayers()[i].flushColorsPressed();
				changeLights(i);
			}
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return "Puzzel Game";
	}

	public void run() {
		
	}
}
