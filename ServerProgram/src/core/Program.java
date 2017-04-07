package core;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Program {
	public static void main(String[] args) {
		ServerView viewer = new ServerView(new ServerController());
		JFrame frame = new JFrame("Server");
		frame.setPreferredSize(new Dimension(640, 480));
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add(viewer);
		frame.setResizable(true);
		frame.pack();
		frame.setVisible(true);
	}
}
