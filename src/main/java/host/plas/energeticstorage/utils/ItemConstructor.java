package host.plas.energeticstorage.utils;

import de.tr7zw.nbtapi.NBTItem;
import host.plas.energeticstorage.EnergeticStorage;
import host.plas.energeticstorage.Skulls;
import host.plas.energeticstorage.configs.obj.ItemInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemConstructor {
    private static final Material DRIVE_MATERIAL = Material.BLUE_DYE;

    public static ItemStack createSystemBlock() {
        ItemStack systemBlock = Skulls.Computer.getItemStack();

        NBTItem systemNBT = new NBTItem(systemBlock);
        systemNBT.setBoolean("ES_SYSTEM", true);
        systemBlock = systemNBT.getItem();

        ItemMeta systemMeta = systemBlock.getItemMeta();
        systemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "ES System");
        systemBlock.setItemMeta(systemMeta);

        return systemBlock;
    }

    public static ItemStack createDrive(int size, int filledItems, int filledTypes) {
        int smallSize = size / 1024;
        if (smallSize != 1 && smallSize != 4 && smallSize != 16 && smallSize != 64) {
            return null;
        }

        // Get the size string for the items name.
        String sizeString = size + "k";
        sizeString = sizeString.substring(0, sizeString.indexOf('k') - 3);
        if (sizeString.equals("65")) sizeString = "64";

        ItemInfo itemInfo = EnergeticStorage.getMainConfig().getItemInfo(sizeString + "k");

        ItemStack drive = new ItemStack(itemInfo.getMaterial(), 1);

        // Save the items data in NBT
        NBTItem driveNBT = new NBTItem(drive);
        driveNBT.setBoolean("ES_Drive", true);
        driveNBT.setInteger("ES_DriveMaxItemAmount", size);
        driveNBT.setInteger("ES_DriveMaxTypeAmount", Reference.getDriveMaxTypes());
        driveNBT.setString("ES_DriveUUID", UUID.randomUUID().toString());
        drive = driveNBT.getItem();

        ItemMeta driveMeta = drive.getItemMeta();
        if (driveMeta != null) {
            driveMeta.setCustomModelData(itemInfo.getCustomModelData());

            driveMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "ES " + sizeString + "k Drive");

            // Get color of items text
            ChatColor itemsColor = ChatColor.GREEN;
            if (filledItems >= size * 0.8) {
                itemsColor = ChatColor.RED;
            } else if (filledItems >= size * 0.5) {
                itemsColor = ChatColor.YELLOW;
            }

            // Get color of types text
            ChatColor typesColor = ChatColor.GREEN;
            if (filledTypes >= Reference.getDriveMaxTypes() * 0.8) {
                typesColor = ChatColor.RED;
            } else if (filledTypes >= Reference.getDriveMaxTypes() * 0.5) {
                typesColor = ChatColor.YELLOW;
            }

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "Filled Items: " + itemsColor + filledItems + ChatColor.BLUE + "/" + ChatColor.GREEN + size);
            lore.add(ChatColor.BLUE + "Filled Types: " + typesColor + filledTypes + ChatColor.BLUE + "/" + ChatColor.GREEN + Reference.getDriveMaxTypes());
            driveMeta.setLore(lore);

            drive.setItemMeta(driveMeta);

            drive.setItemMeta(driveMeta);
        }

        return drive;
    }
}
