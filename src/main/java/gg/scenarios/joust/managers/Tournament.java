package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;

import gg.scenarios.joust.challonge.Challonge;
import gg.scenarios.joust.challonge.GameType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

@Getter
@Setter
public class Tournament {

    private Joust joust = Joust.getInstance();

    public static int globalMatchNumber = 1;

    private String name, url, tournamentNum, description, apiKey, bracketURL;
    private List<TournamentPlayer> host;
    private List<TournamentPlayer> players;
    private GameType type = GameType.SINGLE;

    Challonge challonge = new Challonge("AeoqInZkvafvAeTuiHNat7aADcJdLdxOjmiNVLPT", "ScenariosUHC", "" + System.currentTimeMillis(), "test 1v1 tournament", "fun bracket", GameType.SINGLE);


    public Tournament(String name, String tournamentNum, String description) {
        this.name = name;
        this.url = name + "'s #" + tournamentNum;
        this.tournamentNum = tournamentNum;
        this.description = description;
    }

    public Tournament() {
    }

    public boolean setup() throws ExecutionException, InterruptedException {
        return challonge.post().get();
    }

    public String getBracketURL() {
        return challonge.getUrl();
    }

    public boolean addMembers() throws ExecutionException, InterruptedException {
        TournamentPlayer.tournamentPlayerHashMap.keySet().forEach(s -> challonge.getParticipants().add(s));
        return challonge.addParticpants().get();
    }


    public static Integer[] participants;
    private Integer[] names;

    private void startMatches() throws ExecutionException, InterruptedException {
        System.out.println("starting matches");
        for (Arenas arenas : Arenas.arenasList) {
            if (arenas.isAvailable()) {
                   names = challonge.getMatchParticipants(globalMatchNumber).get();
                for (Integer integer : names) {
                    System.out.println(integer);
                }
            }

        }
    }

    public boolean start() throws ExecutionException, InterruptedException {
        return challonge.start().get();
    }


    public void randomize() {
        CompletableFuture.runAsync(() -> {
            challonge.addParticpants();
            challonge.indexMatches();

        });
    }

    public void update(int matchId, String name) {
        CompletableFuture.runAsync(() -> {
            challonge.updateMatch(matchId, name);
        });
    }

    public void end() {
        CompletableFuture.runAsync(() -> {
            challonge.end();

        });
    }

    public void startNextMatch() throws Exception {
        try {
            joust.getArenaManager().getNextAvailableArena();
            CompletableFuture.runAsync(() -> {
                CompletableFuture<Integer[]> players = challonge.getMatchParticipants(globalMatchNumber);
                try {
                    TournamentMatch match = new TournamentMatch((globalMatchNumber), challonge.getNameFromId(players.get()[0]), challonge.getNameFromId(players.get()[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Could not find arena.");
        }
    }
}