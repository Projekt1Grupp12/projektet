package game;

public abstract class Game {
	public Player[] players;
	
	public Game(Player[] player) {
		
	}
	
	public abstract void sendBadFeedback(Player player);
	
	public abstract void sendGoodFeedback(Player player);
	
	public abstract void update();
	
	public int getLitLights() {
		return -1;
	}
	
	public boolean redPressed() {
		return false;
	}
	
	public boolean greenPressed() {
		return false;
	}
	
	public boolean bluePressed() {
		return false;
	}
}
