package puzzles.interview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author wb-lw252418
 *
 * QUESTION: use java coding implement cat "/log/*.log" | grep "Login" | uniq -c | sort -nr
 * use MultiThread.
 * 
 * TODO  nio read big file. ensure string's integerity
 */
public class MultiThreadLogReporter {
	
//	private static final String FILE_PATH = "/log/*.log";
	private static final String FILE_PATH = "/var/log";
	private static final String EOF= "nullll";
	private static int count = 2;
	private static CountDownLatch latch = new CountDownLatch(count);

	public static void main(String[] args) {
		ExecutorService service = Executors.newFixedThreadPool(4);
		Hub hub = new Hub();
		Map<String, Integer> map = new HashMap<String, Integer>();
		service.submit(new FileSearcher(FILE_PATH, hub));
		service.submit(new UniqDealer(hub, map));
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("start sort!");
		sortReverse(map);
		service.shutdown();
	}
	
	private static void sortReverse(Map<String, Integer> map) {
		Set<Entry<String, Integer>> eSet = map.entrySet();
		Set<String> result = new TreeSet<String>();
		for(Entry<String, Integer> e : eSet) {
			result.add(e.getValue().intValue() + " " + e.getKey());
		}
		List<String> l = new ArrayList<String>();
		l.addAll(result);
		Collections.reverse(l);
		for(String s : l) {
			System.out.println("\t" + s);
		}
	}

	static class Hub {
		private BlockingQueue<String> queue = new LinkedBlockingDeque<String>(1000);
		public void inHub(String str) throws InterruptedException {
			queue.put(str);
		}
		public String outHub() throws InterruptedException {
			return queue.take();
		}
		public boolean isEmpty() {
			return queue.isEmpty();
		}
	}
	
	static class UniqDealer implements Runnable {

		private Map<String, Integer> map;
		private Hub hub;
		
		UniqDealer(Hub hub, Map<String, Integer> map) {
			this.hub = hub;
			this.map = map;
		}
		
		@Override
		public void run() {
			System.out.println("start uniqDealer!");
			String line = null;
			Integer count = null;
			while(true) {
				try {
					line = hub.outHub();
				} catch (InterruptedException e) {
					System.out.println("outHub been interrupt!!");
				}
				if(EOF.equals(line)) {
					break;
				}
				if(null == line) {
					System.out.println("you should not have seen this!");
					continue;
				}
				count = map.get(line);
				if(null == count) {
					map.put(line, 1);
				} else {
					map.put(line, count.intValue() + 1);
				}
			}
			System.out.println("uniqDealer done!");
			latch.countDown();
		}
		
	}
	
	static class FileSearcher implements Runnable {

		private static final String KEY = "Login";
		private String filePath;
		private Hub hub;
		
		FileSearcher(String filePath, Hub hub) {
			this.filePath = filePath;
			this.hub = hub;
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
						if(null == line || "".equals(line)) {
							continue;
						}
						if(line.contains(KEY)) {
							try {
								hub.inHub(line);
							} catch (InterruptedException e) {
								System.out.println("inHub been interrupted!!");
							}
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
			System.out.println("file reader done.");
			try {
				hub.inHub(EOF);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			latch.countDown();
		}
	}
}