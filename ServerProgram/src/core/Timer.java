package core;

public class Timer {
	private long start;
	private long end;
	
	public void start() {
		start = System.currentTimeMillis();
	}
	
	public long end() {
		end = System.currentTimeMillis();
		
		return getDelta();
	}
	
	private long getDelta() {
		return end - start;
	}
}