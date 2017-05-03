package game;

public class HighscoreList {
	private final int AMOUNT_OF_HIGHSCORE = 8;
	
	private HighscoreEntry[] entries;
	
	public HighscoreList() {
		entries = new HighscoreEntry[AMOUNT_OF_HIGHSCORE];
		for(int i = 0; i < entries.length; i++) {
			entries[i] = new HighscoreEntry("", "");
		}
	}
	
	public boolean tryAdd(HighscoreEntry e) {
		HighscoreEntry[] tmp = new HighscoreEntry[AMOUNT_OF_HIGHSCORE];
		
		int offset = 0;
		
		for(int i = 0; i < entries.length; i++) {
			if(((entries[i].getScore().equals("") || e.compare(entries[i]) == -1))&& offset == 0) {
				tmp[i] = new HighscoreEntry(e.getName(), e.getScore());
				offset = 1;
			} else {
				tmp[i] = new HighscoreEntry(entries[i-offset].getName(), entries[i-offset].getScore());
			}
		}
		
		entries = tmp;
		
		return offset != 0;
	}
	
	public void load(String path) {
		
	}
	
	public String toString() {
		String tmp = "";
		for(int i = 0; i < entries.length; i++) {
			tmp += (i+1) + " { " + entries[i].getName() + " | " + entries[i].getScore() + " } \n";
		}
		return tmp;
	}
}