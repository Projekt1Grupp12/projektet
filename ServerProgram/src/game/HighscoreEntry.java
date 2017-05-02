package game;

public class HighscoreEntry {
	private String name;
	private String score;
	
	public HighscoreEntry(String name, String score) {
		this.name = name;
		this.score = score;
	}
	
	public HighscoreEntry(HighscoreEntry h) {
		this.name = new String(h.getName());
		this.score = new String(h.getScore());
	}
	
	public String getName() {
		return name;
	}
	
	public String getScore() {
		return score;
	}
	
	public int compare(HighscoreEntry h) {
		if(Long.parseLong(score) == Long.parseLong(h.getScore())) return 0;
		else if(Long.parseLong(score) > Long.parseLong(h.getScore())) return 1;
		else return -1;
	}
}
