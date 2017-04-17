package core;

import javax.swing.JFrame;
import javax.swing.JPanel;

import clientSimulator.ClientController;
import clientSimulator.ClientView;

public class ServerController {
	private UDPServer server;
	
	private ServerView view;
	
	private int nextId;
	
	private boolean hasCreatedClient; 
	
	public ServerController() {
		server = new UDPServer(4444, new String[]{"10.2.29.150", "0.0.0.0"}, "192.168.0.2");
		new Thread(server).start();
	}
	
	public void updateSimulatorScreen() {
		for(int i = 0; i < server.getPlayers().length; i++) {
			for(int j = 0; j < server.getPlayers()[i].lightsOn().length; j++) {
				if(server.getPlayers()[i].lightsOn()[j])
					view.getScreenSimulatorController().setLight(j + (3*i));
				else 
					view.getScreenSimulatorController().clearLight(j + (3*i));
			}
		}
	}
	
	public void createClient() {
		ClientView viewer = new ClientView(new ClientController(nextId), nextId);
		JFrame frame = new JFrame("client");
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.add(viewer);
		frame.setResizable(true);
		frame.pack();
		frame.setVisible(true);
		
		if(!hasCreatedClient) hasCreatedClient = true;
		nextId = nextId + 1;
	}
	
	public void setView(ServerView view) {
		this.view = view;
	}
	
	public UDPServer getServer() {
		return server;
	}
	
	public String getSentHistory() {
		return server.getSentHistory();
	}
	
	public String getInputHistory() {
		return server.getInputHistory();
	}
}
