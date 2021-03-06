package clientSimulator;

import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import core.BetterRandom;
import core.UDPServer;

/**
 * Controller for the GUI of the client
 * @author tom.leonardsson
 *
 */
public class ClientController implements Runnable {
	private int id;
	private int port; 
	
	private String name;
	
	private ClientView view;
	
	DatagramSocket serverSocketListen = null;
	
	private boolean sentName;
	
	public boolean hasGottenGame;

	/**
	 * Create client controller with a specific player id
	 * @param id the player id of the client
	 */
	public ClientController(int id) {
		port = 4444;
		this.id = id;
		
		try {
			if(!sentName) {
				name = "";
				int length = BetterRandom.random(3, 8);
				for(int i = 0; i < length; i++) {
					name += (char)BetterRandom.random(1, 255);
				}
				send(name);
				sentName = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("port: " + (port+1+id));
			serverSocketListen = new DatagramSocket(port+1+id);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
	}
	
	/**
	 * Set the specific view of the controller
	 * @param view the specifc view
	 */
	public void setView(ClientView view) {
		this.view = view;
	}
	
	/**
	 * Send a message to the server at the port
	 * @param message the message to send
	 * @throws IOException
	 */
	public void send(String message) throws IOException {
		DatagramSocket serverSocket = new DatagramSocket();
		InetAddress ipAddress = InetAddress.getByName("localhost");
		DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, ipAddress, port);
		serverSocket.send(sendPacket);

		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		serverSocket.close();
	}
	
	/**
	 * Get the player id of the client
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * get the player name of the client
	 * @return the player name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The update loop of the client in which it recives and responds
	 */
	public void run() {
		byte[] receiveData = new byte[32];
		DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
		try {
			while(true) {	
				serverSocketListen.receive(packet);
				if(UDPServer.putTogether(packet.getData()).equals("-1")) {
					send("Ready?");
				} else {
					view.setFeedbackText(UDPServer.putTogether(packet.getData()/*, "XXXX MOVES! XXXX".length()*/));
				}
				if(UDPServer.putTogether(packet.getData()).contains("Game")) {
					hasGottenGame = true;
				}
				receiveData = new byte[32];
				packet = new DatagramPacket(receiveData, receiveData.length);
			} } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
