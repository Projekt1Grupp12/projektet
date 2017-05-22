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

/**
 * The UDP server, handels the connections and logic of the server
 * @author tom.leonardsson
 *
 */
public class UDPServer implements Runnable
{	
	final int RECIVE_BUFFER_SIZE = BetterRandom.powerOfTwo(5);
	
	public static final String EXIT_INSTRUCTION = "exit";
	public static final String START_SESSION_INSTRUCTION = "start";
	public static final String START_GAME_INSTRUCTION = "-2";
	public static final String[] ENGINE_INSTRUCTION = new String[]{"-3", "-4"};
	public static final String JOIN_INSTRUCTION = "Ready?";
	public static final String ACK_INSTRUCTION = "-1";
	public static final String TIMEOUT_INSTRUCTION = "timeout";
	public static final String TIMEOUT_ACK_INSTRUCTION = "ok";
	public static final String LOG_OUT_INSTRUCTION = "logout";
	public static final String LOG_OUT_ACK_INSTRUCTION = "logout";
	public static final String HIGHSCORE_INSTRUCTION = "highscore";
	public static final String GET_GAMES_INSTRUCTION =  "getgames";
	public static final String CHOOSE_GAME_INSTRUCTION =  "choosegame";
	public static final String CHOOSE_GAME_REQUEST_INSTRUCTION =  "choosegame?";
	public static final String CHOOSE_MAX_SCORE_REQUEST_INSTRUCTION =  "scorelimit";
	
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
	
	Game game;// = new PuzzelGame(new Player[]{new Player(0), new Player(1)}, this);
	
	Game[] games = new Game[]{new PuzzelGame(new Player[]{new Player(0), new Player(1)}, this), new TrafficGame(new Player[]{new Player(0), new Player(1)}, this), new DualGame(new Player[]{new Player(0), new Player(1)}, this)};
	
	DatagramPacket packet;
	
	Random random = new Random();
	
	public boolean playWithTwo = false;
	private boolean hasSetup; 
	
	public boolean reset;
	
	/**
	 * Create a server with a specifc port
	 * @param port
	 */
	public UDPServer(int port) {
		this.port = port;
		
		receiveData = new byte[RECIVE_BUFFER_SIZE];
		
		packet = new DatagramPacket(receiveData, receiveData.length);
		
		sentHistory = "";
		
		collectedPlayerNames = new String[2];
		
		playerPickedGame = "";
		
		game = games[0];
	}
	
	/**
	 * Setup the IP address
	 */
	public void setup() {
		phoneIps = getIps();
		ardurinoIp = "192.168.0.2";
		hasSetup = true;
	}
	
	/**
	 * Constructor: creates a new instance of a UDPServer.
	 * Starts a new thread running the UDPServer.
	 */
	private void LoginController() {
		new Thread().start();
	}
	
