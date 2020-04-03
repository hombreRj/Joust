package gg.scenarios.joust.commands;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.managers.TournamentPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TournamentAdmin implements CommandExecutor {

    private Joust joust = Joust.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission("tournament.admin")) {
                if (!(args.length > 0)) {
                    player.sendMessage(ChatColor.RED + "/tournament <host:setname:setdesc:setnum>");
                } else if (args[0].equalsIgnoreCase("host")) {
                    joust.getTournament().getHost().add(TournamentPlayer.getTournamentPlayer(player));
                    player.sendMessage(ChatColor.RED + "You are now host");
                } else if (args[0].equalsIgnoreCase("setname")) {
                    joust.getTournament().setName(args[1]);
                    player.sendMessage(ChatColor.RED + "Set the name to: " + args[1]);
                } else if (args[0].equalsIgnoreCase("setnum")) {
                    joust.getTournament().setTournamentNum(args[1]);
                    player.sendMessage(ChatColor.RED + "Set the tournament to: " + args[1]);

                } else if (args[0].equalsIgnoreCase("setdesc")) {
                    player.sendMessage(ChatColor.RED + "Set the description to: " + args[1]);

                    joust.getTournament().setDescription(args[1]);
                } else if (args[0].equalsIgnoreCase("setup")) {
                    joust.getTournament().setup();
                    player.sendMessage(ChatColor.RED + "Setting up tournament");

                } else if (args[0].equalsIgnoreCase("addmembers")) {
                    joust.getTournament().addMembers();
                    joust.getTournament().randomize();
                    player.sendMessage(ChatColor.RED + "Added members");

                } else if (args[0].equalsIgnoreCase("start")) {
                    joust.getTournament().start();
                    player.sendMessage(ChatColor.RED + "Starting tournament");
                }
            } else {
                player.sendMessage(ChatColor.RED + "/tournament <host:setname:setdesc:setnum>");

            }

        } else {
            commandSender.sendMessage(ChatColor.RED + "In game only");
        }
        return false;
    }
}
