package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
	ServerSocket serverSocket;
   
	Socket clientSocket;
	
	PrintWriter out;
    BufferedReader in;
	
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
    	out = new PrintWriter(clientSocket.getOutputStream(), true);
    	in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
    	in.readLine();
    	new Thread(this).start();
	}
	
	public void run() {
		String m = "";
		
		try {
			while((m = in.readLine()) != null) {
				out.println(0);
				System.out.println(m + " | server");
			}
			
			in.close();
	        out.close();
	        clientSocket.close();
	        serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Server s = new Server(4444);
	}
}
