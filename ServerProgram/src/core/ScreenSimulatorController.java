package core;

import java.awt.Color;

public class ScreenSimulatorController {
	private ScreenSimulatorView view;
	
	private Color[] lightColors = {Color.RED, Color.YELLOW, Color.GREEN, Color.RED, Color.YELLOW, Color.GREEN};
	
	public void setView(ScreenSimulatorView view) {
		this.view = view;
	}
	
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
