package net.seanomik.energeticstorage.commands;

import net.seanomik.energeticstorage.files.ConfigFile;
import net.seanomik.energeticstorage.files.PlayersFile;
import net.seanomik.energeticstorage.utils.PermissionChecks;
import net.seanomik.energeticstorage.utils.Reference;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ESReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionChecks.canReloadPlugin(sender)) {
            sender.sendMessage(Reference.PREFIX + ChatColor.RED + "You don't have permission for this command!");
            return true;
        }

        ConfigFile.getConfig().reloadConfig();
        PlayersFile.getConfig().reloadConfig();

        // Re-cache the systems
        try {
            Reference.ES_SYSTEMS = PlayersFile.getAllSystems();
            sender.sendMessage(Reference.PREFIX + ChatColor.GREEN + "Reloaded!");
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(Reference.PREFIX + ChatColor.RED + "Failed to Reload! (" + e.getMessage() + ")");
        }

        return true;
    }
}
