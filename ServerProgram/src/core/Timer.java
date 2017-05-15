package core;

/**
 * Timer used to "count" the time between two time 
 * @author tom.leonardsson
 *
 */
public class Timer {
	private long start;
	private long end;
	
	/**
	 * Set the start to the current time
	 */
	public void start() {
		start = System.currentTimeMillis();
	}
	
	/**
	 * Set the end time to now
	 * @return the diffrence between start and end
	 */
	public long end() {
		end = System.currentTimeMillis();
		
		return getDelta();
	}
	
	/**
	 * Get the diffrence betweend the end time and the start time
	 * @return the diffrence betweend the end time and the start time
	 */
	private long getDelta() {
		return end - start;
	}
}