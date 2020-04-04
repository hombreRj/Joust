package gg.scenarios.joust.listeners;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.challonge.GameType;
import gg.scenarios.joust.managers.PlayerState;
import gg.scenarios.joust.managers.Tournament;
import gg.scenarios.joust.managers.TournamentPlayer;
import gg.scenarios.joust.managers.TournamentState;
import gg.scenarios.joust.utils.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import org.apache.http.nio.pool.NIOConnFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        try {
            if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == 1) {
                event.setCancelled(true);
            } else if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == 0) {
                ItemStack goldenHead = event.getItem();
                if (goldenHead.getItemMeta().getDisplayName().contains("Golden Head")) {
                    Player p = event.getPlayer();
                    PotionEffect pe = new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0);
                    PotionEffect re = new PotionEffect(PotionEffectType.REGENERATION, 100, 1);
                    pe.apply(p);
                    re.apply(p);
                }
            }
        } catch (Exception e) {

        }
    }


    @EventHandler
    public void onBow(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Arrow damager = (Arrow) event.getDamager();

        if (!(damager.getShooter() instanceof Player)) {
            return;
        }
        if (damager.getShooter().equals(player)) {
            return;
        }


        Player killer = (Player) damager.getShooter();

        double distance = killer.getLocation().distance(player.getLocation());
        killer.sendMessage(ChatColor.translateAlternateColorCodes('&', joust.getPREFIX() + player.getName() + " is at " + (player.getHealth())));
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player killed = (Player) event.getEntity();
        killed.setHealth(20);
        if (killed.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent dmgEvent = (EntityDamageByEntityEvent) killed.getLastDamageCause();
            if (dmgEvent.getDamager() instanceof Player) {
                Player killer = (Player) dmgEvent.getDamager();

                TournamentPlayer loser = TournamentPlayer.getTournamentPlayer(killed);
                TournamentPlayer winner = TournamentPlayer.getTournamentPlayer(event.getEntity().getKiller());
                if (joust.getTournament().getType() == GameType.SINGLE) {
                    loser.setState(PlayerState.ELIMINATED);
                    loser.getPlayer().sendMessage(ChatColor.RED + "You have lost this round of the tournament");
                }else{
                    loser.setState(PlayerState.LOBBY);
                }
                event.getDrops().clear();
                winner.getMatch().getArena().clear();
                winner.getMatch().getArena().setAvailable(true);
                //TODO: set match to null;
                loser.setMatch(null);
                winner.setMatch(null);

                winner.setState(PlayerState.LOBBY);


                for (Player player :Bukkit.getOnlinePlayers()){
                    if (!(Joust.mods.contains(player.getUniqueId()))) {
                        killed.showPlayer(player);
                        killer.showPlayer(player);
                    }
                }

                Utils.broadcast(joust.getPREFIX() + ChatColor.GREEN + winner.getName() + ChatColor.RED + " has beaten " + ChatColor.GREEN + loser.getName());
                joust.getTournament().getChallonge().updateMatch(winner.getMatchId(), winner.getName());


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


                loser.getPlayer().getInventory().clear();
                loser.getPlayer().getInventory().setBoots(null);
                loser.getPlayer().getInventory().setHelmet(null);
                loser.getPlayer().getInventory().setLeggings(null);
                loser.getPlayer().getInventory().setChestplate(null);


            }
        }
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        TournamentPlayer tournamentPlayer = new TournamentPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId(), PlayerState.LOBBY);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!(joust.getTournament().getTournamentState() == TournamentState.STARTED)) {
            TournamentPlayer tournamentPlayer = TournamentPlayer.getTournamentPlayer(event.getPlayer());
            if (!(tournamentPlayer == null)) {
                TournamentPlayer.tournamentPlayerHashMap.remove(event.getPlayer().getName());
            }
        }
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
