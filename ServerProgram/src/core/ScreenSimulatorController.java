package core;

import java.awt.Color;

/**
 * The controller of the GUI panel that shows what the physical screen should 
 * be showing based on the players data
 * @author tom.leonardsson
 *
 */
public class ScreenSimulatorController {
	private ScreenSimulatorView view;
	
	private Color[] lightColors = {Color.RED, Color.YELLOW, Color.GREEN, Color.RED, Color.YELLOW, Color.GREEN};
	
	/**
	 * create a controller with a specifc view
	 * @param view the specific view
	 */
	public void setView(ScreenSimulatorView view) {
		this.view = view;
	}
	
	/**
	 * Set a light to on
	 * @param index
	 */
	public void setLight(int index) {
		view.getLights()[index].setBackground(lightColors[index]);
	}
	
	public void clearLight(int index) {
		view.getLights()[index].setBackground(Color.BLACK);
	}
	
	public void flushLights() {
		for(int i = 0; i < view.getLights().length; i++)
			clearLight(i);
	}
}
