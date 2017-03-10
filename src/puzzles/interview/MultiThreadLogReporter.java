package puzzles.interview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author wb-lw252418
 *
 * QUESTION: use java coding implement cat "/log/*.log" | grep "Login" | uniq -c | sort -nr
 * use MultiThread.
 * 
 * TODO  nio read big file. ensure string's integerity
 */
public class MultiThreadLogReporter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

class FileReader implements Runnable {

	private static final String KEY = "Login";
	private String filePath;
	FileReader(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public void run() {
		System.out.println("new Thread : FileReader running.");
		File path = new File(filePath);
		if(!path.exists() || !path.isDirectory()) {
			return;
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		};
		File[] files = path.listFiles(filter);
		if(null == files) {
			return;
		}
		BufferedReader br = null;
		String line = null;
		for(File f : files) {
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				while ((line = br.readLine()) != null) {
					if(line.contains(KEY)) {
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(null != br) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					br = null;
				}
			}
		}
	}
	
}
