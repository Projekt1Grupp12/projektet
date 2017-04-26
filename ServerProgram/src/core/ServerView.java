package core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import game.Game;
import game.Player;
import game.PuzzelGame;
import game.TrafficGame;

public class ServerView extends JPanel {
	private ServerController controller;
	private Timer timer = new Timer(100, new MyListener()); 
	
	//Input from clients and output from server 
	private JTextArea currentInput = new JTextArea();
	private JTextArea currentOutput = new JTextArea();
	
	//Address for the client and arduino, message that is sent to both phone and arduino
	private JTextField[] phoneIpAdressField = new JTextField[]{new JTextField("10.2.29.150"), new JTextField("")};
	private JTextField ardIpAdressField = new JTextField("192.168.0.12");
	private JTextField message = new JTextField();
	
	//Labels
	private JLabel inputLabel = new JLabel("INPUT");
	private JLabel outputLabel = new JLabel("OUTPUT");
	private JLabel[] phoneIpAdressLabel = new JLabel[]{new JLabel("PHONE IP 1: "), new JLabel("PHONE IP 2: ")}; 
	private JLabel ardIpAdressLabel = new JLabel("ARDURINO IP: ");
	
	//Panels for the client phpne and arduino
	private JPanel[] phoneIpAdressesPanel = new JPanel[2];
	private JPanel ardIpAdressPanel = new JPanel();
	private JPanel inputPanel = new JPanel();
	private JPanel outputPanel = new JPanel();
	private JPanel ipAdressPanels = new JPanel();
	private JPanel messagePanel = new JPanel();
	
	private ScreenSimulatorController screenSimulatorController = new ScreenSimulatorController();
	private ScreenSimulatorView screenSimulatorView = new ScreenSimulatorView(screenSimulatorController);
	
	private JButton sendButton = new JButton("SEND");
	private JButton createClient = new JButton("CREATE CLIENT");
	private JButton resetGame = new JButton("RESET");
	
	private JComboBox games = new JComboBox();
	
	/**
	 * Constructor that creates the UI
	 * @param controller
	 */
	public ServerView(ServerController controller) {
		games.addItem(new PuzzelGame(new Player[]{new Player(0), new Player(1)}, controller.getServer()));
		games.addItem(new TrafficGame(new Player[]{new Player(0), new Player(1)}, controller.getServer()));
		
		this.controller = controller;
		controller.setView(this);
		
		this.setLayout(new BorderLayout());
		
		for(int i = 0; i < 2; i++) {
			phoneIpAdressesPanel[i] = new JPanel();
			phoneIpAdressesPanel[i].setLayout(new GridLayout(0, 2));
			phoneIpAdressesPanel[i].add(phoneIpAdressLabel[i]);
			phoneIpAdressesPanel[i].add(phoneIpAdressField[i]);
			ipAdressPanels.add(phoneIpAdressesPanel[i]);
		}
		
		ardIpAdressPanel.setLayout(new GridLayout(0, 2));
		ardIpAdressPanel.add(ardIpAdressLabel);
		ardIpAdressPanel.add(ardIpAdressField);
		
		ipAdressPanels.add(ardIpAdressPanel);
		
		inputPanel.setLayout(new GridLayout(2, 0));
		inputPanel.add(inputLabel);
		inputPanel.add(currentInput);
		currentInput.setPreferredSize(new Dimension(250, 200));
		
		outputPanel.setLayout(new GridLayout(2, 0));
		outputPanel.add(outputLabel);
		outputPanel.add(currentOutput);
		DefaultCaret caret = (DefaultCaret)currentOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		currentOutput.setPreferredSize(new Dimension(250, 200));
		
		//this.add(ipAdressPanels, BorderLayout.NORTH);
		
		this.add(inputPanel, BorderLayout.EAST);
		this.add(outputPanel, BorderLayout.WEST);
		
		this.currentOutput.setText(controller.getSentHistory());
		this.currentInput.setText(controller.getInputHistory());
		
		this.currentInput.setEditable(false);
		this.currentOutput.setEditable(false);
		
		ButtonListener b = new ButtonListener();
		sendButton.addActionListener(b);
		this.messagePanel.add(this.sendButton);
		message.setPreferredSize(new Dimension(100, 30));
		this.messagePanel.add(this.message);
		createClient.addActionListener(b);
		this.messagePanel.add(resetGame);
		resetGame.addActionListener(b);
		
		messagePanel.add(createClient);
		messagePanel.add(games);
		ItemChangeListener itemChangeListener = new ItemChangeListener();
		games.addItemListener(itemChangeListener);
		
		add(messagePanel, BorderLayout.CENTER);
		add(screenSimulatorView, BorderLayout.SOUTH);
		
		timer.start(); 
	}

	private class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) { 
			currentOutput.setText(controller.getSentHistory());
			currentInput.setText(controller.getInputHistory());
			controller.updateSimulatorScreen();
			screenSimulatorView.repaint();
			currentInput.repaint();
			currentOutput.repaint();
		}
	}

	class ItemChangeListener implements ItemListener {
	    public void itemStateChanged(ItemEvent event) {
	       if (event.getStateChange() == ItemEvent.SELECTED) {
	    	   if(event.getSource() == games) {
	    		   controller.getServer().resetGame((Game)event.getItem());
	    	   }
	       }
	    }
	}
	
	public class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == createClient) {
				controller.createClient();
			}
			
			if(e.getSource() == sendButton) {
				try {
					controller.getServer().sendToPhone(((message.getText().length() != 1 && message.getText().charAt(0) == '0' && message.getText().charAt(1) == 'b') ? Integer.parseInt(message.getText().substring(2), 2) + "" : message.getText()), 0);
					controller.getServer().sendToPhone(((message.getText().length() != 1 && message.getText().charAt(0) == '0' && message.getText().charAt(1) == 'b') ? Integer.parseInt(message.getText().substring(2), 2) + "" : message.getText()), 1);
					controller.getServer().sendToArdurino(((message.getText().length() != 1 && message.getText().charAt(0) == '0' && message.getText().charAt(1) == 'b') ? Integer.parseInt(message.getText().substring(2), 2) + "" : message.getText()));
					currentOutput.setText(controller.getSentHistory());
					currentInput.setText(controller.getInputHistory());
					System.out.println(message.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			if(e.getSource() == resetGame) {
				controller.getServer().resetGame(new PuzzelGame(new Player[]{new Player(0), new Player(1)}, controller.getServer()));
			}
		}
	}
	
	public ScreenSimulatorController getScreenSimulatorController() {
		return screenSimulatorController;
	}
}
