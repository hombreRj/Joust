package gg.scenarios.joust.commands;

import gg.scenarios.joust.Joust;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BracketCommand implements CommandExecutor {

    private Joust joust = Joust.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (joust.getTournament().getBracketURL() == null){
            commandSender.sendMessage(ChatColor.RED + "The bracket has not been generated yet!");
        }else{
            commandSender.sendMessage(ChatColor.GREEN + "The link to the tournament bracket is: " + ChatColor.UNDERLINE + joust.getTournament().getBracketURL());
        }
        return false;
    }
}
