package gg.scenarios.joust.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Utils {

    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
