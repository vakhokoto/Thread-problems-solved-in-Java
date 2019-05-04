import java.util.concurrent.BlockingQueue;

public class Operator implements Runnable{
    /* operator ID */
    private int id;

    private BlockingQueue <Bank.Transaction> queue;
    private Account[] accounts;

    public Operator(int operatorId, BlockingQueue <Bank.Transaction> initialQueue, Account[] initialAccounts){
        this.id = operatorId;
        queue = initialQueue;
        accounts = initialAccounts;
    }

    @Override
    public void run() {
        while (true){
            Bank.Transaction currentTransaction = null;
            try {
                currentTransaction = queue.take();
            } catch (InterruptedException e) {
                break;
            }
            if (currentTransaction.getSourceID() == -1 && currentTransaction.getDestinationID() == -1){
                break;
            }
            makeTransaction(currentTransaction);
        }
    }

    /**
     * This method makes the transaction
     * */
    private void makeTransaction(Bank.Transaction currentTransaction) {
        int fromID= currentTransaction.getSourceID();
        int toID = currentTransaction.getDestinationID();
        if (fromID == toID){
            return;
        }
        int min = Math.min(fromID, toID);
        int max = Math.max(fromID, toID);
        int amount = currentTransaction.getAmount();
        synchronized (accounts[min]){
            synchronized (accounts[max]){
                accounts[fromID].giveReward(amount);
                accounts[toID].takeReward(amount);
            }
        }
    }
}
