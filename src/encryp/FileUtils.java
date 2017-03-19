package encryp;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class FileUtils {

	public static final String ENC_NAME = "D:/spring-source.zip";
	private static final String SRC_NAME = "D:/mybaks.7z";
	
	public static void copy(File src, String target) throws Exception {
		ByteBuffer bb = ByteBuffer.allocate(1024 << 19);
		FileChannel fc_src = FileChannel.open(src.toPath(), EnumSet.of(StandardOpenOption.READ));
		File f_target = new File(target);
		if(!f_target.exists()) {
			f_target.createNewFile();
		}
		FileChannel fc_target = FileChannel.open(f_target.toPath(), EnumSet.of(StandardOpenOption.WRITE));
		fc_src.read(bb);
		bb.flip();
		fc_target.write(bb);
		bb.clear();
		fc_src.close();
		fc_target.close();
	}
	
	public static void writeFile(String content, String fileName) throws Exception {
		File f_target = new File(fileName);
		if(!f_target.exists()) {
			f_target.createNewFile();
		}
		ByteBuffer bb = ByteBuffer.wrap(content.getBytes("UTF-8"));
		FileChannel fc = FileChannel.open(f_target.toPath(), EnumSet.of(StandardOpenOption.WRITE));
		fc.write(bb);
		bb.clear();
		fc.close();
	}
	
	public static void zip() throws Exception {
		String[] cmd = new String[]{"cmd", "/c", "D:\\\"Program Files\"\\7-Zip\\7z", "a", "D:\\mybaks.7z", "D:\\mybaks\\*"};
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
	}
	
	public static void del() throws Exception {
		String[] cmd = new String[]{"cmd", "/c", "del /S /F /Q D:\\mybaks\\*"};
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		String[] cmd2 = new String[]{"cmd", "/c", "rd D:\\mybaks"};
		p = Runtime.getRuntime().exec(cmd2);
		p.waitFor();
	}
	
	public static void enc() throws IOException {
		try {
			verse(SRC_NAME, ENC_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deEnc() {
		try {
			verse(ENC_NAME, SRC_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void verse(String src, String target) throws IOException {
		File f = new File(src);
		if(!f.exists()) {
			return;
		}
		ByteBuffer bb = ByteBuffer.allocate(1024 << 18);
		FileChannel fc = FileChannel.open(f.toPath(), EnumSet.of(StandardOpenOption.READ));
		fc.read(bb);
		byte[] arr = bb.array();
		for(int i = 0; i < arr.length; i++) {
			arr[i] = (byte) ~arr[i];
		}
		bb.flip();
		File outFile = new File(target);
		if(!outFile.exists()) {
			outFile.createNewFile();
		}
		FileChannel out = FileChannel.open(outFile.toPath(), EnumSet.of(StandardOpenOption.WRITE));
		out.write(bb);
		bb.clear();
		fc.close();
		out.close();
	}
}
