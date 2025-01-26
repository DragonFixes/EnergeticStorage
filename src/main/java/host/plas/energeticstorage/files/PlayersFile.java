package host.plas.energeticstorage.files;

import host.plas.energeticstorage.EnergeticStorage;
import host.plas.energeticstorage.objects.ESDrive;
import host.plas.energeticstorage.objects.ESSystem;
import host.plas.energeticstorage.utils.ItemSerialization;
import host.plas.energeticstorage.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class PlayersFile extends SimpleConfiguration {
    public PlayersFile() {
        super("players.yml", EnergeticStorage.getInstance());
    }

    @Override
    public void init() {

    }

    public void onReload() {
        init();
    }

    public static PlayersFile getConfig() {
        return EnergeticStorage.getPlayersFile();
    }

    public static boolean doesPlayerHaveSystem(UUID uuid) {
        return getConfig().getResource().contains("players." + uuid + ".systems");
    }

    public static ConcurrentSkipListMap<UUID, List<ESSystem>> getAllSystems() {
        ConcurrentSkipListMap<UUID, List<ESSystem>> allSystems = new ConcurrentSkipListMap<>();

        for (String playerUUIDStr : getConfig().getResource().singleLayerKeySet("players")) {
            UUID playerUUID = UUID.fromString(playerUUIDStr);
            allSystems.put(playerUUID, getPlayersSystems(playerUUID));
        }

        return allSystems;
    }

    public static List<ESSystem> getPlayersSystems(UUID uuid) {
        List<ESSystem> systems = new ArrayList<>();
        for (String systemUUID : getConfig().singleLayerKeySet("players." + uuid + ".systems")) {
            String systemPath = "players." + uuid + ".systems." + systemUUID + ".";
            List<ESDrive> drives = new ArrayList<>();

            if (getConfig().getResource().contains(systemPath + "drives")) {
                for (String driveUUID : getConfig().getResource().singleLayerKeySet(systemPath + "drives")) {

                    Map<ItemStack, Integer> items = new HashMap();
                    if (getConfig().getResource().contains(systemPath + "drives." + driveUUID + ".items")) {
                        try {
                            JSONParser jsonParser = new JSONParser();
                            JSONArray itemJsonArray = (JSONArray) jsonParser.parse(getConfig().getResource().getString(systemPath + "drives." + driveUUID + ".items"));

                            for (int i = 0; i < itemJsonArray.size(); i++) {
                                JSONObject itemObject = (JSONObject) itemJsonArray.get(i);

                                Map.Entry<ItemStack, Integer> item = ItemSerialization.deserializeItem((String) itemObject.get("itemYAML"));

                                items.put(item.getKey(), item.getValue());
                            }
                        } catch (ParseException | InvalidConfigurationException e) {
                            e.printStackTrace();
                        }
                    }

                    int size = getConfig().getResource().getInt(systemPath + "drives." + driveUUID + ".size");

                    drives.add(new ESDrive(size, items));
                }
            }

            List<UUID> trustedUUIDs = new ArrayList<>();
            if (getConfig().getResource().contains(systemPath + "trustedUUIDs")) {
                try {
                    JSONArray trustedJson = (JSONArray) new JSONParser().parse(getConfig().getResource().getString(systemPath + "trustedUUIDs"));
                    for (int i = 0; i < trustedJson.size(); i++) {
                        JSONObject object = (JSONObject) trustedJson.get(i);

                        trustedUUIDs.add(UUID.fromString((String) object.get("UUID")));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            boolean isPublic = getConfig().getResource().getBoolean(systemPath + "public");
            ESSystem.SortOrder sortOrder;
            if (getConfig().getResource().contains(systemPath + "sortOrder")) {
                sortOrder = ESSystem.SortOrder.valueOf(getConfig().getResource().getString(systemPath + "sortOrder"));
            } else {
                sortOrder = ESSystem.SortOrder.ALPHABETICAL;
            }

            Location loc = Utils.convertStringToLocation(getConfig().getResource().getString(systemPath + "loc"));
            systems.add(new ESSystem(uuid, UUID.fromString(systemUUID), loc, drives, trustedUUIDs, isPublic, sortOrder));
        }

        return systems;
    }

    public static void savePlayerSystem(ESSystem esSystem) {
        String systemPath = "players." + esSystem.getOwner() + ".systems." + esSystem.getUUID() + ".";

        getConfig().write(systemPath + "loc", Utils.convertLocationToString(esSystem.getLocation()));
        getConfig().write(systemPath + "public", esSystem.isPublic());
        getConfig().write(systemPath + "sortOrder", esSystem.getSortOrder().toString());

        try {
            JSONArray jsonArray = new JSONArray();
            for (UUID uuid : esSystem.getTrustedPlayers()) {
                String object = "{\"UUID\":\"" + uuid.toString() + "\"}";
                JSONObject uuidJSON = (JSONObject) new JSONParser().parse(object);

                jsonArray.add(uuidJSON);
            }

            getConfig().write(systemPath + "trustedUUIDs", jsonArray.toJSONString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getConfig().write(systemPath + "drives", null);

        for (ESDrive drive : esSystem.getESDrives()) {
            if (drive == null) continue;
            getConfig().write(systemPath + "drives." + drive.getUuid() + ".size", drive.getSize());

            JSONArray itemsJson = new JSONArray();
            for (Map.Entry<ItemStack, Integer> entry : drive.getItems().entrySet()) {
                try {
                    String object = "{\"itemYAML\":\"" + ItemSerialization.serializeItem(entry.getKey(), entry.getValue()).replace("\"", "\\\"") + "\"}";
                    JSONObject itemJSON = (JSONObject) new JSONParser().parse(object);

                    itemsJson.add(itemJSON);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            getConfig().write(systemPath + "drives." + drive.getUuid() + ".items", itemsJson.toJSONString());
        }
    }

    public static void removePlayerSystem(UUID player, UUID uuid) {
        getConfig().write("players." + player + ".systems." + uuid, null);

        // Check if the config for the player is now empty, and if it is, then just remove their UUID from the config.
        if (getConfig().getResource().singleLayerKeySet("players." + player + ".systems").isEmpty()) {
            getConfig().write("players." + player, null);
        }
    }

    public static void savePlayerSystems(List<ESSystem> esSystems) {
        assert esSystems != null;
        for (ESSystem esSystem : esSystems) {
            savePlayerSystem(esSystem);
        }
    }
}

	