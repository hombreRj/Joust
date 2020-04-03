package gg.scenarios.joust.managers;

import api.challonge.Challonge;
import api.challonge.GameType;
import gg.scenarios.joust.Joust;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class Tournament {

    private Joust joust = Joust.getInstance();

    public static int globalMatchNumber =1;

    private String name, url, tournamentNum, description, apiKey, bracketURL;
    private List<TournamentPlayer> host;
    private List<TournamentPlayer> players;
    private GameType type;

    Challonge challonge = new Challonge(apiKey, "IAMRJ", String.valueOf(System.currentTimeMillis()), name, description, type);


    public Tournament(String name, String tournamentNum, String description) {
        this.name = name;
        this.url = name+"'s #" + tournamentNum;
        this.tournamentNum = tournamentNum;
        this.description = description;
    }

    public Tournament() {
    }

    public void setup(){
        this.url = name+"'s #" + tournamentNum;
        CompletableFuture.runAsync(() -> {
            challonge.post();
        });
        bracketURL = challonge.getUrl();
    }

    public void addMembers(){
        CompletableFuture.runAsync(() -> {
            TournamentPlayer.tournamentPlayerHashMap.values().stream().filter(TournamentPlayer::isPlaying).forEach(tournamentPlayer -> challonge.getParticipants().add(tournamentPlayer.getName()));
        });
    }


    public void start() {
        CompletableFuture.runAsync(() -> {
            challonge.addParticpants();
            challonge.start();
        });
        Bukkit.getScheduler().runTaskLater(joust, ()->{
            startMatches();
        }, 20*10);
    }

    private void startMatches() {
        for (Arenas arenas : Arenas.arenasList){
            if (arenas.isAvailable()){
                CompletableFuture.runAsync(()->{
                    String[] players =challonge.getMatchParticipants(globalMatchNumber);
                    try {
                        TournamentMatch match = new TournamentMatch((globalMatchNumber), players[0], players[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    globalMatchNumber++;
                });
            }
        }
    }

    public void randomize() {
        CompletableFuture.runAsync(() -> {
            challonge.randomize();
            challonge.indexMatches();

        });
    }
}
