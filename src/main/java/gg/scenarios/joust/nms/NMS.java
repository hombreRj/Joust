package gg.scenarios.joust.nms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface NMS {
    void removeArrows(Player player);
    void addVehicle(Player player);
    void removeVehicle(Player player);
    void removeMovement(Entity entity);
    void setCustomName(Player player, String string);


}
