package core;

import javax.swing.JPanel;

public class ServerController {
	public ServerController() {
		new Thread(new UDPServer()).start();
	}
}
