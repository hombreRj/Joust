package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.managers.arena.Arena;
import gg.scenarios.joust.managers.enums.PlayerState;
import gg.scenarios.joust.managers.kit.KitManager;
import gg.scenarios.joust.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Getter
public class TournamentMatch {

    private static List<TournamentMatch> matches = new ArrayList<>();
    private Joust joust = Joust.getInstance();


    private int id;
    private String player1, player2;
    private Player ingame1, ingame2;
    private Arena arena;

    public TournamentMatch(int id, String player1, String player2) throws Exception {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        arena = joust.getArenaManager().getNextAvailableArena();
        arena.setAvailable(false);

        //TODO: Start Match, startMatch(player1, player2, arena)
        ingame1 = Bukkit.getPlayer(player1);
        if (ingame1 == null){
            forfeit(Bukkit.getPlayer(player2), player1);
            return;
        }
        ingame2 = Bukkit.getPlayer(player2);
        if (ingame2 == null){
            forfeit(Bukkit.getPlayer(player1), player2);
            return;
        }
        startMatch(ingame1, ingame2, arena);
        matches.add(this);
    }


    private void startMatch(Player player1, Player  player2, Arena arenas) throws ExecutionException, InterruptedException {

        joust.getKitManager().giveKit(player1);
        joust.getKitManager().giveKit(player2);
        player1.teleport(arenas.getSpawn1());
        arenas.setMatch(this);
        TournamentPlayer.getTournamentPlayer(player1).setMatchId(id);
        player2.teleport(arenas.getSpawn2());
        joust.getNms().addVehicle(player1);
        joust.getNms().addVehicle(player2);
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
        player1.sendMessage(ChatColor.GREEN + "Match is starting in 5 seconds");
        player2.sendMessage(ChatColor.GREEN + "Match is starting in 5 seconds");
        Bukkit.getScheduler().runTaskLater(joust, ()->{
            joust.getNms().removeVehicle(player1);
            joust.getNms().removeVehicle(player2);
            try {
                joust.getNms().removeArrows(player1);
                joust.getNms().removeArrows(player2);
            }catch (Exception ignored){ }
        }, 20*5);
    }

    private void forfeit(Player winner, String loser) throws ExecutionException, InterruptedException {
        System.out.println(282);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(Joust.mods.contains(player.getUniqueId()))) {
                winner.showPlayer(player);
            }
        }
        TournamentPlayer player = TournamentPlayer.getTournamentPlayer(winner);
        TournamentPlayer losers = TournamentPlayer.getTournamentPlayer(loser);

        Arena a = getArena();
        a.setMatch(null);
        a.setAvailable(true);
        a.clear();
        //TODO: set match to null;
        player.setMatch(null);
        losers.setMatch(null);
        TournamentPlayer.getTournamentPlayer(winner).setState(PlayerState.LOBBY);
        TournamentPlayer.getTournamentPlayer(loser).setState(PlayerState.LOBBY);


        player.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());

        joust.getTournament().getChallonge().updateMatch(this.id, winner.getName());
        System.out.println("Updating: " + player.getMatchId());
        player.getPlayer().sendMessage(ChatColor.RED + "Your opponent is not online so you have won");
        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                joust.getTournament().startMatches();
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
