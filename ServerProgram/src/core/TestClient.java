package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class TestClient implements Runnable {
	Socket socket;
    PrintWriter out;
    BufferedReader in;
    
    public TestClient(String ipAddress, int port) throws UnknownHostException, IOException
    {
    	socket = new Socket(ipAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(0);
        new Thread(this).start();
    }

	public void run() {
		 String m;
		 out.println(1);
		 
	     try {
	    	 while ((m = in.readLine()) != null) {
	    		 out.println(2);
	    		 System.out.println(m + " | client");
	         }
	            
	         socket.close();
	         out.close();
	         in.close();
	     } catch (IOException e) {
	         System.out.println(e);
	     }
	}
	
	public static void main(String[] args) throws NumberFormatException, UnknownHostException, IOException {
		new TestClient("10.2.19.28", Integer.parseInt("4444"));
	}
}
