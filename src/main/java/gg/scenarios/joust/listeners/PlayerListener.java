package gg.scenarios.joust.listeners;

import gg.scenarios.joust.managers.PlayerState;
import gg.scenarios.joust.managers.Tournament;
import gg.scenarios.joust.managers.TournamentPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerListener implements Listener {

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
}
