package gg.scenarios.joust.managers;

import com.google.gson.internal.$Gson$Preconditions;
import gg.scenarios.joust.Joust;
import gg.scenarios.joust.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
        startMatch(Bukkit.getPlayer(player1), Bukkit.getPlayer(player), arena);
        matches.add(this);
    }


    private void startMatch(Player player1, Player player2, Arenas arenas){

        player1.teleport(arenas.getSpawn1());
        TournamentPlayer.getTournamentPlayer(player1).setMatchId(id);
        player2.teleport(arenas.getSpawn2());
        TournamentPlayer.getTournamentPlayer(player1).setMatch(this);
        TournamentPlayer.getTournamentPlayer(player2).setMatchId(id);
        TournamentPlayer.getTournamentPlayer(player2).setMatch(this);
        Utils.broadcast(joust.getPREFIX() + ChatColor.GREEN +player1.getName() +ChatColor.RED + "Is now fighting " +ChatColor.GREEN+player2.getName() + ChatColor.RED +" at arena " + ChatColor.GREEN +arenas.getName());

    }



}
