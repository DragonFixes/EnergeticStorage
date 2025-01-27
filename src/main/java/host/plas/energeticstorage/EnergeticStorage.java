package host.plas.energeticstorage;

import host.plas.bou.BetterPlugin;
import host.plas.bou.events.ListenerConglomerate;
import lombok.Getter;
import lombok.Setter;
import host.plas.energeticstorage.commands.ESGiveCommand;
import host.plas.energeticstorage.commands.ESReloadCommand;
import host.plas.energeticstorage.configs.MainConfig;
import host.plas.energeticstorage.files.PlayersFile;
import host.plas.energeticstorage.listeners.BlockBreakListener;
import host.plas.energeticstorage.listeners.BlockPlaceListener;
import host.plas.energeticstorage.listeners.PlayerInteractListener;
import host.plas.energeticstorage.objects.ESSystem;
import host.plas.energeticstorage.tasks.HopperTask;
import host.plas.energeticstorage.utils.ItemRecipes;
import host.plas.energeticstorage.utils.Reference;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static org.bukkit.Bukkit.getCommandMap;

public final class EnergeticStorage extends BetterPlugin implements ListenerConglomerate {
    @Getter @Setter
    private static EnergeticStorage instance;
    @Getter @Setter
    private static HopperTask hopperTask;

    @Getter @Setter
    private static MainConfig mainConfig;
    @Getter @Setter
    private static PlayersFile playersFile;

    @Getter @Setter
    private static ESGiveCommand esGiveCommand;
    @Getter @Setter
    private static ESReloadCommand esReloadCommand;

    @Override
    public void onEnable() {
        instance = this;

        mainConfig = new MainConfig();
        playersFile = new PlayersFile();

        registerCommands();
        registerListener();
        ItemRecipes.registerRecipes();

        Reference.ES_SYSTEMS = PlayersFile.getAllSystems();

        if (getMainConfig().allowHopperInput()) {
            hopperTask = new HopperTask();
            hopperTask.runTaskTimerAsynchronously(this, 0L, 8L);
        }
    }

    private void registerCommands() {
        setEsGiveCommand(new ESGiveCommand());
        setEsReloadCommand(new ESReloadCommand());
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
}
