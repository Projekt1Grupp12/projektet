package core;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import game.Player;
import game.PuzzelGame;

public class UDPServer implements Runnable
{	
	boolean recsive = true;
	
	private DatagramSocket serverSocket = null;
	
	private String[] phoneIps;
	private String ardurinoIp;
	private String sentHistory;
	
	private int port;
	private int sentHistoryIndex;
	
	byte[] receiveData;
	
	DatagramPacket packet;
	
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
	
	public byte[] getRecived(DatagramSocket from) throws IOException {
		from.receive(packet);
		return receiveData;
	}
	
	PuzzelGame game = new PuzzelGame(new Player[]{new Player(), new Player()}, this);
	
	public void run() {
		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		while(true) {
			try {
				game.update();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//testProgram();
	}
	
	public void testProgram() {
		int delay = 0;
		int x = 0;
		
		String d = "0";
		
		Random random = new Random();
		
		int newData = 0;
		int oldData = 0;
		
		InetAddress phoneIPAddress = null;
		InetAddress ardIPAddress = null;
		
		while(true)
		{
			try {
				phoneIPAddress = InetAddress.getByName("10.2.29.150");
				ardIPAddress = InetAddress.getByName("192.168.0.2");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			d = x + "";

			delay += 1;
			
			//System.out.println(oldData + " | " + newData  + " | " + (delay > 8 && newData != oldData));
			//x = random.nextInt(64);
			//d = x + "";
			if(delay > 8) {
			    try {
			    	send(d, "192.168.0.2");
				} catch (IOException e) {
					e.printStackTrace();
				}
				oldData = newData;
				x += 1;
				if(x >= 7) x = 0;
				//System.out.println(d + " | state");
				delay = 0;
			}
			
			if(random.nextInt(1000) == 500 && recsive) { 
				try {
					//serverSocket.receive(packet);
					this.getRecived(serverSocket);
					System.out.println(putTogether(packet.getData(), 6) + " | tillbaka");
				} catch (IOException e) {
					e.printStackTrace();
				}
				//newData = Integer.parseInt(putTogether(packet.getData(), 3));
				//System.out.println(Integer.parseInt(putTogether(packet.getData(), 3)) + " | tillbaka");
			}
		}
	}
	
	public String getSentHistory() {
		return sentHistory;
	}
	
	public String putTogether(byte[] t, int l) {
		String tmp = "";
		
		for(int i = 0; i < l; i++) {
			tmp += (char)t[i];
		}
		
		return tmp;
	}
}