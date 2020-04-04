package gg.scenarios.joust.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Utils {

    @Getter
    private static NumberFormat nf = new DecimalFormat("##.##");


    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
