package gg.scenarios.joust.managers.kit;

import gg.scenarios.joust.Joust;
import gg.scenarios.joust.managers.enums.KitType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class KitManager {

    private Joust joust = Joust.getInstance();

    public void giveKit(Player p) {
        if (joust.getTournament().getKitType().equals(KitType.BUILD)) {
            buildKit(p);
        } else if (joust.getTournament().getKitType().equals(KitType.UHC)) {
            uhcKit(p);
        }else if(joust.getTournament().getKitType().equals(KitType.IRON)){
            giveIronKit(p);
        }


    }

    private void giveIronKit(Player p) {
        PlayerInventory pinv = p.getInventory();

        p.setHealth(20.0D);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0F);
        p.setGameMode(GameMode.SURVIVAL);
        pinv.clear();

        ItemStack helmet = new ItemStack(Material.IRON_HELMET);

        ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);

        ItemStack leg = new ItemStack(Material.IRON_LEGGINGS);

        ItemStack boots = new ItemStack(Material.IRON_BOOTS);

        ItemStack sword = new ItemStack(Material.IRON_SWORD);

        ItemStack bow = new ItemStack(Material.BOW);


        pinv.setHelmet(helmet);
        pinv.setChestplate(chest);
        pinv.setLeggings(leg);
        pinv.setBoots(boots);
        pinv.setItem(0, sword);
        pinv.setItem(1, new ItemStack(Material.FISHING_ROD));
        pinv.setItem(2, bow);
        pinv.setItem(4, new ItemStack(Material.GOLDEN_APPLE, 2));
        pinv.setItem(9, new ItemStack(Material.ARROW, 32));
    }

    private void buildKit(Player p) {
        PlayerInventory pinv = p.getInventory();

        p.setHealth(20.0D);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0F);
        p.setGameMode(GameMode.SURVIVAL);
        pinv.clear();

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);

        ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        ItemStack leg = new ItemStack(Material.DIAMOND_LEGGINGS);
        leg.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);


        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE, 4);
        ItemMeta gMeta = goldenHead.getItemMeta();
        gMeta.setDisplayName(ChatColor.AQUA + "Golden Head");
        gMeta.setLore(Arrays.asList("You've crafted a Golden Head!", "Consuming this will grant you even greater effects", "than a normal Golden Apple!"));
        goldenHead.setItemMeta(gMeta);

        pinv.setHelmet(helmet);
        pinv.setChestplate(chest);
        pinv.setLeggings(leg);
        pinv.setBoots(boots);
        pinv.setItem(0, sword);
        pinv.setItem(1, new ItemStack(Material.FISHING_ROD));
        pinv.setItem(2, bow);
    //    pinv.setItem(3, new ItemStack(Material.LAVA_BUCKET));
     //   pinv.setItem(30, new ItemStack(Material.LAVA_BUCKET));
      //  pinv.setItem(4, new ItemStack(Material.WATER_BUCKET));
     //   pinv.setItem(31, new ItemStack(Material.WATER_BUCKET));
        pinv.setItem(5, new ItemStack(Material.BAKED_POTATO, 64));
        pinv.setItem(6, new ItemStack(Material.GOLDEN_APPLE, 6));
        pinv.setItem(7, goldenHead);
        pinv.setItem(8, new ItemStack(Material.STONE, 64));
        pinv.setItem(9, new ItemStack(Material.ARROW, 20));
        pinv.setItem(35, new ItemStack(Material.STONE, 64));
    }


    private void uhcKit(Player p) {
        PlayerInventory pinv = p.getInventory();

        p.setHealth(20.0D);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0F);
        p.setGameMode(GameMode.SURVIVAL);
        pinv.clear();

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);

        ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
        chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        ItemStack leg = new ItemStack(Material.IRON_LEGGINGS);
        leg.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);


        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta gMeta = goldenHead.getItemMeta();
        gMeta.setDisplayName(ChatColor.AQUA + "Golden Head");
        gMeta.setLore(Arrays.asList("You've crafted a Golden Head!", "Consuming this will grant you even greater effects", "than a normal Golden Apple!"));
        goldenHead.setItemMeta(gMeta);

        pinv.setHelmet(helmet);
        pinv.setChestplate(chest);
        pinv.setLeggings(leg);
        pinv.setBoots(boots);
        pinv.setItem(0, sword);
        pinv.setItem(1, new ItemStack(Material.FISHING_ROD));
        pinv.setItem(2, bow);
        //  pinv.setItem(3, new ItemStack(Material.LAVA_BUCKET));
        //  pinv.setItem(30, new ItemStack(Material.LAVA_BUCKET));
        //  pinv.setItem(4, new ItemStack(Material.WATER_BUCKET));
        //   pinv.setItem(31, new ItemStack(Material.WATER_BUCKET));
        pinv.setItem(3, new ItemStack(Material.BAKED_POTATO, 64));
        pinv.setItem(4, new ItemStack(Material.GOLDEN_APPLE, 3));
        pinv.setItem(5, goldenHead);
        pinv.setItem(9, new ItemStack(Material.ARROW, 16));

    }

}
