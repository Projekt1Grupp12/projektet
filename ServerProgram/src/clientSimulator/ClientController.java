package clientSimulator;

import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

public class ClientController {
	private int id;
	private int port; 
	
	private DatagramSocket serverSocket = null;
	
	public ClientController(int id) {
		port = 4444;
		this.id = id;
		try {
			send("-2");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(String message) throws IOException {
		serverSocket = new DatagramSocket(port+1);
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
	
	public int getId() {
		return id;
	}
	
	public static void main(String[] args) {
		ClientView viewer = new ClientView(new ClientController(0), 0);
		JFrame frame = new JFrame("client");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		frame.add(viewer);
		frame.setResizable(true);
		frame.pack();
		frame.setVisible(true);
	}
}
