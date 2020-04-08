package gg.scenarios.joust.commands;

import gg.scenarios.joust.Joust;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand implements CommandExecutor {

    private Joust joust = Joust.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;

            player.sendMessage(ChatColor.BLUE +"" + ChatColor.STRIKETHROUGH + "----------------------------------");
            player.sendMessage(ChatColor.RED + "Host:" );
            player.sendMessage(ChatColor.BLUE +"" + ChatColor.STRIKETHROUGH + "----------------------------------");

        }else{
            sender.sendMessage(ChatColor.RED + "You cannot use this command");
        }
        return false;
    }
}
