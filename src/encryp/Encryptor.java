package encryp;

import java.io.File;

public class Encryptor {

	public void doEnc(File from, File to) throws Exception {
		String projectRoot = "D:/front_works/aegis_sat_front";
		new ProjectManager().copyForBak(projectRoot);
		//zip
		FileUtils.zip();
		FileUtils.del();
		//encrypt
		FileUtils.enc();
		
//		FileUtils.deEnc();
	}
	
	public static void main(String[] args) {
		try {
			new Encryptor().doEnc(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
