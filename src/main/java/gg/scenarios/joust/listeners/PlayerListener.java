package gg.scenarios.joust.listeners;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.challonge.GameType;
import gg.scenarios.joust.managers.PlayerState;
import gg.scenarios.joust.managers.Tournament;
import gg.scenarios.joust.managers.TournamentPlayer;
import gg.scenarios.joust.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

    private Joust joust = Joust.getInstance();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        if (event.getDamager().getType() != EntityType.PLAYER) return;

        Player player = (Player) event.getEntity();
        if (TournamentPlayer.getTournamentPlayer(player).getState() == PlayerState.INGAME) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller().getType() != EntityType.PLAYER) return;
        TournamentPlayer loser = TournamentPlayer.getTournamentPlayer(event.getEntity());
        TournamentPlayer winner = TournamentPlayer.getTournamentPlayer(event.getEntity().getKiller());
        if (joust.getTournament().getType() == GameType.SINGLE) {
            loser.getPlayer().sendMessage(ChatColor.RED + "You have lost this round of the tournament");
        }
        event.getDrops().clear();
        winner.getMatch().getArena().setAvailable(true);
        //TODO: set match to null;
        loser.setMatch(null);
        winner.setMatch(null);


        Utils.broadcast(joust.getPREFIX() + ChatColor.GREEN + winner.getName() + ChatColor.RED + "Has beaten " + ChatColor.GREEN + loser.getName());
        joust.getTournament().update(winner.getMatchId(), winner.getName());


        winner.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
        loser.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());

        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                joust.getTournament().startNextMatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20 * 7);

        loser.setMatchId(0);
        winner.setMatchId(0);

        winner.getPlayer().getInventory().clear();
        winner.getPlayer().getInventory().setBoots(null);
        winner.getPlayer().getInventory().setHelmet(null);
        winner.getPlayer().getInventory().setLeggings(null);
        winner.getPlayer().getInventory().setChestplate(null);


    }



    public void onDrop(PlayerDropItemEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        TournamentPlayer tournamentPlayer = new TournamentPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId(), PlayerState.LOBBY);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (TournamentPlayer.getTournamentPlayer(event.getPlayer()).getState() == PlayerState.INGAME) {
            Bukkit.getServer().getScheduler().runTaskLater(joust, () -> {
                event.getBlockPlaced().setType(Material.AIR);
            }, 20 * 10);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getBucket() != Material.WATER_BUCKET || event.getBucket() != Material.LAVA_BUCKET) return;
        Player player = event.getPlayer();
        Block water = event.getBlockClicked().getRelative(event.getBlockFace());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (water.getType() == Material.WATER) {
                    water.setType(Material.AIR);
                }
            }
        }.runTaskLater(joust, 20 * 10); //set time here
    }

}
