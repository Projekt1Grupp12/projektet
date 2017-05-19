package game;

/**
 * Contains the methods and data of the player
 * @author tom.leonardsson
 *
 */
public class Player {
	public final int R = 0, G = 1, B = 2;
	
	private int score;
	private int screen;
	private int maskScreen;
	private int amountPressed;
	
	private int id;
	
	private boolean[] colorsPressed;
	
	private String name;
	
	/**
	 * Create a flushed player with a specfic id
	 * @param id the id
	 */
	public Player(int id) {
		flushColorsPressed();
		this.id = id;
		flushMask();
		
		name = "";
	}
	
	/**
	 * flush the mask so that nothing is hidden
	 */
	public void flushMask() {
		maskScreen = 0b111;
	}
	
	/**
	 * Hide part of screen
	 * @param index index of the part to be hidden
	 */
	public void clearMaskBit(int index) {
		maskScreen &= ~(1 << index);
	}
	
	/**
	 * raise the players score
	 */
	public void addScore() {
		score += 1;
	}
	
	/**
	 * light up a part of the screen
	 * @param index the index of the part of screen
	 */
	public void setScreenBit(int index) {
		screen |= 1 << index;
	}
	
	/**
	 * turn of a part of the screen
	 * @param index the index of the part of screen
	 */
	public void clearScreenBit(int index) {
		screen &= ~(1 << index);
	}
	
	/**
	 * Turn of all parts of the screen
	 */
	public void flushScreen() {
		screen = 0;
	}
	
	/**
	 * The amount of lights that are turned on
	 * @return the amount of lights that are turned on
	 */
	public int amountLightsOn() {
		int count = 0;
		
		for(int i = 0; i < lightsOn().length; i++){
			count += (lightsOn()[i]) ? 1 : 0;
		}
		
		return count;
	}
	
	/**
	 * get what lights are on
	 * @return which lights that are on
	 */
	public boolean[] lightsOn() {
		boolean[] lights = new boolean[3];
		
		for(int i = 0; i < lights.length; i++)
			lights[i] = (screen & (1L << i)) != 0;
		
		return lights;
	}
	
	/**
	 * Get the mask of the screen
	 * @return the mask
	 */
	public int getMaskScreen() {
		return maskScreen;
	}
	
	/**
	 * Get the current screen data
	 * @return the screen data
	 */
	public int getScreen() {
		return screen;
	}
	
	/**
	 * get how many colors have been pressed
	 * @return amount of pressed colors
	 */
	public int getAmountPressed() {
		return amountPressed;
	}
	
	/**
	 * Set how many lights have been pressed to a specifc amount
	 * @param amountPressed the amount
	 */
	public void setAmountPressed(int amountPressed) {
		this.amountPressed = amountPressed;
	}
	
	/**
	 * Flussh the colors that have been pressed
	 */
	public void flushColorsPressed() {
		colorsPressed = new boolean[3];
	}
	
	/**
	 * Set which colors have been pressed 
	 * @param colorPressed if a color has been pressed
	 * @param index the index of the color that has been pressed
	 */
	public void setColorsPressed(boolean colorPressed, int index) {
		colorsPressed[index] = colorPressed;
	}
	
	/**
	 * Get what colors have been pressed
	 * @return the pressed colors
	 */
	public boolean[] getColorsPressed() {
		return colorsPressed;
	}
	
	/**
	 * Get the current score of the player
	 * @return the score
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Get the player id
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Get the players name, used for highscore list
	 * @return the players name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * set the name of the player
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Run method on reset of game
	 */
	public void onReset() {
		score = 0;
	}
}
