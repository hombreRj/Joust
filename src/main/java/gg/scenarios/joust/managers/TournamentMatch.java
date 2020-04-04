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
import java.util.concurrent.ExecutionException;

@Getter
public class TournamentMatch {

    private static List<TournamentMatch> matches = new ArrayList<>();
    private Joust joust = Joust.getInstance();


    private int id;
    private String player1, player2;
    private Arenas arena;

    public TournamentMatch(int id, String player1, String player2) throws Exception {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        arena = joust.getArenaManager().getNextAvailableArena();
        arena.setAvailable(false);

        //TODO: Start Match, startMatch(player1, player2, arena)
        startMatch(Bukkit.getPlayer(player1), Bukkit.getPlayer(player2), arena);
        matches.add(this);
    }


    private void startMatch(Player player1, Player player2, Arenas arenas) throws ExecutionException, InterruptedException {

        if (player1 == null) {
            forfeit(player2, this.player1);
            return;
        }
        if (player2 == null) {
            forfeit(player1, this.player2);
            return;
        }
        KitManager.giveKit(player1);
        KitManager.giveKit(player2);
        player1.teleport(arenas.getSpawn1());
        TournamentPlayer.getTournamentPlayer(player1).setMatchId(id);
        player2.teleport(arenas.getSpawn2());
        TournamentPlayer.getTournamentPlayer(player1).setMatch(this);
        TournamentPlayer.getTournamentPlayer(player2).setMatchId(id);
        TournamentPlayer.getTournamentPlayer(player1).setState(PlayerState.INGAME);
        TournamentPlayer.getTournamentPlayer(player2).setState(PlayerState.INGAME);
        TournamentPlayer.getTournamentPlayer(player2).setMatch(this);

        for (Player player :Bukkit.getOnlinePlayers()){
            if (!(player.getName().equalsIgnoreCase(player1.getName()) || player.getName().equalsIgnoreCase(player2.getName()))){
                player1.hidePlayer(player);
                player2.hidePlayer(player);
            }
        }
        Utils.broadcast(joust.getPREFIX() + ChatColor.GREEN + player1.getName() + ChatColor.RED + " is now fighting " + ChatColor.GREEN + player2.getName() + ChatColor.RED + " at arena " + ChatColor.GREEN + arenas.getName());
    }

    private void forfeit(Player winner, String loser) throws ExecutionException, InterruptedException {

        for (Player player :Bukkit.getOnlinePlayers()){
            if (!(Joust.mods.contains(player.getUniqueId()))) {
                winner.showPlayer(player);
            }
        }
        TournamentPlayer player = TournamentPlayer.getTournamentPlayer(winner);
        TournamentPlayer losers = TournamentPlayer.getTournamentPlayer(loser);

        this.getArena().setAvailable(true);
        this.getArena().clear();
        //TODO: set match to null;
        player.setMatch(null);
        losers.setMatch(null);
        TournamentPlayer.getTournamentPlayer(player1).setState(PlayerState.LOBBY);
        TournamentPlayer.getTournamentPlayer(player2).setState(PlayerState.LOBBY);


        player.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());

        joust.getTournament().getChallonge().updateMatch(player.getMatchId(), player.getName()).get();
        player.getPlayer().sendMessage(ChatColor.RED + "Your opponent is not online so you have won");
        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                joust.getTournament().startNextMatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20 * 7);

        player.setMatchId(0);
        losers.setMatchId(0);


        winner.getPlayer().getInventory().clear();
        winner.getPlayer().getInventory().setBoots(null);
        winner.getPlayer().getInventory().setHelmet(null);
        winner.getPlayer().getInventory().setLeggings(null);
        winner.getPlayer().getInventory().setChestplate(null);
    }


}
