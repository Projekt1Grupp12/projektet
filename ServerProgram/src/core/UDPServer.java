package core;

import java.io.*;
import java.net.*;
import java.util.Random;

import javax.swing.JOptionPane;

class UDPServer implements Runnable
{	
	public static String putTogether(byte[] t, int l) {
		String tmp = "";
		
		for(int i = l-1; i >= 0; i--) {
			tmp += (char)t[i];
		}
		
		return tmp;
	}

	public void run() {
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(4444);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		int delay = 0;
		int resciveDelay = 0;
		int x = 0;
		
		String d = "0";
		
		Random random = new Random();
		
		DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
		
		int port = 4444;
		
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//"192.168.0.2");

			d = x + "";
			x += 1;
			System.out.println(x);
			DatagramPacket sendPacket = new DatagramPacket(d.getBytes(), d.getBytes().length, ardIPAddress, 4444);
			delay += 1;
			
			System.out.println(oldData + " | " + newData  + " | " + (delay > 8 && newData != oldData));
			if(delay > 8) {
			    try {
					serverSocket.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				oldData = newData;
				x += 1;
				if(x >= 4) x = 0;
				System.out.println(d + " | state");
				delay = 0;
			}
			
			if(random.nextInt(1000) == 500) { 
				try {
					serverSocket.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				d = putTogether(packet.getData(), 1).trim();
				//newData = Integer.parseInt(putTogether(packet.getData(), 3));
				//System.out.println(Integer.parseInt(putTogether(packet.getData(), 3)) + " | tillbaka");
			}
		}
	}
}