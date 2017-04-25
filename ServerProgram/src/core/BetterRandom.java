package core;

import java.util.Random;

public class BetterRandom {
	private static int lastResult = -999;
	
	private static Random random = new Random();
	
	public static int random(int min, int max) {
		int r = random.nextInt(max-min)+min;
		
		if(lastResult != -999) {
			while(r == lastResult)
				r = random.nextInt(max-min)+min;
		}
		
		return (lastResult = r);
	}
}
