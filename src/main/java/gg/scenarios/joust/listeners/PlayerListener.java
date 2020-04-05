package gg.scenarios.joust.listeners;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.challonge.GameType;
import gg.scenarios.joust.managers.*;
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
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ExecutionException;

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
                    PotionEffect re = new PotionEffect(PotionEffectType.REGENERATION, 200, 1);
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
        killer.sendMessage(ChatColor.translateAlternateColorCodes('&', joust.getPREFIX() + "&3" + player.getName() + " &2is at &4" + Utils.getNf().format(player.getHealth())));
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }


    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player killed = (Player) event.getEntity();
        Player killer = null;
        killed.setHealth(20);

        if (killed.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent dmgEvent = (EntityDamageByEntityEvent) killed.getLastDamageCause();
            if (dmgEvent.getDamager() instanceof Player || dmgEvent.getDamager() instanceof Arrow) {
                try {
                    killer = (Player) (((Arrow) dmgEvent.getDamager()).getShooter());
                } catch (ClassCastException c) {
                    killer = (Player) dmgEvent.getDamager();
                }
                killer.setHealth(20);
                TournamentPlayer loser = TournamentPlayer.getTournamentPlayer(killed);
                TournamentPlayer winner = TournamentPlayer.getTournamentPlayer(killer);

                event.getDrops().clear();
                winner.getMatch().getArena().clear();
                winner.getMatch().getArena().setAvailable(true);
                //TODO: set match to null;
                loser.setMatch(null);
                winner.setMatch(null);

                winner.setState(PlayerState.LOBBY);
                loser.setState(PlayerState.LOBBY);

                winner.getPlayer().getActivePotionEffects().forEach(potionEffect -> winner.getPlayer().removePotionEffect(potionEffect.getType()));

                winner.getPlayer().setFireTicks(0);
                loser.getPlayer().setFireTicks(0);


                for (Player player : Bukkit.getOnlinePlayers()) {
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
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.GREEN + "Rules")) e.setCancelled(true);
    }

    @EventHandler
    public void sat(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        TournamentPlayer tournamentPlayer = TournamentPlayer.getTournamentPlayer(player);
        if (tournamentPlayer.getState() == PlayerState.LOBBY) {
            event.setFoodLevel(20);
            player.setSaturation(10);
            event.setCancelled(true);
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
        if (joust.getTournament().getTournamentState() == TournamentState.LOBBY) {
            TournamentPlayer tournamentPlayer = new TournamentPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId(), PlayerState.LOBBY);
        } else {
            TournamentPlayer tournamentPlayer = new TournamentPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId(), PlayerState.SPECTATOR);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws ExecutionException, InterruptedException {
        if (!(joust.getTournament().getTournamentState() == TournamentState.STARTED)) {
            TournamentPlayer tournamentPlayer = TournamentPlayer.getTournamentPlayer(event.getPlayer());
            if (!(tournamentPlayer == null)) {
                TournamentPlayer.tournamentPlayerHashMap.remove(event.getPlayer().getName());
            }
        } else {

        }
    }

    @EventHandler
    public void quitDuringMatch(PlayerQuitEvent event) {
        if (joust.getTournament().getTournamentState() == TournamentState.STARTED) {
            System.out.println("Left during match fucker: " + event.getPlayer());
            TournamentPlayer losers = TournamentPlayer.getTournamentPlayer(event.getPlayer().getName());
            TournamentMatch match = losers.getMatch();
            try {
                if (losers.getMatch().getPlayer1().equalsIgnoreCase(event.getPlayer().getName())) {
                    forfeit(Bukkit.getPlayer(losers.getMatch().getPlayer2()), event.getPlayer().getName());
                } else {
                    forfeit(Bukkit.getPlayer(losers.getMatch().getPlayer1()), event.getPlayer().getName());
                }
            } catch (NullPointerException | ExecutionException | InterruptedException ignored) {
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        if (TournamentPlayer.getTournamentPlayer(event.getEntity().getName()).getState() == PlayerState.INGAME) {
        } else {
            event.setCancelled(true);
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

    private void forfeit(Player winner, String loser) throws ExecutionException, InterruptedException {
        System.out.println(282);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(Joust.mods.contains(player.getUniqueId()))) {
                winner.showPlayer(player);
            }
        }
        TournamentPlayer player = TournamentPlayer.getTournamentPlayer(winner);
        TournamentPlayer losers = TournamentPlayer.getTournamentPlayer(loser);

        Arenas a = player.getMatch().getArena();
        a.setAvailable(true);
        a.clear();
        //TODO: set match to null;
        player.setMatch(null);
        losers.setMatch(null);
        TournamentPlayer.getTournamentPlayer(winner).setState(PlayerState.LOBBY);
        TournamentPlayer.getTournamentPlayer(loser).setState(PlayerState.LOBBY);


        player.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());

        joust.getTournament().getChallonge().updateMatch(player.getMatchId(), winner.getName());
        System.out.println("Updating: " + player.getMatchId());
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
