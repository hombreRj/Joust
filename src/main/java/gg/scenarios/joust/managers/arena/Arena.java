package gg.scenarios.joust.managers.arena;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.managers.Tournament;
import gg.scenarios.joust.managers.TournamentMatch;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Arena {

    private Joust joust = Joust.getInstance();

    public static List<Arena> arenasList = new ArrayList<>();

    private String name;
    private Location spawn1, spawn2, specLocation;
    private boolean available;
    private TournamentMatch match;

    public Arena(String name, Location spawn1, Location spawn2, Location specLocation) {
        this.name = name;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.specLocation = specLocation;
        available = true;
        arenasList.add(this);
    }






    public void clear(){
        System.out.println("Started Clearing Arena: " + this.getName());
        System.out.println("Finished Clearing Arena: " +getName());
    }

}
