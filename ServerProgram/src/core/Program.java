package core;

import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;

/**
 * Runs the program
 * @author tom.leonardsson
 *
 */
public class Program {
	/**
	 * Create a GUI that starts the server
	 * @param args
	 */
	public static void main(String[] args) {
		ServerView viewer = new ServerView(new ServerController());
		JFrame frame = new JFrame("Server");
		frame.setPreferredSize(new Dimension(648, 480));
		frame.setResizable(false);
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add(viewer);
		frame.setResizable(true);
		frame.pack();
		frame.setVisible(true);
	}
}
