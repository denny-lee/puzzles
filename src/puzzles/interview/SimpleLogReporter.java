package puzzles.interview;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author wb-lw252418
 *
 * QUESTION: use java coding implement cat "/log/*.log" | grep "Login" | uniq -c | sort -nr
 * 
 * TODO  nio read big file. ensure string's integerity
 */
public class SimpleLogReporter {

	private static final String LINE_END = "\r\n";
	
	public static void main(String[] args) {
//		String filePattern = "/log/*.log";
//		String filePattern = "/home/admin/output/*.log";
		String filePattern = "D:/testLog/*.log";
//		String keyWord = args[0];
		String keyWord = "Login";
		// cat log
		List<String> logs = fetchAllLogs(filePattern);
		/*for(String content : logs) {
			System.out.println(content);
		}*/
		// grep login
		List<String> loginItem = new ArrayList<String>();
		for(String content : logs) {
			grepStringByLine(content, keyWord, loginItem);
		}
		/*for(String s : loginItem) {
			System.out.println(content);
		}*/
		// uniq -c
		Map<String, Integer> uniqWithCount = new HashMap<String, Integer>();
		Integer count = null;
		for(String line : loginItem) {
			count = uniqWithCount.get(line);
			if(null == count) {
				uniqWithCount.put(line, 1);
			} else {
				uniqWithCount.put(line, count.intValue() + 1);
			}
		}
		/*Set<Entry<String, Integer>> eSet = uniqWithCount.entrySet();
		for(Entry<String, Integer> e : eSet) {
			System.out.println("\t" + e.getValue().intValue() + " " + e.getKey());
		}*/
		// sort -nr
		System.out.println(uniqWithCount.size());
		Set<Entry<String, Integer>> eSet = uniqWithCount.entrySet();
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
		System.out.println(l.size());
	}

	private static void grepStringByLine(String content, String keyWord, List<String> list) {
		if(null == content) {
			return;
		}
		String[] itemArr = content.split(LINE_END);
		for(String it : itemArr) {
			if(null == it || "".equals(it)) {
				continue;
			}
			if(it.contains(keyWord)) {
				list.add(it.trim());
			}
		}
	}

	private static List<String> fetchAllLogs(String filePattern) {
		List<String> result = new ArrayList<String>();
		File[] files = getFiles(filePattern);
		readFileAsList(files, result);
		return result;
	}

	private static void readFileAsList(File[] files, List<String> result) {
		if(null == files) {
			return;
		}
		FileChannel fc = null;
//		ByteBuffer byteBuf = ByteBuffer.allocate(1024 << 4); // 16M
		ByteBuffer byteBuf = ByteBuffer.allocate(1024 << 4); // 2G-1byte
		int readSize = -1;
		String content = null;
		for(File f : files) {
			try {
				fc = FileChannel.open(f.toPath(), EnumSet.of(StandardOpenOption.READ));
//				while((readSize = fc.read(byteBuf)) > 0) {
//				}
				if((readSize = fc.read(byteBuf)) > 0) {
					byteBuf.flip();
					content = new String(byteBuf.array(), 0, readSize);
					byteBuf.clear();
				}
				result.add(content);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				content = null;
				if(null != fc) {
					try {
						fc.close();
						fc = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static File[] getFiles(String filePattern) {
		File path = new File(filePattern.substring(0, filePattern.lastIndexOf("/")));
		if(!path.exists() || !path.isDirectory()) {
			return null;
		}
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		};
		File[] files = path.listFiles(filter);
		return files;
	}

}
