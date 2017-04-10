package core;

import javax.swing.JPanel;

public class ServerController {
	private UDPServer server;
	
	public ServerController() {
		server = new UDPServer(4444, new String[]{"10.2.29.150", "0.0.0.0"}, "192.168.0.2");
		new Thread(server).start();
	}
	
	public UDPServer getServer() {
		return server;
	}
	
	public String getSentHistory() {
		return server.getSentHistory();
	}
}
