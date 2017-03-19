package encryp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ProjectManager {
	private static final List<String> without = new ArrayList<>();
	private static final List<String> withoutSuffix = new ArrayList<>();
	public static final String TR = "D:/mybaks/";
	private static final String META = "META.txt";
	static {
		without.add(".git");
		without.add(".idea");
		without.add(".gitignore");
		without.add("deploy.sh");
		without.add(".DS_Store");
		without.add("README");
		
		withoutSuffix.add(".png");
		withoutSuffix.add(".gif");
		withoutSuffix.add(".jpg");
		withoutSuffix.add(".ico");
	}
	
	public void copyForBak(String projectRoot) {
		File root = new File(TR);
		if(!root.exists()) {
			root.mkdir();
		}
		File f = new File(projectRoot);
		if(!f.exists() || !f.isDirectory()) {
			return;
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String path = dir.getAbsolutePath();
				if(path.indexOf("\\dist") > 0 || path.indexOf("\\node_modules") > 0) {
					return false;
				}
				for(String s : withoutSuffix) {
					if(name.endsWith(s)) {
						return false;
					}
				}
				for(String s : without) {
					if(s.equals(name)) {
						return false;
					}
				}
				return true;
			}
		};
		StringBuilder dirStruct = new StringBuilder();
		dirStruct.append("[root] " + f.getName()).append("\r\n");
		copyFileBak(f, filter, 0, dirStruct);
		try {
			FileUtils.writeFile(dirStruct.toString(), TR + META);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void copyFileBak(File parent, FilenameFilter filter, int depth, StringBuilder dirStruct) {
		if(null == parent || !parent.isDirectory()) {
			return;
		}
		File[] fs = parent.listFiles(filter);
		String newFileName = null;
		String target = null;
		for(File f : fs) {
			for(int i = 0; i < depth; i++) {
				dirStruct.append("\t");
			}
			if(f.isDirectory()) {
				dirStruct.append("[d] " + f.getName()).append("\r\n");
				copyFileBak(f, filter, depth + 1, dirStruct);
			} else {
				dirStruct.append(f.getName()).append("\r\n");
				newFileName = f.getName() + "_bak";
				target = TR + newFileName;
				try {
					FileUtils.copy(f, target);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(f.getName() + "\tcopied!");
			}
		}
	}

	public void listAll(String projectRoot) {
		File f = new File(projectRoot);
		if(!f.exists() || !f.isDirectory()) {
			return;
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				for(String s : withoutSuffix) {
					if(name.endsWith(s)) {
						return false;
					}
				}
				for(String s : without) {
					if(s.equals(name)) {
						return false;
					}
				}
				return true;
			}
		};
		listFile(f, filter, 0);
	}
	
	private void listFile(File parent, FilenameFilter filter, int depth) {
		if(null == parent || !parent.isDirectory()) {
			return;
		}
		File[] fs = parent.listFiles(filter);
		for(File f : fs) {
			StringBuilder sb = new StringBuilder();
			if(f.isDirectory()) {
				for(int i = 0; i < depth; i++) {
					sb.append("\t");
				}
				sb.append("[d] " + f.getName());
				System.out.println(sb.toString());
				listFile(f, filter, depth + 1);
			} else {
				for(int i = 0; i < depth; i++) {
					sb.append("\t");
				}
				sb.append(f.getName());
				System.out.println(sb.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		String projectRoot = "D:/front_works/auiplug";
//		new ProjectManager().listAll(projectRoot);
		new ProjectManager().copyForBak(projectRoot);
	}
}
