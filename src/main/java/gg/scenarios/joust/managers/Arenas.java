package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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



}
