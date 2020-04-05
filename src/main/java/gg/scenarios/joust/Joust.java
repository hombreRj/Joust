package gg.scenarios.joust;

import gg.scenarios.joust.commands.BracketCommand;
import gg.scenarios.joust.commands.RulesCommand;
import gg.scenarios.joust.commands.TournamentAdmin;
import gg.scenarios.joust.listeners.PlayerListener;
import gg.scenarios.joust.managers.ArenaManager;
import gg.scenarios.joust.managers.Arenas;
import gg.scenarios.joust.managers.Tournament;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class Joust extends JavaPlugin {

    @Getter
    private static Joust instance;
    public static List<UUID> mods = new ArrayList<>();
    private ArenaManager arenaManager;
    @Getter private Tournament tournament;
    private final String API_KEY = "AeoqInZkvafvAeTuiHNat7aADcJdLdxOjmiNVLPT";

    private final String PREFIX = "&8[&3Tournament&8] &r";



    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        arenaManager = new ArenaManager();
        arenaManager.init();
        tournament = new Tournament();
        tournament.setApiKey(API_KEY);

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("tournament").setExecutor(new TournamentAdmin());
        getCommand("bracket").setExecutor(new BracketCommand());
        getCommand("rules").setExecutor(new RulesCommand());
    }


    @Override
    public void onDisable(){
        saveConfig();
    }


}
