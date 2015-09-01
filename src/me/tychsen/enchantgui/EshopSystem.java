package me.tychsen.enchantgui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class EshopSystem {
    private Map<String, Integer> playerNavigation;
    private int inventorySize;
    EshopEnchants enchants;

    public EshopSystem() {
        playerNavigation = new HashMap<>();
        inventorySize = 36;
        enchants = new EshopEnchants();
    }

    public void showMainMenu(Player p) {
        playerNavigation.put(p.getName(), 0);

        Inventory inv = p.getServer().createInventory(p, inventorySize, "EnchantGUI");
        populateInventory(inv, p);
        p.openInventory(inv);
    }

    public void showEnchantPage(Player p, ItemStack item) {
        playerNavigation.put(p.getName(), 1);

        Inventory inv = p.getServer().createInventory(p, inventorySize,
                "EnchantGUI: " + item.getItemMeta().getDisplayName());
        inv.setContents(createItemlistForEnchant(p, item));

        ItemStack backitem = new ItemStack(Material.EMERALD);
        ItemMeta meta = backitem.getItemMeta();
        meta.setDisplayName("Go back");
        backitem.setItemMeta(meta);
        inv.setItem(27, backitem);

        p.openInventory(inv);
    }

    public int getPlayerCurrentPosition(Player p) {
        if (playerNavigation.containsKey(p.getName())) {
            return playerNavigation.get(p.getName());
        } else {
            throw new NoSuchElementException("Player is missing from navigation list.");
        }
    }

    public void purchaseEnchant(Player p, ItemStack item) {
        p.getServer().getLogger().info("Player " + ChatColor.GREEN + p.getName() + ChatColor.RESET +
        " bought an enchant!");
    }

    public boolean hasEnchantLevelPerms(Player p, Enchantment ench, int level) {
        String base = "eshop.enchants";
        String name = ench.getName().toLowerCase();

        if (p.hasPermission(base + ".all") || p.hasPermission(base + name + ".all") ||
                p.hasPermission(base + name + "." + level) || p.isOp()) {
            return true;
        }
        else {
            return false;
        }
    }

    private void populateInventory(Inventory inv, Player p) {
        ItemStack[] items = enchants.generateMenuItemsForPlayer(p);
        inv.setContents(items);
    }

    private ItemStack[] createItemlistForEnchant(Player p, ItemStack item) {
        Enchantment ench = item.getEnchantments().keySet().toArray(new Enchantment[1])[0];
        int maxLevel = ench.getMaxLevel();
        String name = item.getItemMeta().getDisplayName();
        List<ItemStack> itemlist = new ArrayList<>();

        for (int i = 1; i <= maxLevel; i++) {
            ItemStack tmp;
            if (hasEnchantLevelPerms(p, ench, i)) {
                tmp = item.clone();
                ItemMeta meta = tmp.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.GOLD + "Level: " + i));
                tmp.setItemMeta(meta);
            } else {
                tmp = new ItemStack(Material.AIR);
            }

            itemlist.add(tmp);
        }

        return itemlist.toArray(new ItemStack[itemlist.size()]);
    }
}
