package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Arenas {

    private Joust joust = Joust.getInstance();

    public static List<Arenas> arenasList = new ArrayList<>();

    private String name;
    private Location spawn1, spawn2;
    private boolean available;

    public Arenas(String name, Location spawn1, Location spawn2 ) {
        this.name = name;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        available = true;
        arenasList.add(this);
    }


    public boolean clear(){
        Bukkit.getScheduler().runTaskAsynchronously(joust, ()->{
            int x1 = (int) Math.max(spawn2.getX(), spawn1.getX());
            int y1 = (int) Math.max(spawn2.getY(), spawn1.getY()) +2;
            int z1 = (int) Math.max(spawn2.getZ(), spawn1.getZ());

            int x2 = (int) Math.min(spawn2.getX(), spawn1.getX());
            int y2 = (int) Math.min(spawn2.getY(), spawn1.getY()) -2;
            int z2 = (int) Math.min(spawn2.getZ(), spawn1.getZ());


            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        if (Bukkit.getWorld("world").getBlockAt(x, y, z).getType() == Material.WATER || Bukkit.getWorld("world").getBlockAt(x, y, z).getType() == Material.STATIONARY_WATER || Bukkit.getWorld("world").getBlockAt(x, y, z).getType() == Material.LAVA|| Bukkit.getWorld("world").getBlockAt(x, y, z).getType() == Material.STATIONARY_LAVA ||
                         Bukkit.getWorld("world").getBlockAt(x, y, z).getType() == Material.COBBLESTONE || Bukkit.getWorld("world").getBlockAt(x, y, z).getType() == Material.OBSIDIAN || Bukkit.getWorld("world").getBlockAt(x, y, z).getType() == Material.STONE){
                            Bukkit.getWorld("world").getBlockAt(x, y, z).setType(Material.AIR);
                        }
                    }
                }
            }
        });
        return true;
    }


}
