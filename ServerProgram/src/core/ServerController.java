package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import clientSimulator.ClientController;
import clientSimulator.ClientView;

/**
 * The controller of the GUI for the server
 * @author tom.leonardsson 
 *
 */
public class ServerController {
	private UDPServer server;
	
	private ServerView view;
	
	private int nextId = 0;
	
	public static boolean hasCreatedClient; 
	
	/**
	 * Constructor: creates a new instance of a UDPServer.
	 * Starts a new thread running the UDPServer.
	 */
	public ServerController() {
		server = new UDPServer(4444);
		new Thread(server).start();
	}
	
	/**
	 * Update the simluators screen with the screen data and mask form the player
	 */
	public void updateSimulatorScreen() {
		for(int i = 0; i < server.getPlayers().length; i++) {
			for(int j = 0; j < server.getPlayers()[i].lightsOn().length; j++) {
				if(server.getPlayers()[i].lightsOn()[j] && (server.getPlayers()[i].getMaskScreen() & (1L << j)) != 0)
					view.getScreenSimulatorController().setLight(j + (3*i));
				else 
					view.getScreenSimulatorController().clearLight(j + (3*i));
			}
		}
	}
	
	/**
	 * Create a client and raise the next id
	 */
	public void createClient() {
		ClientView viewer = new ClientView(new ClientController(nextId), nextId);
		JFrame frame = new JFrame("client");
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.add(viewer);
		frame.setResizable(true);
		frame.pack();
		frame.setLocation(view.getLocation().x+view.getWidth()+8+nextId*frame.getWidth(), 100);
		frame.setVisible(true);
		if(!hasCreatedClient) hasCreatedClient = true;
		nextId = nextId + 1;
	}
	
	/**
	 * Set set the next id to give out
	 * @param nextId the id
	 */
	public void setNextId(int nextId) {
		this.nextId = nextId;
	}
	
	/**
	 * Get the next id to give out
	 * @return the id
	 */
	public int getNextId() {
		return nextId;
	}
	
	/**
	 * Set a specific view
	 * @param view the specifc view
	 */
	public void setView(ServerView view) {
		this.view = view;
	}
	
	/**
	 * Returns the instance of the UDPServer
	 * @return the instance of the server
	 */
	public UDPServer getServer() {
		return server;
	}
	
	/**
	 * Returns the history of all the data sent from the server
	 * @return the history of sent data
	 */
	public String getSentHistory() {
		return server.getSentHistory();
	}
	
	/**
	 * Returns the history of all the input data from the client(s)
	 * @return the history of recsived data
	 */
	public String getInputHistory() {
		return server.getInputHistory();
	}
}
