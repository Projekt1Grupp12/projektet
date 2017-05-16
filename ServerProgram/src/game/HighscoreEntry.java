package game;

/**
 * A highscore entry for the highscore list, keeps track of the name and score
 * @author tom.leonardsson
 *
 */
public class HighscoreEntry {
	private String name;
	private String score;
	
	/**
	 * Create a highscore list entry with a specifc name and score
	 * @param name the name
	 * @param score the score
	 */
	public HighscoreEntry(String name, String score) {
		this.name = name;
		this.score = score;
	}
	
	/**
	 * create a new entry by copying another
	 * @param h the entry
	 */
	public HighscoreEntry(HighscoreEntry h) {
		this.name = new String(h.getName());
		this.score = new String(h.getScore());
	}
	
	/**
	 * get the name of the entry
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * get the score of the entry
	 * @return the score
	 */
	public String getScore() {
		return score;
	}
	
	/**
	 * Compare two highscore entries to see which is higher
	 * @param h
	 * @return the result of the compare
	 */
	public int compare(HighscoreEntry h) {
		if(Long.parseLong(score) == Long.parseLong(h.getScore())) return 0;
		else if(Long.parseLong(score) > Long.parseLong(h.getScore())) return 1;
		else return -1;
	}
	
	/**
	 * Return a structured string of the highscore entry
	 */
	public String toString() {
		return getName() + "-" + getScore();
	}
}
