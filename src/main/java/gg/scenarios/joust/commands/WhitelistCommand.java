package gg.scenarios.joust.commands;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.managers.TournamentPlayer;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.function.Predicate;

public class WhitelistCommand implements CommandExecutor {
    public static int count;

    private final Joust uhc = Joust.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("whitelist")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("in game only");
            } else {
                Player player = (Player) sender;

                if (player.hasPermission("uhc.whitelist")) {

                    if (!(args.length > 0)) {
                        player.sendMessage(ChatColor.GOLD + "--------- WHITELIST HELP --------");
                        player.sendMessage(ChatColor.DARK_GREEN + "/wl on|off|all");
                        player.sendMessage(ChatColor.DARK_AQUA + "/wl add <name>");
                        player.sendMessage(ChatColor.DARK_AQUA + "/wl remove <name>");
                        player.sendMessage(ChatColor.GOLD + "--------- WHITELIST HELP --------");
                    } else if (args[0].equals("on")) {
                        uhc.getTournament().setWhitelist(true);
                        player.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.LIGHT_PURPLE + "enabled" + ChatColor.YELLOW + " the whitelist");

                    } else if (args[0].equalsIgnoreCase("off")) {
                        uhc.getTournament().setWhitelist(false);
                        player.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.LIGHT_PURPLE + "disabled" + ChatColor.YELLOW + " the whitelist");
                    } else if (args[0].equalsIgnoreCase("add")) {
                        if (args[1] == null) {
                            player.sendMessage(ChatColor.YELLOW + "please enter a username");

                        } else {
                            uhc.getTournament().getWhitelistSet().add(UUID.fromString(insertDashUUID(getUUID(args[1]))));
                            //Utils.getOfflinePlayer(args[1], offlinePlayer -> uhc.getGameManager().getWhitelist().add(offlinePlayer.getUniqueId()));
                            player.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.YELLOW + " to the whitelist");
                        }
                    } else if (args[0].equalsIgnoreCase("all")) {
                        uhc.getTournament().getWhitelistSet().removeAll(uhc.getTournament().getWhitelistSet());
                        uhc.getTournament().getPlayers().stream().filter(TournamentPlayer::isOnline).forEach(uuid -> {
                            uhc.getTournament().getWhitelistSet().add(uuid.getPlayer().getUniqueId());
                            count++;
                        });
//                        uhc.getGameManager().getWhitelist().forEach(uuid -> {
//                            if (UHCPlayer.getByUUID(uuid).isOnline()) {
//                                if (UHCPlayer.getByUUID(uuid).isSpectating()) {
//                                    try {
//                                        uhc.getGameManager().getPlayers().remove(UHCPlayer.getByUUID(uuid));
//                                    } catch (Exception e) {
//
//                                    }
//                                    uhc.getGameManager().getPlayers().add(UHCPlayer.getByUUID(uuid));
//                                }
//                            }
//                        });
                        player.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.LIGHT_PURPLE + uhc.getTournament().getPlayers().size() + ChatColor.YELLOW + " players to the whitelist");

                    }

                } else {
                    player.sendMessage("no perm kiddo");
                }
            }
        }
        return false;
    }

    public static String insertDashUUID(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }

    public static String getUUID(String playerName) {

        try {
            HttpResponse<JsonNode> response = Unirest.get("https://api.mojang.com/users/profiles/minecraft/" + playerName)
                    .header("accept", "application/json")
                    .asJson();

            return response.getBody().getObject().getString("id");
        } catch (Exception e) {
            return "";
        }
    }
}
