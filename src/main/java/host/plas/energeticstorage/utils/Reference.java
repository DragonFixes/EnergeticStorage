package host.plas.energeticstorage.utils;

import host.plas.energeticstorage.EnergeticStorage;
import host.plas.energeticstorage.gui.ESDriveGUI;
import host.plas.energeticstorage.gui.ESSystemSecurityGUI;
import host.plas.energeticstorage.gui.ESTerminalGUI;
import host.plas.energeticstorage.objects.ESSystem;
import org.bukkit.ChatColor;

import java.util.*;

public class Reference {

    public static String PREFIX = ChatColor.AQUA + "" + ChatColor.ITALIC + "[Energetic Storage] " + ChatColor.RESET;

    public static ESTerminalGUI ES_TERMINAL_GUI = new ESTerminalGUI();
    public static ESDriveGUI ES_DRIVE_GUI = new ESDriveGUI();
    public static ESSystemSecurityGUI ES_SYSTEM_SECURITY_GUI = new ESSystemSecurityGUI();

    public static Map<UUID, List<ESSystem>> ES_SYSTEMS = new HashMap<>();

    public static int getDriveMaxTypes() {
        return EnergeticStorage.getMainConfig().getDriveMaxTypes();
    }
}
