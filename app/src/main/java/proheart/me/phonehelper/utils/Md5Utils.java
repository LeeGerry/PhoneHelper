package proheart.me.phonehelper.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {


	public static String encode(String text) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] result = digester.digest(text.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				int number = b & 0xff;
				String hexStr = Integer.toHexString(number);
				if(hexStr.length()==1){
					sb.append("0");
				}
				sb.append(hexStr);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

			return "";
		}

	}
}
