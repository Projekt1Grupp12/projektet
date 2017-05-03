package game;

import java.io.IOException;

import core.ServerController;
import core.Timer;
import core.UDPServer;

public abstract class Game implements Runnable {
	private Player[] players;
	
	private int fullScreen;
	private int winningPlayer;
	
	private UDPServer server;
	
	private String input;
	
	public boolean realTime;
	
	public boolean closeGame;
	
	public boolean gameOver;
	
	private Timer timer;
	
	private HighscoreList highscoreList = new HighscoreList();
	
	public Game(Player[] players, UDPServer server) {
		this.players = players;
		this.server = server;
		input = "";
		closeGame = false;
		
		timer = new Timer();
	}
	
	public void reset() {
		input = "";
		for(int i = 0; i < players.length; i++) {
			players[i] = new Player(players[i].getId());
		}
		closeGame = false;
		gameOver = false;
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
	
	public void takeProgressStep(int index) throws IOException {
		server.sendToArdurino((index == 0) ? "-3" : "-4");
		for(int i = 0; i < players.length; i++) {
			if(players[i].amountLightsOn() > 1) {
				server.sendToArdurino(setupFullScreen() + "");
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
			System.out.println(highscoreList.toString());
		}
		gameOver = true;
	}
	
	public abstract String getName();
	
	public String toString() {
		return getName();
	}
}
