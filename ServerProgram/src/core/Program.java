package core;

import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;

public class Program {
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
	
	public static String getStack() {
		String tmp = "";
		
		StackTraceElement[] s = Thread.currentThread().getStackTrace();
		
		for(int i = 0; i < s.length; i++) {
			tmp += "{ " + s[i].getFileName() + " | "+ s[i].getMethodName() + " | " + s[i].getLineNumber() + " }\n";
		}
		
		return tmp;
	}
}
