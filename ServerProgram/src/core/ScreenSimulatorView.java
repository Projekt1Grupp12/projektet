package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The GUI of the panel that shows what the physical screen should
 * be showing based of data from the player
 * @author tom.leonardsson
 *
 */
public class ScreenSimulatorView extends JPanel {
	private JButton[] lights = new JButton[6];
	
	private ScreenSimulatorController controller;
	
	/**
	 * create a view with a specifc view
	 * @param controller
	 */
	public ScreenSimulatorView(ScreenSimulatorController controller) {
		this.controller = controller;
		controller.setView(this);
		
		this.setLayout(new GridLayout(0, 6));
		for(int i = 0; i < lights.length; i++) {
			lights[i] = new JButton(" ");
			lights[i].setPreferredSize(new Dimension(32, 32));
			lights[i].setBackground(Color.BLACK);
			lights[i].setEnabled(false);
			add(lights[i]);
		}
	}
	
	/**
	 * Get the lights
	 * @return the lights
	 */
	public JButton[] getLights() {
		return lights;
	}
}
