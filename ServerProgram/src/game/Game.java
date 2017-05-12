package game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import core.ServerController;
import core.Timer;
import core.UDPServer;

public abstract class Game implements Runnable {
	public final int MAX_STEPS = 8;
	
	private Player[] players;
	
	private int fullScreen;
	private int winningPlayer;
	private int maxScore;
	private int stepParts;
	private int stepsCount;
	
	private UDPServer server;
	
	private String input;
	
	public boolean realTime;
	
	public boolean closeGame;
	
	public boolean gameOver;
	
	private Timer timer;
	
	private HighscoreList highscoreList;
	
	public Game(Player[] players, UDPServer server) {
		this.players = players;
		this.server = server;
		input = "";
		closeGame = false;
		
		timer = new Timer();
		
		try {
			setupHighscoreFile();
			highscoreList = new HighscoreList(getHighscoreFileName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reset() {
		input = "";
		for(int i = 0; i < players.length; i++) {
			players[i] = new Player(players[i].getId());
		}
		closeGame = false;
		gameOver = false;
		try {
			highscoreList.save(getHighscoreFileName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int setupFullScreen() {
	    String tmp = "";
	    String maskTmp = "";
	    
	    for(int i = 0; i < players.length; i++) {
	    	String pad = Integer.toBinaryString(players[i].getScreen());
	    	pad = (pad.length() == 1) ? "00" + pad : pad;
	    	pad = (pad.length() == 2) ? "0" + pad : pad;
	    	
	    	tmp += pad;
	    }
	    
	    for(int i = 0; i < players.length; i++) {
	    	String pad = Integer.toBinaryString(players[i].getMaskScreen());
	    	pad = (pad.length() == 1) ? "00" + pad : pad;
	    	pad = (pad.length() == 2) ? "0" + pad : pad;
	    	
	    	maskTmp += pad;
	    }
	    
	    int r = Integer.parseInt(tmp, 2);

	    if(!tmp.equals("")) 
	    	fullScreen = r&Integer.parseInt(maskTmp, 2);
	    else
	    	return 0;
	    
		return fullScreen;
	}
	
	public void flushFullScreen() throws IOException {
		fullScreen = 0;
		server.sendToArdurino("00");
	} 
	int x = 0;
	public void takeProgressStep(int index) throws IOException {
		server.sendToArdurino(UDPServer.ENGINE_INSTRUCTION[index]);
		for(int i = 0; i < players.length; i++) {
			if(players[i].amountLightsOn() > 1) {
				stepsCount += 1;
				if(stepsCount % stepParts == 0) {
					x += 1;
					System.out.println((players[index].getScore() % MAX_STEPS) + " | " + players[index].getScore());
					server.sendToArdurino(setupFullScreen() + "");
					stepsCount = 0;
				}
				System.out.println(x);
				break;
			}
		}
	}
	
	public void sendToArdurino(String message) throws IOException {
		server.sendToArdurino(message);
	}
	
	public void sendToPhone(String message, int index) throws IOException {
		server.sendToPhone(message, index);
		if(ServerController.hasCreatedClient) server.sendToClientSimulator(message, index);
	}
	
	public abstract void sendBadFeedback(Player player) throws IOException;
	
	public abstract void sendGoodFeedback(Player player) throws IOException;
	
	public abstract void update(String input) throws IOException;
	
	public abstract boolean checkGoodInput(Player player);
	
	public boolean[] colorsPressed(Player player) {
		return new boolean[]{greenPressed(player), yellowPressed(player), redPressed(player)};
	}
	
	public boolean redPressed(Player player) {
		return input.equals("1;"+player.getId());
	}
	
	public boolean greenPressed(Player player) {
		return input.equals("3;"+player.getId());
	}
	
	public boolean yellowPressed(Player player) {
		return input.equals("2;"+player.getId());
	}
	
	public Player[] getPlayers() {
		return players;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getInput() {
		return input;
	}
	
	public void setTimer() {
		timer.start();
	}
	
	public long endTimer() {
		return timer.end();
	}
	
	public void setWinningPlayer(int winningPlayer) {
		this.winningPlayer = winningPlayer;
	}
	
	public void setGameOver() {
		long result = endTimer();
		if(!gameOver) {
			HighscoreEntry h = new HighscoreEntry(players[winningPlayer].getName(), result + "");
			highscoreList.tryAdd(h);
			server.hasStartedGame = false;
		}
		//server.resetSession();
		gameOver = true;
	}
	
	public void setupHighscoreFile() throws IOException {
		if(!new File(getHighscoreFileName()).exists()) {
			 File file = new File(getHighscoreFileName());
			 file.createNewFile();
		}
	}
	
	public String getHighscoreFileName() {
		String tmp = "";
		for(int i = 0; i < getName().length(); i++) {
			if(getName().charAt(i) != '!') tmp += (getName().charAt(i) == ' ') ? '_' : getName().charAt(i);
		}
		
		return tmp.toLowerCase() + ".txt";
	}
	
	public abstract String getName();
	
	public String toString() {
		return getName();
	}
	
	public int getMaxScore() {
		return maxScore;
	}
	
	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore*MAX_STEPS;
		stepParts = (this.maxScore / MAX_STEPS);
	}
}
