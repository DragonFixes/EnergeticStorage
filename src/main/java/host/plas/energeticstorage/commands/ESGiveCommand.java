package host.plas.energeticstorage.commands;

import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.SimplifiedCommand;
import host.plas.bou.utils.obj.StringArgument;
import host.plas.energeticstorage.EnergeticStorage;
import host.plas.energeticstorage.files.PlayersFile;
import host.plas.energeticstorage.utils.ItemConstructor;
import host.plas.energeticstorage.utils.PermissionChecks;
import host.plas.energeticstorage.utils.Reference;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class ESGiveCommand extends SimplifiedCommand {
    public ESGiveCommand() {
        super("esgive", EnergeticStorage.getInstance());
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext ctx) {
        ConcurrentSkipListSet<String> tab = new ConcurrentSkipListSet<>();

        switch (ctx.getArgCount()) {
            case 1:
                tab.addAll(Arrays.asList("drive", "system"));
                break;
            case 2:
                if (ctx.getStringArg(0).equals("drive")) {
                    tab.addAll(Arrays.asList("1k", "4k", "16k", "64k"));
                } else if (ctx.getStringArg(0).equals("system")) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        tab.add(ChatColor.stripColor(player.getDisplayName()));
                    }
                }
                break;
            case 3:
                if (ctx.getStringArg(0).equals("drive")) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        tab.add(ChatColor.stripColor(player.getDisplayName()));
                    }
                }

                break;
        }

        return tab;
    }

    private String generateCommandUsage(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("drive")) {
                return Reference.PREFIX + "Usage: /esgive drive [1k, 4k, 16k, 64k] (player)";
            }

            return Reference.PREFIX + "Usage: /esgive [drive/system] [1k, 4k, 16k, 64k] (player)";
        } else {
            return Reference.PREFIX + "Usage: /esgive [drive/system] [1k, 4k, 16k, 64k] (player)";
        }
    }

    @Override
    public boolean command(CommandContext ctx) {
        CommandSender sender = ctx.getCommandSender();
        if (! PermissionChecks.canESGive(sender)) {
            ctx.sendMessage(Reference.PREFIX + "&cYou don't have permission for this command!");

            return true;
        }

        if (ctx.getArgCount() == 0) {
            sender.sendMessage(generateCommandUsage(ctx.getArgs().stream().map(StringArgument::getContent).toArray(String[]::new)));
            return true;
        }

        switch (ctx.getStringArg(0)) { // Switch on item type
            case "save":
                PlayersFile.savePlayerSystems(Reference.ES_SYSTEMS.get(((Player) sender).getUniqueId()));
                sender.sendMessage("Saved systems!");
                break;
            case "system":
                //Player p = (Player) sender;

                if (ctx.getArgCount() == 2) {
                    if (!PermissionChecks.canESGiveOthers(sender)) {
                        sender.sendMessage(Reference.PREFIX + ChatColor.RED + "You don't have permission to give an item to another player!");

                        return true;
                    }

                    Player player = Bukkit.getPlayer(ctx.getStringArg(1));
                    if (player != null) {
                        player.getInventory().addItem(ItemConstructor.createSystemBlock());

                        sender.sendMessage(Reference.PREFIX + ChatColor.GREEN + "Gave an ES System to " + player.getDisplayName());
                    } else {
                        sender.sendMessage(Reference.PREFIX + ChatColor.RED + "Player does not exist or is not online!");
                    }
                } else if (ctx.getArgCount() == 1) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.getInventory().addItem(ItemConstructor.createSystemBlock());

                        sender.sendMessage(Reference.PREFIX + ChatColor.GREEN + "Gave an ES System to " + player.getDisplayName());
                    } else {
                        sender.sendMessage(Reference.PREFIX + ChatColor.RED + "Supply a player to run this command!");
                        sender.sendMessage(generateCommandUsage(ctx.getArgs().stream().map(StringArgument::getContent).toArray(String[]::new)));
                    }
                }
                break;
            case "drive":
                if (ctx.getArgCount() < 2) {
                    sender.sendMessage(generateCommandUsage(ctx.getArgs().stream().map(StringArgument::getContent).toArray(String[]::new)));
                    break;
                }

                if (ctx.getStringArg(1).equals("1k") || ctx.getStringArg(1).equals("4k") || ctx.getStringArg(1).equals("16k") || ctx.getStringArg(1).equals("64k")) {
                    int size = Integer.parseInt(ctx.getStringArg(1).replace("k", "")) * 1024;

                    if (ctx.getArgCount() == 3) {
                        if (!PermissionChecks.canESGiveOthers(sender)) {
                            sender.sendMessage(Reference.PREFIX + ChatColor.RED + "You don't have permission to give an item to another player!");

                            return true;
                        }

                        Player player = Bukkit.getPlayer(ctx.getStringArg(2));
                        if (player != null) {
                            player.getInventory().addItem(ItemConstructor.createDrive(size, 0, 0));

                            sender.sendMessage(Reference.PREFIX + ChatColor.GREEN + "Gave an ES Drive to " + player.getDisplayName());
                        } else {
                            sender.sendMessage(Reference.PREFIX + ChatColor.RED + "Player does not exist or is not online!");
                        }
                    } else {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            player.getInventory().addItem(ItemConstructor.createDrive(size, 0, 0));

                            sender.sendMessage(Reference.PREFIX + ChatColor.GREEN + "Gave an ES Drive to " + player.getDisplayName());
                        } else {
                            sender.sendMessage(Reference.PREFIX + ChatColor.RED + "Supply a player to run this command!");
                            sender.sendMessage(generateCommandUsage(ctx.getArgs().stream().map(StringArgument::getContent).toArray(String[]::new)));
                        }
                    }
                } else {
                    sender.sendMessage(generateCommandUsage(ctx.getArgs().stream().map(StringArgument::getContent).toArray(String[]::new)));
                }

                break;
        }

        return true;
    }
}
