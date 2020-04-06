package gg.scenarios.joust.managers.arena;

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
            String[] spec = joust.getConfig().getString("ARENAS." + key + ".LOCATIONS.SPEC").split(";");

            int x1 = Integer.parseInt(loc1[0]);
            int y1 = Integer.parseInt(loc1[1]);
            int z1 = Integer.parseInt(loc1[2]);
            int x2 = Integer.parseInt(loc2[0]);
            int y2 = Integer.parseInt(loc2[1]);
            int z2 = Integer.parseInt(loc2[2]);
            int x3 = Integer.parseInt(spec[0]);
            int y3 = Integer.parseInt(spec[1]);
            int z3 = Integer.parseInt(spec[2]);

            Location location1 = new Location(Bukkit.getWorld("world"), x1, y1, z1);
            Location location2 = new Location(Bukkit.getWorld("world"), x2, y2, z2);
            Location specLocation = new Location(Bukkit.getWorld("world"), x3,y3,z3);
            new Arena(arenaName, location1, location2, specLocation);
        }

        System.out.println("SUCCESSFULLY LOADED " + count + " ARENAS");
    }

    public Arena getNextAvailableArena() throws Exception {
       return Arena.arenasList.stream().filter(Arena::isAvailable).findFirst().orElseThrow(Exception::new);
    }

    public Arena getArenaByName(String name) throws Exception {
        return Arena.arenasList.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElseThrow(Exception::new);
    }
}
