package host.plas.energeticstorage.listeners;

import host.plas.energeticstorage.EnergeticStorage;
import host.plas.energeticstorage.files.PlayersFile;
import host.plas.energeticstorage.objects.ESDrive;
import host.plas.energeticstorage.objects.ESSystem;
import host.plas.energeticstorage.utils.ItemConstructor;
import host.plas.energeticstorage.utils.PermissionChecks;
import host.plas.energeticstorage.utils.Reference;
import host.plas.energeticstorage.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.LinkedList;
import java.util.List;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreakListener(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.PLAYER_HEAD || event.getBlock().getType() == Material.PLAYER_WALL_HEAD) {
            Block block = event.getBlock();
            Player player = event.getPlayer();

            if (Utils.isBlockASystem(block)) {
                ESSystem esSystem = Utils.findSystemAtLocation(block.getLocation());

                if (esSystem != null) {
                    if (esSystem.isPlayerTrusted(player) || esSystem.getOwner().equals(player.getUniqueId()) || PermissionChecks.canDestroyUntrustedSystems(player)) {
                        for (ESDrive drive : esSystem.getESDrives()) {
                            block.getLocation().getWorld().dropItemNaturally(block.getLocation(), drive.getDriveItem());
                        }

                        // Remove the system from cache and storage
                        Bukkit.getScheduler().runTaskAsynchronously(EnergeticStorage.getInstance(), () -> {
                            PlayersFile.removePlayerSystem(player.getUniqueId(), esSystem.getUUID());

                            List<ESSystem> systems = new LinkedList<>(Reference.ES_SYSTEMS.get(player.getUniqueId()));
                            systems.removeIf(esSystem::equals);
                            Reference.ES_SYSTEMS.replace(player.getUniqueId(), systems);
                        });

                        // Only drop the system if they're not in creative.
                        event.setDropItems(false);
                        if (player.getGameMode() != GameMode.CREATIVE) {
                            event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), ItemConstructor.createSystemBlock());
                        }
                    } else {
                        event.setCancelled(true);
                        player.sendMessage(Reference.PREFIX + ChatColor.RED + "You are not trusted to this system!");
                    }
                }
            }
        }
    }
}
