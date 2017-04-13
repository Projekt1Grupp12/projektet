package core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

public class ServerView extends JPanel implements ActionListener {
	ServerController controller;
	
	private JTextArea currentInput = new JTextArea();
	private JTextArea currentOutput = new JTextArea();
	 
	private JTextField[] phoneIpAdressField = new JTextField[]{new JTextField("10.2.29.150"), new JTextField("")};
	private JTextField ardIpAdressField = new JTextField("192.168.0.12");
	
	private JLabel inputLabel = new JLabel("INPUT");
	private JLabel outputLabel = new JLabel("OUTPUT");
	private JLabel[] phoneIpAdressLabel = new JLabel[]{new JLabel("PHONE IP 1: "), new JLabel("PHONE IP 2: ")}; 
	private JLabel ardIpAdressLabel = new JLabel("ARDURINO IP: ");
	
	private JPanel[] phoneIpAdressesPanel = new JPanel[2];
	private JPanel ardIpAdressPanel = new JPanel();
	private JPanel inputPanel = new JPanel();
	private JPanel outputPanel = new JPanel();
	private JPanel ipAdressPanels = new JPanel();
	private JPanel messagePanel = new JPanel();
	
	private JButton sendButton = new JButton("SEND");
	private JTextField message = new JTextField();
	
	public ServerView(ServerController controller) {
		this.controller = controller;
		
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
		
		this.add(ipAdressPanels, BorderLayout.NORTH);
		
		this.add(inputPanel, BorderLayout.EAST);
		this.add(outputPanel, BorderLayout.WEST);
		
		this.currentOutput.setText(controller.getSentHistory());
		this.currentInput.setText(controller.getInputHistory());
		
		ButtonListener b = new ButtonListener();
		sendButton.addActionListener(b);
		this.messagePanel.add(this.sendButton);
		message.setPreferredSize(new Dimension(100, 30));
		this.messagePanel.add(this.message);
		
		add(messagePanel, BorderLayout.CENTER);
		
		timer.start(); //Start the timer
	}
	
	Timer timer = new Timer(100, new MyListener()); //Tick every 1000ms, let MyListener listen to the ticks

	private class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) { //Timer ticked
			currentOutput.setText(controller.getSentHistory());
			currentInput.setText(controller.getInputHistory());
			currentInput.repaint();
			currentOutput.repaint();
		}
	}

	
	public class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == sendButton) {
				try {
					controller.getServer().sendToPhone(((message.getText().length() != 1 && message.getText().charAt(0) == '0' && message.getText().charAt(1) == 'b') ? Integer.parseInt(message.getText().substring(2), 2) + "" : message.getText()), 0);
					controller.getServer().sendToArdurino(((message.getText().length() != 1 && message.getText().charAt(0) == '0' && message.getText().charAt(1) == 'b') ? Integer.parseInt(message.getText().substring(2), 2) + "" : message.getText()));
					currentOutput.setText(controller.getSentHistory());
					currentInput.setText(controller.getInputHistory());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
