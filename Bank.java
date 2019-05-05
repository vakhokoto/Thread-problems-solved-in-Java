import java.io.*;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Bank {
    /* number of accounts */
    private static final int ACCOUNT_NUMBER = 20;

    /* accounts initial amount */
    private static final int INITIAL_AMOUNT = 1000;

    /* BlockingQueue initial size */
    private static final int INITIAL_CAPACITY = 100;

    private static Account[] accounts;
    private static BlockingQueue <Bank.Transaction> queue;
    private static Thread[] operators;
    private static int operatorNum;
    private static BufferedReader reader;

    public static void main(String[] args){
        if (args.length < 2){
            return;
        }
        String fileName = args[0];
        operatorNum = Integer.parseInt(args[1]);
        try {
            reader = new BufferedReader(new FileReader(fileName));
            doJob();
            if (!fileName.equals("small.txt")){
                for (int i=0; i<ACCOUNT_NUMBER; ++i){
                    if (accounts[i].getBalance() != INITIAL_AMOUNT){
                        System.out.println("Not correct balance - " + accounts[i].getID() + " -- " + accounts[i].getBalance());
                    }
                }
            } else {
                for (int i=0; i<ACCOUNT_NUMBER; ++i){
                    System.out.println("id:" + accounts[i].getID() + " balance: "
                            + accounts[i].getBalance() + " transactions: " + accounts[i].getTransactionNumber());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * starts running Operator threads and does the job
     * also calls startRead to feed threads with information
     * */
    private static void doJob() {
        accounts = new Account[ACCOUNT_NUMBER];
        for (int i=0; i<accounts.length; ++i){
            accounts[i] = new Account(INITIAL_AMOUNT, i);
        }
        queue = new ArrayBlockingQueue<>(INITIAL_CAPACITY);
        operators = new Thread[operatorNum];
        for (int i=0; i<operatorNum; ++i){
            operators[i] = new Thread(new Operator(i, queue, accounts));
        }
        for (int i=0; i<operatorNum; ++i){
            operators[i].start();
        }
        startRead();
    }

    /**
     * starts reading of transaction info and feed with it operators
     * */
    private static void startRead() {
        while (true){
            String line = null;
            try {
                line = reader.readLine();
                if (line == null){
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            int[] transactionInfo = parseLine(line);
            int fromID = transactionInfo[0];
            int toID = transactionInfo[1];
            int amount = transactionInfo[2];
            Transaction t = new Transaction(fromID, toID, amount);
            try {
                queue.put(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i=0; i<operatorNum; ++i){
            try {
                queue.put(new Transaction(-1, -1, 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i=0; i<operatorNum; ++i){
            try {
                operators[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method parses transaction information line into int array
     *
     * @param line String to be parsed
     * @return int array parsed from the line
     * */
    private static int[] parseLine(String line){
        int[] ans = new int[3];
        StringTokenizer tokenizer = new StringTokenizer(line);
        int pos = 0;
        while (tokenizer.hasMoreTokens()){
            ans[pos++] = Integer.parseInt(tokenizer.nextToken());
        }
        return ans;
    }

    /* this class is to store transaction information */
    public static class Transaction{
        private int from, to, amount;

        public Transaction(int fromAccountID, int toAccountID, int initialAmount){
            from = fromAccountID;
            to = toAccountID;
            amount = initialAmount;
        }

        /**
         * Returns amount of transaction
         *
         * @return int destination account ID
         * */
        public int getAmount(){
            return amount;
        }

        /**
         * Returns destination account ID
         *
         * @return int destination account ID
         * */
        public int getDestinationID(){
            return to;
        }

        /**
         * Returns source account ID
         *
         * @return int source account ID
         * */
        public int getSourceID(){
            return from;
        }
    }
}
