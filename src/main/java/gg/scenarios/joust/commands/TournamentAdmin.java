package gg.scenarios.joust.commands;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.managers.TournamentPlayer;
import gg.scenarios.joust.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

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
                    try {
                        System.out.println(joust.getTournament().setup());
                        TournamentPlayer.tournamentPlayerHashMap.keySet().forEach(sl -> joust.getTournament().getChallonge().getParticipants().add(sl));

                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage(ChatColor.RED + "Setting up tournament");

                } else if (args[0].equalsIgnoreCase("addmembers")) {

                    try {
                        joust.getTournament().randomize();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage(ChatColor.RED + "Added members");

                } else if (args[0].equalsIgnoreCase("start")) {
                    try {
                        joust.getTournament().start();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage(ChatColor.RED + "Starting tournament");

                }else if (args[0].equalsIgnoreCase("end")) {
                    try {
                        joust.getTournament().end();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    Utils.broadcast(joust.getPREFIX() + "&c&LThe tournament is now over view the bracket at");
                    Utils.broadcast("&c&o" + joust.getTournament().getBracketURL());
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
