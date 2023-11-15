import models.Casino;
import models.Match;
import models.PlayerAction;
import models.PlayerRecord;
import enums.Action;
import enums.Result;
import utils.DataLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Game {
    private final DataLoader dataLoader = new DataLoader();
    private HashMap<String, PlayerRecord> playerRecords = new HashMap<>();
    private HashMap<String, Match> matches = new HashMap<>();
    private ArrayList<PlayerAction> playerActions = new ArrayList<>();
    private Casino casino = Casino.getInstance();

    public Game() {
        initialisePlayers();
        initialisePlayerActions();
        initialiseMatches();
    }

    public void run() {
        playMatches();
        createResultFile();
    }

    public void playMatches() {
        for (PlayerAction playerAction: playerActions) {
            String playerID = playerAction.playerID;
            String matchID = playerAction.matchID;
            Action action = playerAction.action;
            String side = playerAction.side;
            int coin = playerAction.coin;

            PlayerRecord player = playerRecords.get(playerID);

            player.addMatch(playerAction);

            if (!player.isLegitimate()) continue;

            if (action == Action.DEPOSIT) {
                player.deposit(coin);
                continue;
            }

            if (action == Action.WITHDRAW) {
                if (player.hasEnoughMoney(coin)) {
                    player.withdraw(coin);
                } else {
                    markAsCheater(player, playerAction);
                }
                continue;
            }

            if (action == Action.BET) {
                if (!player.hasEnoughMoney(coin)) {
                    markAsCheater(player, playerAction);
                    continue;
                }
                player.addBet();

                Match match = matches.get(matchID);
                Result result = match.result;
                BigDecimal rateA = match.rateA;
                BigDecimal rateB = match.rateB;

                if (result == Result.DRAW) {
                    continue;
                }

                if (isWin(result.toString(), side)) {
                    int coinsCasino = coinsCasino(result, rateA, rateB, coin);
                    int coinsWonPlayer = coinsWonPlayer(result, rateA, rateB, coin);

                    casino.deposit(coinsCasino);
                    player.deposit(coinsWonPlayer);
                    player.addWin();

                    continue;
                }

                if (!isWin(result.toString(), side)) {
                    casino.deposit(coin);
                    player.withdraw(coin);
                }
            }
        }
    }

    private void createResultFile() {
        try {
            String filename = "result.txt";
            String path = "src/";
            FileWriter myFile = new FileWriter(path + filename);

            ArrayList<String> illegitimatePlayers = illegitimatePlayers();
            ArrayList<String> legitimatePlayers = legitimatePlayers();
            long casinoBalance = casino.getBalance();

            for (String player: legitimatePlayers) {
                myFile.write(player + "\n");
            }
            myFile.write("\n");
            if (legitimatePlayers.size() == 0) myFile.write("\n");

            for (String player: illegitimatePlayers) {
                myFile.write(player + "\n");
            }
            myFile.write("\n");
            if (illegitimatePlayers.size() == 0) myFile.write("\n");

            myFile.write(String.valueOf(casinoBalance));

            myFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<String> legitimatePlayers() {
        ArrayList<String> playerIDs = new ArrayList<>(playerRecords.keySet());
        ArrayList<String> players = new ArrayList<>();

        playerIDs.sort(Comparator.naturalOrder());

        for (String key: playerIDs) {
            PlayerRecord player = playerRecords.get(key);

            if (!player.isLegitimate()) continue;

            BigDecimal winRate = new BigDecimal(player.getWins()).divide(BigDecimal.valueOf(player.getBets()), 2, BigDecimal.ROUND_HALF_EVEN);
            players.add(player.id + " " + player.getBalance() + " " + winRate);
        }

        return players;
    }

    private ArrayList<String> illegitimatePlayers() {
        ArrayList<String> playerIDs = new ArrayList<>(playerRecords.keySet());
        ArrayList<String> players = new ArrayList<>();

        playerIDs.sort(Comparator.naturalOrder());

        for (String key: playerIDs) {
            PlayerRecord player = playerRecords.get(key);

            if (player.isLegitimate()) continue;

            ArrayList<PlayerAction> historyCheating = player.getHistoryCheating();
            PlayerAction firstCheatingInstance = historyCheating.get(historyCheating.size() - 1);

            String action = String.valueOf(firstCheatingInstance.action);
            String coin = String.valueOf(firstCheatingInstance.coin);
            String matchID = firstCheatingInstance.matchID.isBlank() ? "null" : firstCheatingInstance.matchID;
            String side = firstCheatingInstance.side.isBlank() ? "null" : firstCheatingInstance.side;

            players.add(key + " " + action + " " + matchID + " " + coin + " " + side);
        }

        return players;
    }

    private void markAsCheater(PlayerRecord player, PlayerAction playerAction) {
        player.setIsLegitimate(false);
        player.addMatchCheating(playerAction);
    }

    private int coinsCasino(Result result, BigDecimal rateA, BigDecimal rateB, int betAmount) {
        if (result == Result.A && rateA.compareTo(new BigDecimal("1")) < 0) {
            BigDecimal casinoRate = new BigDecimal("1").subtract(rateA);
            BigDecimal coinsWon = casinoRate.multiply(BigDecimal.valueOf(betAmount)).setScale(0, 1);

            return coinsWon.intValue();
        }

        if (result == Result.B && rateB.compareTo(new BigDecimal("1")) < 0) {
            BigDecimal casinoRate = new BigDecimal("1").subtract(rateB);
            BigDecimal coinsWon = casinoRate.multiply(BigDecimal.valueOf(betAmount)).setScale(0, 1);

            return coinsWon.intValue();
        }

        return betAmount;
    }

    private int coinsWonPlayer(Result result, BigDecimal rateA, BigDecimal rateB, int betAmount) {
        if (result == Result.A) {
            BigDecimal coinsWon = rateA.multiply(BigDecimal.valueOf(betAmount)).setScale(0, 1);
            return coinsWon.intValue();
        }

        if (result == Result.B) {
            BigDecimal coinsWon = rateB.multiply(BigDecimal.valueOf(betAmount)).setScale(0, 1);
            return coinsWon.intValue();
        }

        return 0;
    }

    private boolean isWin(String a, String b) {
        return a.equals(b);
    }

    private void initialisePlayers() {
        dataLoader.getPlayers().forEach(playerID -> {
            playerRecords.put(playerID, new PlayerRecord(playerID));
        });
    }

    private void initialisePlayerActions() {
        dataLoader.getPlayerActions().forEach(playerAction -> {
            String playerID = playerAction.get("playerID");
            String matchID = playerAction.get("matchID");
            Action action = Action.valueOf(playerAction.get("action"));
            int coin = Integer.parseInt(playerAction.get("coin"));
            String side = playerAction.get("side");

            playerActions.add(new PlayerAction(playerID, matchID, action, coin, side));
        });
    }

    private void initialiseMatches() {
        dataLoader.getMatches().forEach(match -> {
            String matchID = match.get("matchID");
            String rateA = match.get("rateA");
            String rateB = match.get("rateB");
            Result result = Result.valueOf(match.get("result"));

            matches.put(matchID, new Match(matchID, rateA, rateB, result));
        });
    }
}
