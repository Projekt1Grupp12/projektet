package game;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import core.ResourceReader;

public class HighscoreList {
	private final int AMOUNT_OF_HIGHSCORE = 8;
	
	private HighscoreEntry[] entries;
	
	public HighscoreList(String path) {
		entries = new HighscoreEntry[AMOUNT_OF_HIGHSCORE];
		for(int i = 0; i < entries.length; i++) {
			entries[i] = new HighscoreEntry("", "");
		}
		
		try {
			load(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean tryAdd(HighscoreEntry e) {
		HighscoreEntry[] tmp = new HighscoreEntry[AMOUNT_OF_HIGHSCORE];
		
		int offset = 0;
		
		for(int i = 0; i < AMOUNT_OF_HIGHSCORE; i++) {
			if(((entries[i].getScore().equals(" ") || e.compare(entries[i]) == -1))&& offset == 0) {
				tmp[i] = new HighscoreEntry(e.getName(), e.getScore());
				offset = 1;
			} else {
				tmp[i] = new HighscoreEntry(entries[i-offset].getName(), entries[i-offset].getScore());
			}
		}
		
		entries = tmp;
		
		return offset != 0;
	}
	
	public void load(String path) throws IOException {
		if(ResourceReader.getValuesOnLine(path, 0,"-") == null) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
			try {
				for(int i = 0; i < AMOUNT_OF_HIGHSCORE; i++) {
					writer.write(" " + "-" + " ");
					writer.newLine();
				}
			} catch(Exception e) {
				
			} finally {
				writer.flush();
				writer.close();
			}
		} else {
			for(int i = 0; i < AMOUNT_OF_HIGHSCORE; i++) {
				String[] r = ResourceReader.getValuesOnLine(path, i,  "-");
				entries[i] = new HighscoreEntry(r[0], r[1]);
			}
		}
	}
	
	public void save(String path) throws IOException {
		PrintWriter writer = new PrintWriter(path);
		writer.print("");
		writer.close();
		
		BufferedWriter bufferdWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
		try {
			for(int i = 0; i < AMOUNT_OF_HIGHSCORE; i++) {
				bufferdWriter.write(entries[i].toString());
				bufferdWriter.newLine();
			}
		} catch(Exception e) {
			
		} finally {
			bufferdWriter.flush();
			bufferdWriter.close();
		}
	}
	
	public String toString() {
		String tmp = "";
		for(int i = 0; i < AMOUNT_OF_HIGHSCORE; i++) {
			tmp += (i+1) + " { " + entries[i].getName() + " | " + entries[i].getScore() + " } \n";
		}
		return tmp;
	}
}