
public class Cracker {
	/* Array of chars used to produce strings */
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();

	/* number of workers to crack */
	private static int workerNum;

	/* worker threads */
	private static Thread[] workers;

	public static void main(String[] args){
		if (args.length != 3){
			return;
		}

		/* hash of string to guess */
		byte[] h = hexToArray(args[0]);

		/* max length of string */
		int maxLength = Integer.parseInt(args[1]);

		workerNum = Integer.parseInt(args[2]);
		workers = new Thread[workerNum];
		int start = 0;
		for (int i=0; i<workerNum; ++i){
			int l = CHARS.length / workerNum + (i == workerNum - 1 ?CHARS.length % workerNum:0);
			char[] ch = new char[l];
			for (int j=start; j<start + l; ++j){
				ch[j - start] = CHARS[j];
			}
			start += l;
			CrackerWorker w = new CrackerWorker(maxLength, new String(ch), CHARS, h);
			workers[i] = new Thread(w);
		}
		for (int i=0; i<workerNum; ++i){
			workers[i].start();
		}
		for (int i=0; i<workerNum; ++i){
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("All done.");
	}

	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
}
