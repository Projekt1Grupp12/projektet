package game;

import java.io.IOException;

import core.UDPServer;

public abstract class Game {
	private Player[] players;
	
	private int fullScreen;
	
	private UDPServer server;
	
	private String input;
	
	public Game(Player[] players, UDPServer server) {
		this.players = players;
		this.server = server;
		input = "";
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
	
	public abstract void update(String input) throws IOException;
	
	public abstract boolean checkGoodInput(Player player);
	
	public boolean[] colorsPressed(Player player) {
		return new boolean[]{greenPressed(player), yellowPressed(player), redPressed(player)};
	}
	
	public boolean redPressed(Player player) {
		return input.equals("3"+player.getId());
	}
	
	public boolean greenPressed(Player player) {
		return input.equals("1"+player.getId());
	}
	
	public boolean yellowPressed(Player player) {
		return input.equals("2"+player.getId());
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
}
