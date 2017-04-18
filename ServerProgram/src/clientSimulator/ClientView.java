package clientSimulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ClientView extends JPanel {
	ClientController controller;
	
	private JButton[] colorButtons = new JButton[3];
	
	private Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
	
	private JLabel feedback = new JLabel();
	
	private JPanel buttons = new JPanel();
	
	public ClientView(ClientController controller, int id) {
		this.controller = new ClientController(id);
		setLayout(new GridLayout(2, 0));
		System.out.println(controller.getId());
		
		ButtonListener listener = new ButtonListener();
		
		buttons.setLayout(new GridLayout(0, 3));
		for(int i = 0; i < colorButtons.length; i++) {
			colorButtons[i] = new JButton();
			colorButtons[i].setBackground(colors[i]);
			colorButtons[i].setPreferredSize(new Dimension(32, 32));
			colorButtons[i].addActionListener(listener);
			buttons.add(colorButtons[i]);
		}
		
		add(feedback);
		add(buttons);
	}
	
	public class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for(int i = 0; i < colorButtons.length; i++) {
				if(e.getSource() == colorButtons[i]) {
					try {
						controller.send((i+1) + "" + controller.getId());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
