package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;

import gg.scenarios.joust.challonge.Challonge;
import gg.scenarios.joust.challonge.GameType;
import gg.scenarios.joust.utils.Utils;
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
    private TournamentState tournamentState;

    Challonge challonge = new Challonge("AeoqInZkvafvAeTuiHNat7aADcJdLdxOjmiNVLPT", "ScenariosUHC", "" + System.currentTimeMillis(), "test 1v1 tournament", "fun bracket", GameType.SINGLE);


    public Tournament(String name, String tournamentNum, String description) {
        this.name = name;
        this.url = name + "'s #" + tournamentNum;
        this.tournamentNum = tournamentNum;
        this.description = description;
    }

    public Tournament() {
        tournamentState = TournamentState.LOBBY;
    }

    public boolean setup() throws ExecutionException, InterruptedException {
        tournamentState = TournamentState.STARTED;
        return challonge.post().get();
    }

    public String getBracketURL() {
        return challonge.getUrl();
    }

    public boolean addMembers() throws ExecutionException, InterruptedException {
        TournamentPlayer.tournamentPlayerHashMap.keySet().forEach(s -> challonge.getParticipants().add(s));
        return challonge.addParticpants().get();
    }


    int winner = 0;

    private void startMatches() throws ExecutionException, InterruptedException, Exception {
        Utils.broadcast(joust.getPREFIX() + "&c&lTournament is now starting matches are now being sent.");

        for (Arenas arenas : Arenas.arenasList) {
            if (arenas.isAvailable()) {
                Integer[] names = challonge.getMatchParticipants(globalMatchNumber).get();
                TournamentMatch match = new TournamentMatch(globalMatchNumber, challonge.getNameFromId(names[0]), challonge.getNameFromId(names[1]));
                globalMatchNumber++;
            }
        }
    }

    public void start() throws ExecutionException, InterruptedException {
        tournamentState = TournamentState.STARTED;
        challonge.start().get();
        challonge.indexMatches().get();
        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                startMatches();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20 * 10);

    }


    public void randomize() throws ExecutionException, InterruptedException {
        challonge.addParticpants().get();
        challonge.randomize().get();
    }

    public void update(int matchId, String name) throws ExecutionException, InterruptedException {
        challonge.updateMatch(matchId, name).get();
    }

    public void end() throws ExecutionException, InterruptedException {
        tournamentState = TournamentState.OVER;
        challonge.end().get();
    }

    public void startNextMatch() {
        if (tournamentState == TournamentState.STARTED) {
            try {
                Arenas arenas = joust.getArenaManager().getNextAvailableArena();
                if (arenas.isAvailable()) {
                    Integer[] names = challonge.getMatchParticipants(globalMatchNumber).get();
                    TournamentMatch match = new TournamentMatch(globalMatchNumber, challonge.getNameFromId(names[0]), challonge.getNameFromId(names[1]));
                    globalMatchNumber++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not load arena trying again in 10 seconds");
                Bukkit.getScheduler().runTaskLater(joust, this::startNextMatch, 20*10);
            }
        }
    }
}