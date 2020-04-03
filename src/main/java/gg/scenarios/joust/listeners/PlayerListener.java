package gg.scenarios.joust.listeners;

import api.challonge.GameType;
import gg.scenarios.joust.Joust;
import gg.scenarios.joust.managers.PlayerState;
import gg.scenarios.joust.managers.Tournament;
import gg.scenarios.joust.managers.TournamentPlayer;
import gg.scenarios.joust.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements Listener {

    private Joust joust = Joust.getInstance();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        if (event.getDamager().getType() != EntityType.PLAYER) return;

        Player player = (Player) event.getEntity();
        if (TournamentPlayer.getTournamentPlayer(player).getState()== PlayerState.INGAME){
            event.setCancelled(false);
        }else{
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (event.getEntity().getKiller().getType() != EntityType.PLAYER) return;

        TournamentPlayer loser = TournamentPlayer.getTournamentPlayer(event.getEntity());
        TournamentPlayer winner = TournamentPlayer.getTournamentPlayer(event.getEntity().getKiller());
        if (joust.getTournament().getType() == GameType.SINGLE){
            loser.getPlayer().sendMessage(ChatColor.RED+"You have lost this round of the tournament");
        }

        winner.getMatch().getArena().setAvailable(true);
        //TODO: set match to null;
        loser.setMatch(null);
        winner.setMatch(null);



        Utils.broadcast(joust.getPREFIX() + ChatColor.GREEN +winner.getName() +ChatColor.RED + "Has beaten " +ChatColor.GREEN+loser.getName());
        joust.getTournament().update(winner.getMatchId(), winner.getName());

        Bukkit.getScheduler().runTaskLater(joust, ()->{
            try {
                joust.getTournament().startNextMatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        },20*7);

        loser.setMatchId(0);
        winner.setMatchId(0);

    }
}
