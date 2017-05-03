package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class with one method to load data from files
 * @author tom.leonardsson
 *
 */
public class ResourceReader {
	/**
	 * Get the elements on a specifc line in a file at a specif path, split at every ";"
	 * @param path the path to the file
	 * @param line the line to read
	 * @return an array of strings that holds every piece of the line
	 * @throws IOException
	 */
	public static String[] getValuesOnLine(String path, int line) throws IOException {
		String[] tmp = null;
		String l = "";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path + ".txt"));
			int count = 0;
			
			l = br.readLine();
			
			while(l != null) {
				if(count == line) break;
				l = br.readLine();
				count += 1;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(l != null)
			tmp = l.split(";");
		
		return tmp;
	}
}
