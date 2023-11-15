package models;

import java.util.ArrayList;

public class PlayerRecord {
    public final String id;
    private long balance = 0;
    private boolean isLegitimate = true;
    private int wins = 0;
    private int bets = 0;
    private ArrayList<PlayerAction> history = new ArrayList<>();
    private ArrayList<PlayerAction> historyCheating = new ArrayList<>();

    public PlayerRecord(String id) {
        this.id = id;
    }

    public void addMatch(PlayerAction match) {
        history.add(match);
    }

    public ArrayList<PlayerAction> getHistory() {
        return history;
    }

    public void addMatchCheating(PlayerAction match) {
        historyCheating.add(match);
    }

    public ArrayList<PlayerAction> getHistoryCheating() {
        return historyCheating;
    }

    public void setIsLegitimate(boolean isLegitimate) {
        this.isLegitimate = isLegitimate;
    }

    public boolean isLegitimate() {
        return this.isLegitimate;
    }

    public void deposit(long amount) {
        this.balance += amount;
    }

    public void withdraw(long amount) {
        this.balance -= amount;
    }

    public boolean hasEnoughMoney(int requiredAmount) {
        return balance >= requiredAmount;
    }

    public long getBalance() {
        return balance;
    }

    public void addWin() {
        this.wins += 1;
    }

    public int getWins() {
        return wins;
    }

    public void addBet() {
        this.bets += 1;
    }

    public int getBets() {
        return bets;
    }
}
