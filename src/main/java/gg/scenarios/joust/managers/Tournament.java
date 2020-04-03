package gg.scenarios.joust.managers;

import api.challonge.Challonge;
import api.challonge.GameType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class Tournament {


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





}
