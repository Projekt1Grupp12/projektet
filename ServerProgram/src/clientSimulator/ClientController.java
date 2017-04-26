package clientSimulator;

import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import core.UDPServer;

public class ClientController implements Runnable {
	private int id;
	private int port; 
	
	private ClientView view;
	
	DatagramSocket serverSocketListen = null;

	public ClientController(int id) {
		port = 4444;
		this.id = id;
		
		try {
			send("0");
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
	
	public void setView(ClientView view) {
		this.view = view;
	}
	
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
	
	public int getId() {
		return id;
	}

	@Override
	public void run() {
		byte[] receiveData = new byte[1024];
		DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
		try {
			while(true) {	
				serverSocketListen.receive(packet);
				send("-");
				view.setFeedbackText(UDPServer.putTogether(packet.getData()/*, "XXXX MOVES! XXXX".length()*/));
				receiveData = new byte[1024];
				packet = new DatagramPacket(receiveData, receiveData.length);
			} } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
