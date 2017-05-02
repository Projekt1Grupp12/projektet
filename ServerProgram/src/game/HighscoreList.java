package game;

public class HighscoreList {
	private final int AMOUNT_OF_HIGHSCORE = 8;
	
	private HighscoreEntry[] entries;
	
	public HighscoreList() {
		entries = new HighscoreEntry[AMOUNT_OF_HIGHSCORE];
	}
	
	public boolean tryAdd(HighscoreEntry e) {
		HighscoreEntry[] tmp = new HighscoreEntry[AMOUNT_OF_HIGHSCORE];
		
		int offset = 0;
		
		for(int i = 0; i < entries.length; i++) {
			if(e.compare(entries[i]) == -1) {
				tmp[i] = new HighscoreEntry(e);
				offset = 1;
			} else {
				tmp[i] = new HighscoreEntry(entries[i-offset]);
			}
		}
		
		return offset != 0;
	}
	
	public HighscoreEntry[] load(String path) {
		return null;
	}
}