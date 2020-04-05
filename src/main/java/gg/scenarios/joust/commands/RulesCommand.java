package gg.scenarios.joust.commands;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RulesCommand implements CommandExecutor  {

    private Joust joust = Joust.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            ((Player) commandSender).openInventory(tournamentRules()) ;
        }
        return false;
    }

    private Inventory tournamentRules() {
        Inventory rules = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Rules");
        List<String> rulesLore = new ArrayList<>();

        rulesLore.add(ChatColor.RED +" " +ChatColor.STRIKETHROUGH +"-------------------------------------------------------------");
        rulesLore.add(ChatColor.GREEN + "» Tournament Rules");
        rulesLore.add(ChatColor.GREEN + "» Any use of hacked clients or .");
        rulesLore.add(ChatColor.GREEN + "» Illegal mods will result in a punishment.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» All tournament matches are final.");
        rulesLore.add(ChatColor.GREEN + "» There will be no rematches.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» Leaving the game whilst a tournament is.");
        rulesLore.add(ChatColor.GREEN + "» Ongoing might result in a disqualification.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» Exploiting game-breaking plugin .");
        rulesLore.add(ChatColor.GREEN + "» issues/abusing bugs will result in a punishment.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» Any sort of poor sportsmanship.");
        rulesLore.add(ChatColor.GREEN + "» Harassment or bullying will result in a punishment.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» Purposely delaying/disrupting the match will");
        rulesLore.add(ChatColor.GREEN + "» Result in a disqualification Repeated offenders will be punished.");

        rulesLore.add(ChatColor.RED +" " +ChatColor.STRIKETHROUGH +"-------------------------------------------------------------");

        ItemStack spacer = new ItemCreator(Material.STAINED_GLASS_PANE).setName(" ").get();


        ItemStack rule = new ItemCreator(Material.PAPER).setLore(rulesLore).setName(ChatColor.RED + "Rules").get();
        rules.setItem(13, rule);

        for (int i = 0; i < rules.getContents().length; i++) {
            if (rules.getContents()[i] == null){
                rules.setItem(i, spacer);
            }
        }

        return rules;
    }
}
