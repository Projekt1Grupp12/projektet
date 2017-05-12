package core;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import game.DualGame;
import game.Game;
import game.Player;
import game.PuzzelGame;
import game.TrafficGame;

public class UDPServer implements Runnable
{	
	final int RECIVE_BUFFER_SIZE = 128;
	
	public static final String EXIT_INSTRUCTION = "exit";
	public static final String START_SESSION_INSTRUCTION = "start";
	public static final String START_GAME_INSTRUCTION = "-2";
	public static final String[] ENGINE_INSTRUCTION = new String[]{"-3", "-4"};
	public static final String JOIN_INSTRUCTION = "join?";
	public static final String ACK_INSTRUCTION = "-1";
	public static final String TIMEOUT_INSTRUCTION = "timeout";
	public static final String TIMEOUT_ACK_INSTRUCTION = "ok";
	public static final String LOG_OUT_INSCTRUCTION = "logout";
	public static final String LOG_OUT_ACK_INSTRUCTION = "logout";
	
	boolean recsive = true;
	
	private DatagramSocket serverSocket = null;
	
	private String[] collectedPlayerNames;
	private String[] phoneIps;
	private String ardurinoIp;
	private String sentHistory;
	private String inputHistory;
	
	private String playerPickedGame;
	
	private int port;
	private int sentHistoryIndex;
	private int inputHistoryIndex;
	
	public boolean hasStartedGame; 
	
	public boolean startNewThread;
	
	byte[] receiveData;
	
	Game game = new PuzzelGame(new Player[]{new Player(0), new Player(1)}, this);
	
	Game[] games = new Game[]{new PuzzelGame(new Player[]{new Player(0), new Player(1)}, this), new TrafficGame(new Player[]{new Player(0), new Player(1)}, this), new DualGame(new Player[]{new Player(0), new Player(1)}, this)};
	
	DatagramPacket packet;
	
	Random random = new Random();
	
	public boolean playWithTwo = false;
	private boolean hasSetup; 
	
	public boolean reset;
	
	public UDPServer(int port) {
		this.port = port;
		
		receiveData = new byte[RECIVE_BUFFER_SIZE];
		
		packet = new DatagramPacket(receiveData, receiveData.length);
		
		sentHistory = "";
		
		collectedPlayerNames = new String[2];
		
		playerPickedGame = "";
	}
	
	public void setup() {
		phoneIps = getIps();
		ardurinoIp = "192.168.0.2";
		hasSetup = true;
	}
	
	public String[] getIpsReset() {
		String[] ips = new String[2];
		
		try {
			serverSocket.receive(packet);
			ips[0] = packet.getAddress().getHostName();
			collectedPlayerNames[0] = putTogether(packet.getData());
			while(putTogether(packet.getData(), 2).equals(START_GAME_INSTRUCTION)) {
				serverSocket.receive(packet);
				ips[0] = packet.getAddress().getHostName();
			}
			send("0", ips[0]);
			System.out.println(ips[0] + " | ip 0");
			serverSocket.receive(packet);
			ips[1] = packet.getAddress().getHostName();
			collectedPlayerNames[1] = putTogether(packet.getData());
			while(ips[0].equals(ips[1]) && playWithTwo || putTogether(packet.getData(), 2).equals(START_GAME_INSTRUCTION)) {
				serverSocket.receive(packet);
				ips[1] = packet.getAddress().getHostName();
			}
			this.send("1", ips[1]);
			System.out.println(ips[1] + " | ip 1");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ips;
	}
	
	public String[] getIps() {
		String[] ips = new String[2];
		byte[] receiveData = new byte[RECIVE_BUFFER_SIZE];
		
		DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
		try {
			serverSocket = new DatagramSocket(4444);

			serverSocket.receive(packet);
			inputHistory = putTogether(packet.getData()) + "  : " + (inputHistoryIndex++) + " : " + packet.getAddress().getHostName() + "\n" + inputHistory;
			ips[0] = packet.getAddress().getHostName();
			collectedPlayerNames[0] = putTogether(packet.getData());
			System.out.println(collectedPlayerNames[0]);
			while(putTogether(packet.getData(), 2).equals(START_GAME_INSTRUCTION)) {
				serverSocket.receive(packet);
				ips[0] = packet.getAddress().getHostName();
			}
			send("0", ips[0]);
			System.out.println(ips[0] + " | ip 0");
			serverSocket.receive(packet);
			inputHistory = putTogether(packet.getData()) + "  : " + (inputHistoryIndex++) + " : " + packet.getAddress().getHostName() + "\n" + inputHistory;
			ips[1] = packet.getAddress().getHostName();
			collectedPlayerNames[1] = putTogether(packet.getData());
			System.out.println(collectedPlayerNames[1]);
			while(ips[0].equals(ips[1]) && playWithTwo || putTogether(packet.getData(), 2).equals(START_GAME_INSTRUCTION)) {
				serverSocket.receive(packet);
				ips[1] = packet.getAddress().getHostName();
			}
			this.send("1", ips[1]);
			System.out.println(ips[1] + " | ip 1");
			
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < game.getPlayers().length; i++)
			getPlayers()[i].setName(collectedPlayerNames[i]);
			
		return ips;
	}
	
	public void send(String message, String ip, int port) throws IOException {
		InetAddress ipAddress = InetAddress.getByName(ip);
		DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, ipAddress, port/*+(message.equals("WIN!") || message.equals("LOSE!") || message.equals(EXIT_INSTRUCTION) ? 1 : 0*/);
		serverSocket.send(sendPacket);

		sentHistory = message + "  : " + (sentHistoryIndex++) + " : " + ip + "\n" + sentHistory;
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void send(String message, String ip) throws IOException {
		send(message, ip, 4444 +(message.equals("WIN!") || message.equals("LOSE!") || message.equals(EXIT_INSTRUCTION) ? 1 : 0));
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
			if(!hasStartedGame) { 
				if(!playerPickedGame.equals("")) sendToPhone(ACK_INSTRUCTION, 1);
				//sendToClientSimulator("-1", i);
			}
		}
	
		inputHistory = putTogether(packet.getData()) + "  : " + (inputHistoryIndex++) + " : " + packet.getAddress().getHostName() + "\n" + inputHistory;
	}
	
