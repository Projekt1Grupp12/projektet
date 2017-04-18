package core;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import game.Game;
import game.Player;
import game.PuzzelGame;

public class UDPServer implements Runnable
{	
	boolean recsive = true;
	
	private DatagramSocket serverSocket = null;
	
	private String[] phoneIps;
	private String ardurinoIp;
	private String sentHistory;
	private String inputHistory;
	
	private int port;
	private int sentHistoryIndex;
	private int inputHistoryIndex;
	
	private boolean hasStartedGame; 
	
	byte[] receiveData;
	
	Game game = new PuzzelGame(new Player[]{new Player(0), new Player(1)}, this);
	
	DatagramPacket packet;
	
	Random random = new Random();
	
	public UDPServer(int port, String[] phoneIps, String ardurinoIp) {
		this.port = port;
		
		this.phoneIps = new String[phoneIps.length];
		for(int i = 0; i < phoneIps.length; i++)
			this.phoneIps[i] = phoneIps[i];
		
		this.ardurinoIp = ardurinoIp;
		
		receiveData = new byte[1024];
		
		packet = new DatagramPacket(receiveData, receiveData.length);
		
		sentHistory = "";
	}
	
	public void send(String message, String ip) throws IOException {
		InetAddress ipAddress = InetAddress.getByName(ip);
		DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, ipAddress, port);
		serverSocket.send(sendPacket);

		sentHistory = message + "  : " + (sentHistoryIndex++) + "\n" + sentHistory;
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToPhone(String message, int index) throws IOException {
		send(message, phoneIps[index]);
	}
	
	public void sendToArdurino(String message) throws IOException {
		send(message, ardurinoIp);
	}
	
	public void recive() throws IOException {
		serverSocket.receive(packet);
		inputHistory = putTogether(packet.getData(), 5) + "  : " + (inputHistoryIndex++) + "\n" + inputHistory;
	}

	public void run() {
		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		try {
			while(true) {
				try {
					TimeUnit.MILLISECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				recive();
				String input = putTogether(packet.getData(), 2);
				if(!hasStartedGame) {
					if(input.equals("-2")) {
						hasStartedGame = true;
						game.update(input);
					}
				}
				else
					game.update(input);
					
				receiveData = new byte[1024];
				packet = new DatagramPacket(receiveData, receiveData.length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Player[] getPlayers() {
		return this.game.getPlayers();
	}
	
	public String getSentHistory() {
		return sentHistory;
	}
	
	public String getInputHistory() {
		return inputHistory;
	}
	
	public String putTogether(byte[] t, int l) {
		String tmp = "";
		
		for(int i = 0; i < l; i++) {
			tmp += (char)t[i];
		}
		
		return tmp;
	}
}