	/**
	 * Get the IP addresses of the clients while the server has aldready been running, get the client player names and give them a player ID
	 * @return the IP addresses of the clients
	 */
	public String[] getIpsReset() {
		String[] ips = new String[2];
		
		try {
			serverSocket.receive(packet);
			ips[0] = packet.getAddress().getHostName();
			collectedPlayerNames[0] = putTogether(packet.getData());
			while(putTogether(packet.getData(), 2).equals(START_GAME_INSTRUCTION) || putTogether(packet.getData()).contains(LOG_OUT_INSTRUCTION)) {
				serverSocket.receive(packet);
				ips[0] = packet.getAddress().getHostName();
			}
			send("0", ips[0]);
			System.out.println(ips[0] + " | ip 0");
			serverSocket.receive(packet);
			ips[1] = packet.getAddress().getHostName();
			collectedPlayerNames[1] = putTogether(packet.getData());
			while(ips[0].equals(ips[1]) && playWithTwo || putTogether(packet.getData(), 2).equals(START_GAME_INSTRUCTION) || putTogether(packet.getData()).contains(LOG_OUT_INSTRUCTION)) {
				serverSocket.receive(packet);
				
				ips[1] = packet.getAddress().getHostName();
			}
			send("1", ips[1]);
			send(CHOOSE_GAME_INSTRUCTION, ips[0], port+1);
			System.out.println(ips[1] + " | ip 1");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ips;
	}
	
	/**
	 * Listen for messages to pick up client IP address and their player name and give them a spot and player id
	 * @return the IP address of the clients
	 */
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

				if(putTogether(packet.getData()).split(";").length != 0 && putTogether(packet.getData()).split(";")[0].equals(HIGHSCORE_INSTRUCTION)) {
					send(game.getHighscoreList(), ips[Integer.parseInt(putTogether(packet.getData()).split(";")[1])]);
					send(game.getHighscoreList(), "localhost");
				}
				ips[1] = packet.getAddress().getHostName();
			}
			this.send("1", ips[1]);
			send(CHOOSE_GAME_INSTRUCTION, ips[0], port+1);
			System.out.println(ips[1] + " | ip 1");
			
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < game.getPlayers().length; i++)
			getPlayers()[i].setName(collectedPlayerNames[i]);
			
		return ips;
	}
	
	/**
	 * Send message to a specifc IP address and port and add to histoiry of output
	 * @param message the message
	 * @param ip the specifc IP
	 * @param port the specifc port
	 * @throws IOException
	 */
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
	
	/**
	 * Send message to the default port at specifc IP address and add to histoiry of output
	 * @param message the message
	 * @param ip the specific IP address
	 * @throws IOException
	 */
	public void send(String message, String ip) throws IOException {
		send(message, ip, port + (message.equals("WIN!") || message.equals("LOSE!") || message.equals(EXIT_INSTRUCTION) ? 1 : 0));
	}
	
	/**
	 * Send message to the client simulaotr(the client that is run on the computer) at specifc player id and add to histoiry of output
	 * @param message the message
	 * @param index the specifc player id
	 * @throws IOException
	 */
	public void sendToClientSimulator(String message, int index) throws IOException {
		send(message, phoneIps[index], port+1+index);
	}
	
	/**
	 * Send messsage to the client with spefic id and add to histoiry of output
	 * @param message the message 
	 * @param index the specific id
	 * @throws IOException
	 */
	public void sendToPhone(String message, int index) throws IOException {
		if(!phoneIps[index].equals("127.0.0.1") && !phoneIps[index].equals("localhost"))
			send(message, phoneIps[index]);
	}
	
	/**
	 * Send message to the embeededsystem and add to histoiry of output
	 * @param message the message
	 * @throws IOException
	 */
	public void sendToArdurino(String message) throws IOException { 
		send(message, ardurinoIp);
	}
	
	/**
	 * Rescive and ACK data incoming at default port and add the data to the input history
	 * @throws IOException
	 */
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
	
	/**
	 * Listen for new client IP addresses and unpick game
	 */
	public void resetSession() {
		sentHistory = "";
		inputHistory = "";
		
		phoneIps = getIpsReset();
		
		playerPickedGame = "";
	}
	
	/**
	 * Reset the game to a specifc game
	 * @param g the specifc game
	 */
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
	
	/**
	 * The update loop of the server, listens and sends messages and setup the server
	 */
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
					if(input.equals(GET_GAMES_INSTRUCTION)) {
						String t = "";
						for(int i = 0; i < games.length; i++)
							t += games[i].getName() + ((i != games.length-1) ? ";" : "");
						sendToPhone(t, 0);
					}
					
					
					
					if(input.split(";").length != 1 && input.split(";")[0].equals(CHOOSE_GAME_REQUEST_INSTRUCTION) && input.split(";")[1].equals("0")) {
						send(CHOOSE_GAME_INSTRUCTION, phoneIps[0], port+1);
					}
					
					if(input.contains("Game")) {
						playerPickedGame = input;
						sendToPhone(ACK_INSTRUCTION, 0);
						sendToClientSimulator(ACK_INSTRUCTION, 0);
						
						send(playerPickedGame.split(";")[0], phoneIps[1], port+1);
						sendToClientSimulator(playerPickedGame.split(";")[0], playerPickedGame.split(";")[1].equals("0") ? 1 : 0);
						
						for(int i = 0; i < games.length; i++) {
							if(games[i].getName().equals(input.split(";")[0])) {
								resetGame(games[i]);
							}
						}
					}
					
					if(input.split(";").length == 2) {
						if(input.split(";")[0].equals("ready")) {
							int p = port + 1;
							for(int i = 0; i < 2 ; i++) {
								//sendToPhone(START_SESSION_INSTRUCTION, i);
								send(START_SESSION_INSTRUCTION, phoneIps[i], p);
								//sendToClientSimulator(START_SESSION_INSTRUCTION, i);
							}
							
							//sendToPhone(ACK_INSTRUCTION, 1);
							//send(START_SESSION_INSTRUCTION, phoneIps[0], port+1);
							input = "";
						}
						
						if(input.split(";")[0].equals(TIMEOUT_INSTRUCTION)) {
							sendToPhone(TIMEOUT_ACK_INSTRUCTION, Integer.parseInt(input.split(";")[1]));
							sendToClientSimulator(TIMEOUT_ACK_INSTRUCTION, Integer.parseInt(input.split(";")[1]));
						}
						
						if(input.split(";")[0].equals(HIGHSCORE_INSTRUCTION)) {
							sendToPhone(game.getHighscoreList(), Integer.parseInt(input.split(";")[1]));
							sendToClientSimulator(game.getHighscoreList(), Integer.parseInt(input.split(";")[1]));
						}
					}
					
					if(input.equals(START_GAME_INSTRUCTION)) {
						resetGame(game);
						game.setTimer();
						hasStartedGame = true;
						game.update(input);
					}
					if(input.split(";")[0].equals(CHOOSE_MAX_SCORE_REQUEST_INSTRUCTION)) {
						send("scorelmit set", phoneIps[0], port);
						int maxScore = Integer.parseInt(input.split(";")[2]); //scorelimit
						game.setMaxScore(maxScore);
						
					}
				}
				else {
					if(!game.realTime) game.update(input);
				}
				
				if(input.split(";")[0].equals(LOG_OUT_INSTRUCTION)) {
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
	
	/**
	 * Get the players in the game
	 * @return the players
	 */
	public Player[] getPlayers() {
		return this.game.getPlayers();
	}
	
	/**
	 * Get the history of sent data
	 * @return the history of sent data
	 */
	public String getSentHistory() {
		return sentHistory;
	}
	
	/**
	 * Get the history of rescived data
	 * @return the history of rescived data
	 */
	public String getInputHistory() {
		return inputHistory;
	}
	
	/**
	 * Turn of the game
	 */
	public void endGame() {
		game.closeGame = true;
	}
	
	/**
	 * Check if a client has local ip-addreess
	 * @param index the index of the client
	 * @return if it's a local ip-address
	 */
	public boolean isLocalAddress(int index) {
		return phoneIps[index].equals("127.0.0.1");
	}
	
	/**
	 * Turn the data from a UDP packet into a string with the data translated to chars
	 * @param t the data
	 * @param l the length of data taken
	 * @return a string of the data converterd
	 */
	public static String putTogether(byte[] t, int l) {
		String tmp = "";
		
		for(int i = 0; i < l; i++) {
			tmp += (char)t[i];
		}
		
		return tmp;
	}
	
	/**
	 * Take all the data recisved, reverse it while turning it into chars and traverse until no more zeros 
	 * keep everything past that point and reverse again and return
	 * @param t the data
	 * @return the string of data
	 */
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