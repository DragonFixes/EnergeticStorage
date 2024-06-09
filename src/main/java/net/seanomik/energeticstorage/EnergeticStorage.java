package net.seanomik.energeticstorage;

import net.seanomik.energeticstorage.commands.ESGiveCommand;
import net.seanomik.energeticstorage.commands.ESReloadCommand;
import net.seanomik.energeticstorage.files.ConfigFile;
import net.seanomik.energeticstorage.files.PlayersFile;
import net.seanomik.energeticstorage.listeners.BlockBreakListener;
import net.seanomik.energeticstorage.listeners.BlockPlaceListener;
import net.seanomik.energeticstorage.listeners.PlayerInteractListener;
import net.seanomik.energeticstorage.objects.ESSystem;
import net.seanomik.energeticstorage.tasks.HopperTask;
import net.seanomik.energeticstorage.utils.ItemRecipes;
import net.seanomik.energeticstorage.utils.Reference;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static org.bukkit.Bukkit.getCommandMap;

public final class EnergeticStorage extends JavaPlugin implements Listener {
    private static EnergeticStorage plugin;
    private static HopperTask hopperTask;

    @Override
    public void onEnable() {
        plugin = this;

        registerCommands();
        registerListener();
        ItemRecipes.registerRecipes();

        ConfigFile.getConfig().saveDefaultConfig();
        PlayersFile.getConfig().saveDefaultConfig();

        Reference.ES_SYSTEMS = PlayersFile.getAllSystems();

        if (ConfigFile.isHopperInputEnabled()) {
            hopperTask = new HopperTask();
            hopperTask.runTaskTimerAsynchronously(this, 0L, 8L);
        }
    }

    private void registerCommands() {
        try {
            Class<PluginCommand> pluginClass = PluginCommand.class;
            var constructor = pluginClass.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            var esGiveCommandClass = new ESGiveCommand();
            PluginCommand esGiveCommand = constructor.newInstance("esgive", this);
            esGiveCommand.setAliases(List.of("egive"));
            esGiveCommand.setExecutor(esGiveCommandClass);
            esGiveCommand.setTabCompleter(esGiveCommandClass);
            esGiveCommand.setDescription("Give an Energetic Storage item.");
            esGiveCommand.setUsage("/esgive");
            esGiveCommand.setPermission(new Permission("energeticstorage.esgive", PermissionDefault.OP).getName());
            getCommandMap().register(this.getName().toLowerCase(), esGiveCommand);

            PluginCommand esReloadCommand = constructor.newInstance("esreload", this);
            esReloadCommand.setAliases(List.of("ereload"));
            esReloadCommand.setExecutor(new ESReloadCommand());
            esReloadCommand.setDescription("Reload the Energetic Storage plugin.");
            esReloadCommand.setUsage("/esreload");
            esReloadCommand.setPermission(new Permission("energeticstorage.esreload", PermissionDefault.OP).getName());
            getCommandMap().register(this.getName().toLowerCase(), esReloadCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerListener() {
        getServer().getPluginManager().registerEvents(Reference.ES_TERMINAL_GUI, this);
        getServer().getPluginManager().registerEvents(Reference.ES_DRIVE_GUI, this);
        getServer().getPluginManager().registerEvents(Reference.ES_SYSTEM_SECURITY_GUI, this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    /*public void cachePlayersSystems(Player player) {
        if (PlayersFile.doesPlayerHaveSystem(player.getUniqueId())) {
            Reference.ES_SYSTEMS.put(player.getUniqueId(), PlayersFile.getPlayersSystems(player.getUniqueId()));
        }
    }*/

    /**
     * Saves all player systems.
     */
    private void savePlayerSystems() {
        for (Map.Entry<UUID, List<ESSystem>> systemEntry : Reference.ES_SYSTEMS.entrySet()) {
            PlayersFile.savePlayerSystems(systemEntry.getValue());
        }
    }

    @EventHandler
    public void onWorldSaveEvent(WorldSaveEvent event) {
        // Save the player systems when the world is saved
        savePlayerSystems();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // Save the player systems on shutdown to prevent item loss
        savePlayerSystems();
    }

    public static EnergeticStorage getPlugin() {
        return plugin;
    }

    public static HopperTask getHopperTask() {
        return hopperTask;
    }

    public static void setHopperTask(HopperTask hopperTask) {
        EnergeticStorage.hopperTask = hopperTask;
    }
}
