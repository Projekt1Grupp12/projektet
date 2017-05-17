package core;

import java.util.Random;

/**
 * Better random that checks the last random and makes it something diffrenet if its the smae
 * @author tom.leonardsson
 *
 */
public class BetterRandom {
	private static int lastResult = -999;
	
	private static Random random = new Random();
	
	/**
	 * Pick a random value between the max and min that it is not the last value
	 * @param min the minumum value generated
	 * @param max the maximum value generated
	 * @return
	 */
	public static int random(int min, int max) {
		int r = random.nextInt(max-min)+min;
		
		if(lastResult != -999) {
			while(r == lastResult)
				r = random.nextInt(max-min)+min;
		}
		
		return (lastResult = r);
	}
	
	/**
	 * Just counts 2^x
	 * @param x the value to raise up 2 by
	 * @return 2^x
	 */
	public static int powerOfTwo(int x) {
		int r = 2;
		
		for(int i = 0; i < x; i++) {
			r *= 2;
		}
		
		return r/2;
	}
}
