package game;

import java.io.IOException;

import core.UDPServer;

public abstract class Game {
	private Player[] players;
	
	private int fullScreen;
	
	private UDPServer server;
	
	public Game(Player[] players, UDPServer server) {
		this.players = players;
		this.server = server;
	}
	
	public int setupFullScreen() {
	    String tmp = "";
	    
	    for(int i = 0; i < players.length; i++) {
	    	String pad = Integer.toBinaryString(players[i].getScreen());
	    	pad = (pad.length() == 1) ? "00" + pad : pad;
	    	pad = (pad.length() == 2) ? "0" + pad : pad;
	    	
	    	tmp += pad;
	    }
	    
	    if(!tmp.equals("")) 
	    	fullScreen = Integer.parseInt(tmp, 2);
	    else
	    	return 0;
	    
		return fullScreen;
	}
	
	public void flushFullScreen() throws IOException {
		fullScreen = 0;
		server.sendToArdurino("00");
	}
	
	public void sendToArdurino(String message) throws IOException {
		server.sendToArdurino(message);
	}
	
	public abstract void sendBadFeedback(Player player);
	
	public abstract void sendGoodFeedback(Player player);
	
	public abstract void update() throws IOException;
	
	public abstract boolean checkGoodInput(Player player);
	
	public boolean[] colorsPressed(Player player) {
		return new boolean[]{redPressed(player), greenPressed(player), redPressed(player)};
	}
	
	public boolean redPressed(Player player) {
		return false;
	}
	
	public boolean greenPressed(Player player) {
		return false;
	}
	
	public boolean bluePressed(Player player) {
		return false;
	}
	
	public Player[] getPlayers() {
		return players;
	}
}
