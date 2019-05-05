import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class TestClass {
    private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
    private static Random rgen;
    private static final int ATTEMPT = 40;
    private static MessageDigest d;

    public static void main(String[] args){
        rgen = new Random();
        int at = Math.abs(rgen.nextInt()) % ATTEMPT;
        try {
            d = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        for (int i=0; i<at; ++i){
            int l = Math.abs(rgen.nextInt()) % 6;
            String s = "";
            for (int j=0; j<l; ++j){
                s += CHARS[Math.abs(rgen.nextInt()) % CHARS.length];
            }
            String hash = Cracker.hexToString(d.digest(s.getBytes()));
            System.out.println("String to crack -- " + l + " " + s + " " + hash);
            String[] arg = new String[]{hash, "5", "8"};
            Cracker.main(arg);
        }
    }
}
