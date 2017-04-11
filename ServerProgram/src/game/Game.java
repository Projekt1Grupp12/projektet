package game;

import java.io.IOException;

import core.UDPServer;

public abstract class Game {
	private Player[] players;
	
	private int fullScreen;
	
	private UDPServer server;
	
	public Game(Player[] player) {
		
	}
	
	public int setupFullScreen() {
	    String tmp = "";
	    
	    for(int i = 0; i < players.length; i++) {
	    	tmp += Integer.toBinaryString(players[i].getScreen());
	    }
	    
	    fullScreen = Integer.parseInt(tmp, 2);
	    
		return fullScreen;
	}
	
	public void flushFullScreen() throws IOException {
		fullScreen = 0;
		server.sendToArdurino("00");
	}
	
	public abstract void sendBadFeedback(Player player);
	
	public abstract void sendGoodFeedback(Player player);
	
	public abstract void update() throws IOException;
	
	public boolean redPressed() {
		return false;
	}
	
	public boolean greenPressed() {
		return false;
	}
	
	public boolean bluePressed() {
		return false;
	}
	
	public Player[] getPlayers() {
		return players;
	}
}
