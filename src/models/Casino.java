package models;

public class Casino {
    private static Casino instance;
    private long balance = 0;

    private Casino() {}

    public static Casino getInstance() {
        if (instance == null) instance = new Casino();

        return instance;
    }

    public long getBalance() {
        return balance;
    }

    public void deposit(long amount) {
        this.balance += amount;
    }
}
