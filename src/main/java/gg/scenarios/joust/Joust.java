package gg.scenarios.joust;

import gg.scenarios.joust.managers.ArenaManager;
import gg.scenarios.joust.managers.Arenas;
import gg.scenarios.joust.managers.Tournament;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


@Getter
public class Joust extends JavaPlugin {

    @Getter
    private static Joust instance;
    private ArenaManager arenaManager;
    @Getter private Tournament tournament;
    private final String API_KEY = "iBxWp4qs86txMquXmRGd3nVJ3zlIYUJGrNYfoxnv";


    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        arenaManager = new ArenaManager();
        arenaManager.init();
        tournament = new Tournament();
    }


    @Override
    public void onDisable(){
        saveConfig();
    }


}
