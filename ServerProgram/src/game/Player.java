package game;

public class Player {
	private int score;
	
	private int screen;
	
	public Player() {
		
	}
	
	public void addScore() {
		score += 1;
	}
	
	public void setScreenBit(int index) {
		screen |= 1 << index;
	}
	
	public void clearScreenBit(int index) {
		screen &= ~(1 << index);
	}
	
	public void flushScreen() {
		screen = 0;
	}
	
	public int getScreen() {
		return screen;
	}
}
