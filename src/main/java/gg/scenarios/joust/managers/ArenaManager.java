package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ArenaManager {

    private Joust joust = Joust.getInstance();

    public static int count = 0;

    public void init() {
        for (String key : joust.getConfig().getConfigurationSection("ARENAS").getKeys(false)) {
            count++;
            String arenaName = joust.getConfig().getString("ARENAS." + key + ".NAME");
            System.out.println(arenaName);
            String[] loc1 = joust.getConfig().getString("ARENAS." + key + ".LOCATIONS.SPAWN1").split(";");
            String[] loc2 = joust.getConfig().getString("ARENAS." + key + ".LOCATIONS.SPAWN2").split(";");

            int x1 = Integer.parseInt(loc1[0]);
            int y1 = Integer.parseInt(loc1[1]);
            int z1 = Integer.parseInt(loc1[2]);
            int x2 = Integer.parseInt(loc2[0]);
            int y2 = Integer.parseInt(loc2[1]);
            int z2 = Integer.parseInt(loc2[2]);

            Location location1 = new Location(Bukkit.getWorld("world"), x1, y1, z1);
            Location location2 = new Location(Bukkit.getWorld("world"), x2, y2, z2);

            new Arenas(arenaName, location1, location2);
        }

        System.out.println("SUCCESSFULLY LOADED " + count + " ARENAS");
    }

    public Arenas getNextAvailableArena() throws Exception {
       return Arenas.arenasList.stream().filter(Arenas::isAvailable).findFirst().orElseThrow(Exception::new);
    }
}
