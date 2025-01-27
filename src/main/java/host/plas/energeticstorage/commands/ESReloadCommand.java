package host.plas.energeticstorage.commands;

import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.SimplifiedCommand;
import host.plas.energeticstorage.EnergeticStorage;
import host.plas.energeticstorage.files.PlayersFile;
import host.plas.energeticstorage.utils.PermissionChecks;
import host.plas.energeticstorage.utils.Reference;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ESReloadCommand extends SimplifiedCommand {
    public ESReloadCommand() {
        super("esreload", EnergeticStorage.getInstance());
    }

    @Override
    public boolean command(CommandContext ctx) {
        CommandSender sender = ctx.getCommandSender();
        if (!PermissionChecks.canReloadPlugin(sender)) {
            sender.sendMessage(Reference.PREFIX + ChatColor.RED + "You don't have permission for this command!");
            return true;
        }

        EnergeticStorage.getMainConfig().onReload();
        EnergeticStorage.getPlayersFile().onReload();

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
