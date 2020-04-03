package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.challonge.Challonge;
import gg.scenarios.joust.challonge.GameType;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.cactoos.func.Async;

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

    public void setup() {
        Bukkit.getScheduler().runTaskAsynchronously(joust, challonge::post);

        bracketURL = challonge.getUrl();
    }

    public void addMembers() {
        Bukkit.getScheduler().runTaskAsynchronously(joust, () ->
                TournamentPlayer.tournamentPlayerHashMap.keySet().forEach(s -> {
                    challonge.getParticipants().add(s);
                })
        );

    }


    public void start() {
        Bukkit.getScheduler().runTaskAsynchronously(joust, challonge::start);

        System.out.println("tournament started");
        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                startMatches();
            } catch (ExecutionException | InterruptedException e) {

            }
        }, 20 * 10);
    }

    public static Integer[] participants;
    private Integer[] names;

    private void startMatches() throws ExecutionException, InterruptedException {
        System.out.println("starting matches");
        for (Arenas arenas : Arenas.arenasList) {
            if (arenas.isAvailable()) {
                Bukkit.getScheduler().runTaskAsynchronously(joust, () -> {
                    names = challonge.getMatchParticipants(globalMatchNumber);
                });

            }
            globalMatchNumber++;
        }
    }

    public void randomize() {
        Bukkit.getScheduler().runTaskAsynchronously(joust, ()-> {
            challonge.addParticpants();
            challonge.randomize();
            challonge.indexMatches();
        });

    }

    public void update(int matchId, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(joust, ()-> {
            challonge.updateMatch(matchId, name);
        });
    }

    public void end() {
        Bukkit.getScheduler().runTaskAsynchronously(joust, ()-> {
            challonge.end();

        });
    }

    public void startNextMatch() throws Exception {
        try {
            joust.getArenaManager().getNextAvailableArena();
            Bukkit.getScheduler().runTaskAsynchronously(joust, ()-> {
                Integer[] players = challonge.getMatchParticipants(globalMatchNumber);
                try {
                    TournamentMatch match = new TournamentMatch((globalMatchNumber), challonge.getNameFromId(players[0]), challonge.getNameFromId(players[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Could not find arena.");
        }
    }
}