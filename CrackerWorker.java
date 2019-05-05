import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CrackerWorker implements Runnable{
    /* symbols to start the strings with */
    private String firstSymbol;

    /* all possible characters */
    private char[] allSymbols;

    /* hash to guess */
    private byte[] hashToGuess;

    /* hashing tool */
    private MessageDigest d;

    /* maximum length of the string */
    private int maxLength;

    public CrackerWorker(int maxLength, String fSymbols, char[] all, byte[] hashToGuess){
        firstSymbol = fSymbols;
        allSymbols = all;
        this.hashToGuess = hashToGuess;
        this.maxLength = maxLength;
        try {
            d = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (int i=0; i<firstSymbol.length(); ++i){
            guess(String.valueOf(firstSymbol.charAt(i)));
        }
    }

    /**
     * This method makes guesses for all possible combination of characters
     *
     * @param cur current guess
     * */
    private void guess(String cur) {
        byte[] currentHash = d.digest(cur.getBytes());
        if (Arrays.equals(currentHash, hashToGuess)){
            System.out.println(cur);
        }
        if (cur.length() == maxLength){
            return;
        }
        for (int i=0; i<allSymbols.length; ++i){
            guess(cur + allSymbols[i]);
        }
    }
}
