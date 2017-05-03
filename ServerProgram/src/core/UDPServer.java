package core;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import game.Game;
import game.Player;
import game.PuzzelGame;
import game.TrafficGame;

public class UDPServer implements Runnable
{	
	final int RECIVE_BUFFER_SIZE = 128;
	
	boolean recsive = true;
	
	private DatagramSocket serverSocket = null;
	
	private String[] collectedPlayerNames;
	private String[] phoneIps;
	private String ardurinoIp;
	private String sentHistory;
	private String inputHistory;
	
	private int port;
	private int sentHistoryIndex;
	private int inputHistoryIndex;
	
	private boolean hasStartedGame; 
	
	public boolean startNewThread;
	
	byte[] receiveData;
	
	Game game = new PuzzelGame(new Player[]{new Player(0), new Player(1)}, this);
	
	DatagramPacket packet;
	
	Random random = new Random();
	
	public boolean playWithTwo = false;
	private boolean hasSetup; 
	
	public UDPServer(int port) {
		this.port = port;
		
		receiveData = new byte[RECIVE_BUFFER_SIZE];
		
		packet = new DatagramPacket(receiveData, receiveData.length);
		
		sentHistory = "";
		
		collectedPlayerNames = new String[2];
	}
	
	public void setup() {
		phoneIps = getIps();
		ardurinoIp = "192.168.0.2";
		hasSetup = true;
	}
	
	public String[] getIps() {
		String[] ips = new String[2];
		byte[] receiveData = new byte[RECIVE_BUFFER_SIZE];
		
		DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
		try {
			serverSocket = new DatagramSocket(4444);

			serverSocket.receive(packet);
			ips[0] = packet.getAddress().getHostName();
			while(putTogether(packet.getData(), 2).equals("-2")) {
				serverSocket.receive(packet);
				ips[0] = packet.getAddress().getHostName();
			}
			send("0", ips[0]);
			System.out.println(ips[0] + " | ip 0");
			serverSocket.receive(packet);
			ips[1] = packet.getAddress().getHostName();
			while(ips[0].equals(ips[1]) && playWithTwo || putTogether(packet.getData(), 2).equals("-2")) {
				serverSocket.receive(packet);
				ips[1] = packet.getAddress().getHostName();
			}
			this.send("1", ips[1]);
			System.out.println(ips[1] + " | ip 1");
			
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ips;
	}
	
	public void send(String message, String ip) throws IOException {
		InetAddress ipAddress = InetAddress.getByName(ip);
		DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, ipAddress, port+(message.equals("WIN!") || message.equals("LOSE!") ? 1 : 0));
		serverSocket.send(sendPacket);

		sentHistory = message + "  : " + (sentHistoryIndex++) + " : " + ip + "\n" + sentHistory;
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToClientSimulator(String message, int id) throws IOException {
		InetAddress ipAddress = InetAddress.getByName(phoneIps[id]);
		DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, ipAddress, port+1+id);
		serverSocket.send(sendPacket);

		sentHistory = message + "  : " + (sentHistoryIndex++) + " : " + ipAddress.getHostName() + "\n" + sentHistory;
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToPhone(String message, int index) throws IOException {
		if(!phoneIps[index].equals("127.0.0.1") && !phoneIps[index].equals("localhost"))
			send(message, phoneIps[index]);
	}
	
	public void sendToArdurino(String message) throws IOException {
		send(message, ardurinoIp);
	}
	
	public void recive() throws IOException {
		serverSocket.receive(packet);
		for(int i = 0; i < 2; i++) {
			if(!hasStartedGame) sendToPhone("-1", i);
		}
		inputHistory = putTogether(packet.getData(), 5) + "  : " + (inputHistoryIndex++) + " : " + packet.getAddress().getHostName() + "\n" + inputHistory;
	}
	
	public void resetGame(Game g) {
		game.closeGame = true;
		game = g;
		game.setTimer();
		
		try {
			sendToArdurino("00");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		game.reset();
		if(game.realTime) {
			new Thread(game).start();
		}
	}

	public void run() {
		if(!hasSetup) {
			setup();
		}
		
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
				String input = putTogether(packet.getData(), hasStartedGame ? 3 : 2);
				System.out.println(input);
				if(game.realTime) game.setInput(input);
				if(!hasStartedGame) {
					if(input.equals("-2")) {
						game.setTimer();
						hasStartedGame = true;
						game.update(input);
					}
				}
				else
					if(!game.realTime) game.update(input);

				receiveData = new byte[RECIVE_BUFFER_SIZE];
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
	
	public void endGame() {
		game.closeGame = true;
	}
	
	public static String putTogether(byte[] t, int l) {
		String tmp = "";
		
		for(int i = 0; i < l; i++) {
			tmp += (char)t[i];
		}
		
		return tmp;
	}
	
	public static String putTogether(byte[] t) {
		String tmp = "";
		
		int index = 0;
		
		tmp = putTogether(t, t.length);
		
		String revTmp = "";
		
		for(int i = tmp.length()-1; i >= 0; i--) {
			revTmp += tmp.charAt(i);
		}
		
		for(int i = 0; i < revTmp.length(); i++) {
			if(revTmp.charAt(i) != (int)0) {
				index = i;
				break;
			}
		}
		
		tmp = "";
		
		for(int i = index; i < revTmp.length(); i++) {
			tmp += revTmp.charAt(i);
		}
		
		revTmp = "";
		
		for(int i = tmp.length()-1; i >= 0; i--) {
			revTmp += tmp.charAt(i);
		}
		
		return revTmp;
	}
}