	public void resetSession() {
		phoneIps = getIpsReset();
		
		playerPickedGame = "";
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
		
		hasStartedGame = false;
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
				if(reset) {
					resetSession();
					reset = false;
				}
				
				try {
					TimeUnit.MILLISECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				recive();
				String input = putTogether(packet.getData());
				//System.out.println(input);
				if(game.realTime) game.setInput(input);
				//System.out.println(hasStartedGame);
				if(!hasStartedGame) {
					if(input.contains("Game")) {
						playerPickedGame = input;
						for(int i = 0; i < games.length; i++) {
							if(games[i].getName().equals(input.split(";")[0])) {
								resetGame(games[i]);
							}
						}
					}
					
					if(!playerPickedGame.equals("") && playerPickedGame.split(";").length == 2 &&  input.split(";")[0].equals(JOIN_INSTRUCTION) && !input.split(";")[1].equals(playerPickedGame.split(";")[1])) {
						sendToPhone(playerPickedGame.split(";")[0], playerPickedGame.split(";")[1].equals("0") ? 1 : 0);
						sendToClientSimulator(playerPickedGame.split(";")[0], playerPickedGame.split(";")[1].equals("0") ? 1 : 0);
					}

					if(input.split(";").length == 2) {
						if(input.split(";")[0].equals("ready")) {
							for(int i = 0; i < 2 ; i++) {
								sendToPhone(START_SESSION_INSTRUCTION, 1);
								sendToClientSimulator(START_SESSION_INSTRUCTION, 1);
							}
						}
						
						if(input.split(";")[0].equals(TIMEOUT_INSTRUCTION)) {
							sendToPhone(TIMEOUT_ACK_INSTRUCTION, Integer.parseInt(input.split(";")[1]));
							sendToClientSimulator(TIMEOUT_ACK_INSTRUCTION, Integer.parseInt(input.split(";")[1]));
						}
					}
					
					if(input.equals(START_GAME_INSTRUCTION)) {
						game.setTimer();
						hasStartedGame = true;
						game.update(input);
					}
				}
				else {
					if(!game.realTime) game.update(input);
				}
				
				if(input.split(";")[0].equals(LOG_OUT_INSCTRUCTION)) {
					sendToPhone(LOG_OUT_ACK_INSTRUCTION, Integer.parseInt(input.split(";")[1]));
					send(LOG_OUT_ACK_INSTRUCTION, phoneIps[Integer.parseInt(input.split(";")[1]) == 0 ? 1 : 0], 4445);
					hasStartedGame = false;
					resetSession();
				}

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