package clientSimulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The GUI of the client
 * @author tom.leonardsson
 *
 */
public class ClientView extends JPanel {
	ClientController controller;
	
	private JButton[] colorButtons = new JButton[3];
	private JButton startButton = new JButton();
	private JButton joinButton = new JButton("JOIN");
	private JButton logOut = new JButton("Logut");
	
	private Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
	private String[] gameNames = new String[]{" ", "Puzzle Game", "Traffic Game", "Duel Game"};
	
	private JLabel feedback = new JLabel();
	
	private JPanel buttons = new JPanel();
	private JPanel startButtonPanel = new JPanel();
	
	private JLabel idText = new JLabel();
	
	private JComboBox<String> games = new JComboBox<String>();
	
	/**
	 * Create a client view with a specific controller and player id
	 * @param controller the specifc controller
	 * @param id the palyer id
	 */
	public ClientView(ClientController controller, int id) {
		for(int i = 0; i < gameNames.length; i++) {
			games.addItem(gameNames[i]);
		}
		
		this.controller = controller;
		controller.setView(this);
		setLayout(new GridLayout(9, 0));

		add(new JLabel(controller.getName()));
		
		ButtonListener listener = new ButtonListener();
		
		buttons.setLayout(new GridLayout(0, 3));
		for(int i = 0; i < colorButtons.length; i++) {
			colorButtons[i] = new JButton();
			colorButtons[i].setBackground(colors[i]);
			colorButtons[i].setPreferredSize(new Dimension(32, 32));
			colorButtons[i].addActionListener(listener);
			buttons.add(colorButtons[i]);
		}
		
		startButton.setBackground(Color.RED);
		startButton.addActionListener(listener);
		
		logOut.addActionListener(listener);
		
		startButtonPanel.setLayout(new GridLayout(0, 3));
		startButtonPanel.add(new JLabel());
		startButtonPanel.add(startButton);
		startButtonPanel.add(new JLabel());
		
		joinButton.addActionListener(listener);
		
		ItemChangeListener changeListener = new ItemChangeListener();
		games.addItemListener(changeListener);
		
		feedback.setPreferredSize(new Dimension(120, 32));
		idText.setText("Player: " + controller.getId());
		add(feedback);
		add(buttons);
		add(new JLabel());
		add(startButtonPanel);
		add(idText);
		add(games);
		add(joinButton);
		add(logOut);
	}
	
	/**
	 * Set the feedback text to a specific text
	 * @param text the specifc text to set it to
	 */
	public void setFeedbackText(String text) {
		this.feedback.setText(text);
	}
	
	class ItemChangeListener implements ItemListener {
	    public void itemStateChanged(ItemEvent event) {	    	
	       if (event.getStateChange() == ItemEvent.SELECTED) {
	    	   if(event.getSource() == games) {
	    		   try {
					controller.send((String) games.getSelectedItem() + ";" + controller.getId());
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	   }
	       }
	    }
	}
	
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for(int i = 0; i < colorButtons.length; i++) {
				if(e.getSource() == colorButtons[i]) {
					try {
						controller.send((i+1) + ";" + controller.getId());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					idText.setText("Player: " + controller.getId());
				}
			}
			
			if(e.getSource() == startButton) {
				try {
					controller.send("-2");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				idText.setText("Player: " + controller.getId());
			}
			
			if(e.getSource() == logOut) {
				try {
					controller.send("logout;" + controller.getId());
					//else controller.hasGottenGame = false;
					//controller.send("highscore;"+controller.getId());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			if(e.getSource() == joinButton) {
				try {
					if(!controller.hasGottenGame) controller.send("ready?;" + controller.getId());
					else controller.send("ready;" + controller.getId());
					//controller.send("highscore;" + controller.getId());
					try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
