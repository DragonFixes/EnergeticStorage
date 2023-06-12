package net.seanomik.energeticstorage.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GUIHelper {
    /**
     * Creates a GUI Item with a given material, display name, and optional lore
     *
     * @param material The material to use
     * @param name     The name of the item
     * @param lore     Optional lore to add to the item
     * @return An item stack to be used in the GUI.
     */
    public static ItemStack createGuiItem(Material material, String name, String... lore) {
        return createGuiItem(material, name, List.of(lore));
    }

    /**
     * Creates a GUI item with given material, display name, and required lore
     *
     * @param material The material to use
     * @param name     The name of the item
     * @param lore     Optional lore to add to the item
     * @return An item stack to be used in the GUI.
     */
    public static ItemStack createGuiItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + name);
            if(!lore.isEmpty()) {
                meta.setLore(lore);
            }
        }
        item.setItemMeta(meta);

        return item;
    }
}
