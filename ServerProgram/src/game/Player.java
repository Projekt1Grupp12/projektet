package game;

public class Player {
	public final int R = 0, G = 1, B = 2;
	
	private int score;
	private int screen;
	private int amountPressed;
	
	private boolean[] colorsPressed;
	
	public Player() {
		flushColorsPressed();
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
	
	public int amountLightsOn() {
		int count = 0;
		
		for(int i = 0; i < lightsOn().length; i++){
			count += (lightsOn()[i]) ? 1 : 0;
		}
		
		return count;
	}
	
	public boolean[] lightsOn() {
		boolean[] lights = new boolean[3];
		
		for(int i = 0; i < lights.length; i++)
			lights[i] = (screen & (1L << i)) != 0;
		
		return lights;
	}
	
	public int getScreen() {
		return screen;
	}
	
	public int getAmountPressed() {
		return amountPressed;
	}
	
	public void setAmountPressed(int amountPressed) {
		this.amountPressed = amountPressed;
	}
	
	public void flushColorsPressed() {
		colorsPressed = new boolean[3];
	}
	
	public void setColorsPressed(boolean colorPressed, int index) {
		colorsPressed[index] = colorPressed;
	}
	
	public boolean[] getColorsPressed() {
		return colorsPressed;
	}
}
