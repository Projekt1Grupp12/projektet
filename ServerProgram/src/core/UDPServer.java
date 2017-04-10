package core;

import java.io.*;
import java.net.*;
import java.util.Random;

import javax.swing.JOptionPane;

class UDPServer implements Runnable
{	
	boolean recsive;
	
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
		
		sentHistory = message + " : " + (sentHistoryIndex++) + "\n" + sentHistory;
	}
	
	public void sendToPhone(String message, int index) throws IOException {
		send(message, phoneIps[index]);
	}
	
	public void sendToArdurino(String message) throws IOException {
		send(message, ardurinoIp);
	}
	
	public byte[] getRecived() throws IOException {
		serverSocket.receive(packet);
		return receiveData;
	}

	public void run() {
		try {
			serverSocket = new DatagramSocket(4444);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] sendData = new byte[1024];
		
		int delay = 0;
		int resciveDelay = 0;
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

			DatagramPacket sendPacket = new DatagramPacket(d.getBytes(), d.getBytes().length, ardIPAddress, 4444);
			delay += 1;
			
			//System.out.println(oldData + " | " + newData  + " | " + (delay > 8 && newData != oldData));
			
			if(delay > 8) {
			    /*try {
			    	send(d, "192.168.0.2");
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				oldData = newData;
				x += 1;
				if(x >= 7) x = 0;
				System.out.println(d + " | state");
				delay = 0;
			}
			
			if(random.nextInt(1000) == 500 && recsive) { 
				try {
					serverSocket.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
		
		for(int i = l-1; i >= 0; i--) {
			tmp += (char)t[i];
		}
		
		return tmp;
	}
}