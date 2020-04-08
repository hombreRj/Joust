package gg.scenarios.joust.nms.verisons;


import com.mojang.authlib.GameProfile;
import gg.scenarios.joust.nms.NMS;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;

import java.lang.reflect.Field;
import java.util.HashMap;


public class v1_8_R3 implements NMS {


    @Getter
    private HashMap<Player, Integer> vehicles = new HashMap<>();


    @Override
    public void removeArrows(Player player) {
        getEntity(player).getDataWatcher().watch(9, (byte) 0);
    }

    @Override
    public void addVehicle(Player player) {
        Location location = player.getLocation();
        WorldServer worldServer = ((CraftWorld) player.getLocation().getWorld()).getHandle();

        EntityBat bat = new EntityBat(worldServer);
        bat.setLocation(location.getX() + 0.5, location.getY() + 2.0, location.getZ() + 0.5, 0.0f, 0.0f);
        bat.setHealth(bat.getMaxHealth());
        bat.setInvisible(true);
        bat.d(0);
        bat.setAsleep(true);
        bat.setAirTicks(10);
        bat.setSneaking(false);

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(bat);
        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(0, ((CraftPlayer) player).getHandle(), bat);

        playerConnection.sendPacket(packet);
        playerConnection.sendPacket(attach);

        vehicles.put(player, bat.getId());
    }

    @Override
    public void removeVehicle(Player player) {
        if (vehicles.get(player) != null) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(vehicles.get(player));
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(packet);
            vehicles.put(player, null);
        }
    }

    @Override
    public void removeMovement(Entity entity) {
        if (entity instanceof Item || entity instanceof Slime || entity instanceof Ghast || entity instanceof Bat || entity instanceof Spider || entity instanceof CaveSpider || entity instanceof Horse || entity instanceof Blaze || entity instanceof Squid)
            return;

        EntityCreature creature = (EntityCreature) ((CraftEntity) entity).getHandle();
        creature.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0);
        creature.getAttributeInstance(GenericAttributes.c).setValue(0.0);
        creature.getNavigation().n();
    }


    public EntityPlayer getEntity(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public void sendPacket(Player player, Packet packet) {
        getEntity(player).playerConnection.sendPacket(packet);
    }

    public void broadcastWorld(Packet packet, Player player) {
        for (Player online : player.getWorld().getPlayers()) {
            if (online != null && online.getEntityId() != player.getEntityId() && online.canSee(player)) {
                sendPacket(online, packet);
            }
        }
    }

    @Override
    public void setCustomName(Player player, String name) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!(online.equals(player))) {
                PacketPlayOutEntityDestroy despawn = new PacketPlayOutEntityDestroy(player.getEntityId());
                PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
                try {
                    Field b = spawn.getClass().getDeclaredField("b");
                    b.setAccessible(true);
                    b.set(spawn, new GameProfile(player.getUniqueId(), name));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(despawn);
                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(spawn);
            }
        }
    }


}