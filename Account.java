public class Account {
    /* balance of the account */
    private int balance;

    /* balance of the account */
    private int transactionNumber;

    /* ID of the account */
    private int id;

    public Account(int initialBalance, int accountID){
        balance = initialBalance;
        id = accountID;
        transactionNumber = 0;
    }

    /**
     * This method returns ID of the account
     *
     * @return ID of the account
     * */
    public int getID(){
        return id;
    }

    /**
     * This method returns current transactions made connected to the account
     *
     * @return transactions made connected to the account
     * */
    public int getTransactionNumber(){
        return transactionNumber;
    }

    /**
     * This method returns current balance of the account
     *
     * @return current balance of account
     * */
    public int getBalance(){
        return balance;
    }

    /**
     * This method increases balance of the account with val
     * */
    public void takeReward(int val){
        transactionNumber++;
        balance += val;
    }

    /**
     * This method decreases balance of the account with val
     * */
    public void giveReward(int val){
        transactionNumber++;
        balance -= val;
    }
}
