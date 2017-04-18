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
	
	/**
	 * Constructor: creates a new instance of a UDPServer.
	 * Starts a new thread running the UDPServer.
	 */
	public ServerController() {
		server = new UDPServer(4444, new String[]{"10.2.29.150", "10.2.29.150"}, "192.168.0.2");
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
	
	/**
	 * Returns the instance of the UDPServer
	 * @return
	 */
	public UDPServer getServer() {
		return server;
	}
	
	/**
	 * Returns the history of all the data sent from the server
	 * @return
	 */
	public String getSentHistory() {
		return server.getSentHistory();
	}
	
	/**
	 * Returns the history of all the input data from the client(s)
	 * @return
	 */
	public String getInputHistory() {
		return server.getInputHistory();
	}
}
