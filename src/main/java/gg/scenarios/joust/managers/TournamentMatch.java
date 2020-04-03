package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TournamentMatch {

    private static List<TournamentMatch> matches = new ArrayList<>();
    private Joust joust = Joust.getInstance();


    private int id;
    private String player1, player;
    private Arenas arena;

    public TournamentMatch(int id, String player1, String player) throws Exception {
        this.id = id;
        this.player1 = player1;
        this.player = player;
        arena = joust.getArenaManager().getNextAvailableArena();
        arena.setAvailable(false);

        //TODO: Start Match, startMatch(player1, player2, arena)
        matches.add(this);
    }
}
