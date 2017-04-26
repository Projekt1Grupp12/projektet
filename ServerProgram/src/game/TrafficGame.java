package game;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.UDPServer;

public class TrafficGame extends Game {
	final int RED = 0, YELLOW = 1, GREEN = 2;
	
	private int lightState = 0;
	
	private int delay;
	private int maxDelay;
	private int levelCount;
	private int currentLevel;
	private int speed;
	private int updateMessageDelay;
	
	private boolean fromRed = true;
	private boolean[] hasPressedYellow;
	
	private String message = "STANDING STILL! ";
	
	public TrafficGame(Player[] players, UDPServer server) {
		super(players, server);
		delay = -1;
		maxDelay = 96;
		hasPressedYellow = new boolean[2];
		for(int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i].setScreenBit(0);
		}
		
		realTime = true;
	}
	
	public void stepLight() {
		lightState += fromRed ? 1 : -1;
		
		for(int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i].flushScreen();
			hasPressedYellow[i] = false;
			
			if(lightState == RED) {
				getPlayers()[i].setScreenBit(0);
				speed = 0;
				fromRed = true;
			}
			
			if(lightState == YELLOW) {
				getPlayers()[i].setScreenBit(1);
				if(fromRed) {
					getPlayers()[i].setScreenBit(0);
				}
				speed = 2;
			}
			
			if(lightState == GREEN) {
				getPlayers()[i].setScreenBit(2);
				fromRed = false;
				speed = 0;
			}
		}
		
	}

	public void sendBadFeedback(Player player) throws IOException {
		sendToPhone("BAD MOVE!" +  player.getScore(), player.getId());
	}

	public void sendGoodFeedback(Player player) throws IOException {
		player.addScore();
		sendToPhone("GOOD MOVE!"  +  player.getScore(), player.getId());
		takeProgressStep(player.getId());
		setInput("");
	}
	
	public boolean checkGoodInput(Player player) {
		boolean condition = (redPressed(player) || yellowPressed(player) || greenPressed(player));
	
		try {
			if(condition && (lightState == GREEN || (lightState == YELLOW && !hasPressedYellow[player.getId()] && fromRed))) {
				if(lightState == YELLOW)
					hasPressedYellow[player.getId()] = true;
				sendGoodFeedback(player);
			}
			else if(condition && (lightState != GREEN || (lightState == YELLOW && hasPressedYellow[player.getId()]) || lightState == RED))
				sendBadFeedback(player);
			else {
				
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		return condition;
	}

	public void update(String input) throws IOException {
		setInput(input);
		
		levelCount += 1;
		
		if(levelCount >= currentLevel*10) {
			currentLevel += 1;
			if(delay > 32)
				delay -= 4;
			levelCount = 0;
		}
		
		if(delay == -1) 
			sendToArdurino(((setupFullScreen() < 10) ? "0" + setupFullScreen() : setupFullScreen()+"") + "");
		delay += 1 + speed;
		
		if(delay >= maxDelay) {
			stepLight();
			sendToArdurino(((setupFullScreen() < 10) ? "0" + setupFullScreen() : setupFullScreen()+"") + "");
			delay = 0;
		}
		
		for(int i = 0; i < getPlayers().length; i++) {
			checkGoodInput(getPlayers()[i]);
			getPlayers()[i].setAmountPressed(0);
			getPlayers()[i].flushColorsPressed();
		}
		
		updateMessageDelay += 1;
		
		try {
			TimeUnit.MILLISECONDS.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return "Traffic Game";
	}

	public void run() {
		try {
			while(true)
				update(getInput());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
