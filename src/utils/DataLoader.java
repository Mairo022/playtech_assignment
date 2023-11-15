package utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DataLoader {
    private ArrayList<String> players = new ArrayList<>();
    private ArrayList<HashMap<String, String>> playerActions = new ArrayList<>();
    private ArrayList<HashMap<String, String>> matches = new ArrayList<>();

    public DataLoader() {
        loadPlayerDataFile();
        loadMatchDataFile();
    }

    public ArrayList<HashMap<String, String>> getMatches() {
        return matches;
    }

    public ArrayList<HashMap<String, String>> getPlayerActions() {
        return playerActions;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    private void loadPlayerDataFile() {
        try {
            String filename = "data/player_data.txt";
            Path filepath = Paths.get(getClass().getClassLoader().getResource(filename).toURI());
            List<String> playerFile = Files.readAllLines(filepath, StandardCharsets.UTF_8);

            for (String line: playerFile) {
                String[] items = line.split(",");

                String playerID = items[0];
                String action = items[1];
                String matchID = items[2];
                String coin = items[3];
                String side = items.length > 4 ? items[4] : "";

                if (!players.contains(playerID)) {
                    players.add(playerID);
                }

                playerActions.add(new HashMap<>(){{
                    put("playerID", playerID);
                    put("matchID", matchID);
                    put("action", action);
                    put("coin", coin);
                    put("side", side);
                }});
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMatchDataFile() {
        try {
            String filename = "data/match_data.txt";
            Path filepath = Paths.get(getClass().getClassLoader().getResource(filename).toURI());
            List<String> matchFile = Files.readAllLines(filepath, StandardCharsets.UTF_8);

            for (String line: matchFile) {
                String[] items = line.split(",");

                String matchID = items[0];
                String rateA = items[1];
                String rateB = items[2];
                String result = Objects.equals(items[3], "") ? "EMPTY" : items[3];

                matches.add(new HashMap<>(){{
                    put("matchID", matchID);
                    put("rateA", rateA);
                    put("rateB", rateB);
                    put("result", result);
                }});
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
