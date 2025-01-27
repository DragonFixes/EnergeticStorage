package host.plas.energeticstorage.objects;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import host.plas.energeticstorage.utils.ItemConstructor;
import host.plas.energeticstorage.utils.ItemSerialization;
import host.plas.energeticstorage.utils.Reference;
import host.plas.energeticstorage.utils.Utils;
import org.apache.commons.text.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

@Getter @Setter
public class ESDrive implements Cloneable, ConfigurationSerializable {
    private UUID uuid;
    private int size;
    private Map<ItemStack, Integer> items = new HashMap<>(); // Item, amount

    public ESDrive(int size) {
        this.size = size;
    }

    protected ESDrive(UUID uuid, int size, Map<ItemStack, Integer> items) {
        this.uuid = uuid;
        this.size = size;
        this.items = items;
    }

    public ESDrive(int size, Map<ItemStack, Integer> items) {
        this(size);
        uuid = UUID.randomUUID();

        this.items = items;
    }

    public ESDrive(ItemStack driveItem) {
        NBTItem driveNBT = new NBTItem(driveItem);

        if (driveNBT.hasKey("ES_DriveItems")) {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray itemJsonArray = (JSONArray) jsonParser.parse(driveNBT.getString("ES_DriveItems"));

                for (int i = 0; i < itemJsonArray.size(); i++) {
                    JSONObject itemObject = (JSONObject) itemJsonArray.get(i);

                    Map.Entry<ItemStack, Integer> item = ItemSerialization.deserializeItem((String) itemObject.get("itemYAML"));

                    items.put(item.getKey(), item.getValue());
                }
            } catch (ParseException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        size = driveNBT.getInteger("ES_DriveMaxItemAmount");
        uuid = (driveNBT.hasKey("ES_DriveUUID")) ? UUID.fromString(driveNBT.getString("ES_DriveUUID")) : UUID.randomUUID();
    }

    public static ItemStack sterilizedItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta.hasLore()) {
            List<String> itemLore = meta.getLore();
            itemLore.removeIf(lore -> lore.startsWith("Amount:"));

            meta.setLore(itemLore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public int getFilledSpace() {
        int filled = 0;

        for (int amount : items.values()) {
            filled += amount;
        }

        return filled;
    }

    public int getFilledTypes() {
        List<Material> foundItems = new ArrayList<>();

        for (ItemStack item : items.keySet()) {
            if (!foundItems.contains(item.getType())) {
                foundItems.add(item.getType());
            }
        }

        return foundItems.size();
    }

    public ESDrive clone() {
        try {
            ESDrive drive = (ESDrive) super.clone();
            if (this.items != null) {
                items = new HashMap<>(items);
            }

            return drive;
        } catch (CloneNotSupportedException var2) {
            throw new Error(var2);
        }
    }

    public boolean canAddItem(ItemStack item) {
        if (Utils.isItemValid(item)) {
            // If the item is valid, we're full on types, we have the item in the drive, and we're not full on space, return true.
            // else always just cascase down and check if we have space.
            if (Utils.containsSimilarItem(new ArrayList<>(items.keySet()), item, true)) {
                return getFilledSpace() < size;
            } else {
                if (getFilledTypes() < Reference.getDriveMaxTypes()) {
                    return getFilledSpace() < size;
                }

                return false;
            }
        } else {
            return getFilledSpace() < size;
        }
    }

    public boolean addItem(ItemStack item) {
        // Sterilize items of the "Amount: " lore
        item = sterilizedItem(item.clone());

        if (canAddItem(item)) {
            // The item is contained, then update the amount.
            if (Utils.containsSimilarItem(new ArrayList<>(items.keySet()), item, true)) {
                int amount = (int) items.values().toArray()[Utils.indexOfSimilarItem(new ArrayList<>(items.keySet()), item)] + item.getAmount();
                items = Utils.removeSimilarItem(items, item);
                items.put(item, amount);
            } else {
                items.put(item, item.getAmount());
            }

            return true;
        }

        return false;
    }
    
    public ItemStack removeItem(ItemStack item) {
        // If there isn't enough items stored to take out the requested amount, then just take out all that we can.
        int foundItemAmount = (int) items.values().toArray()[Utils.indexOfSimilarItem(new ArrayList<>(items.keySet()), item)];
        if (foundItemAmount - item.getAmount() < 1) {
            items = Utils.removeSimilarItem(items, item);
            item.setAmount(foundItemAmount);
        } else {
            int newAmount = foundItemAmount - item.getAmount();

            items = Utils.removeSimilarItem(items, item);
            items.put(item, newAmount);
        }

        return item;
    }

    public int getItemAmount(ItemStack item) {
        return (int) items.values().toArray()[Utils.indexOfSimilarItem(new ArrayList<>(items.keySet()), item)];
    }

    private String exceptionMessage(Exception e) {
        return "An exception occurred in ESDrive (UUID:" + uuid + ", Exception: " + e.getMessage() + ")";
    }

    public ItemStack getDriveItem() {
        try {
            ItemStack drive = ItemConstructor.createDrive(size, getFilledSpace(), getFilledTypes());
            NBTItem driveNBT = new NBTItem(drive);

            JSONArray itemsJson = new JSONArray();
            for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
                try {
                    String object = "{\"itemYAML\":\"" + StringEscapeUtils.escapeJson(ItemSerialization.serializeItem(entry.getKey(), entry.getValue())) + "\"}";
                    JSONObject itemJSON = (JSONObject) new JSONParser().parse(object);

                    itemsJson.add(itemJSON);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            driveNBT.setString("ES_DriveItems", itemsJson.toJSONString());
            drive = driveNBT.getItem();

            return drive;
        } catch (Exception e) {
            System.out.println(exceptionMessage(e));
        }

        return null;
    }

    // @TODO: Implement (has not been tested)
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap();
        result.put("uuid", uuid);
        result.put("size", size);

        if (!items.isEmpty()) {
            List<Object> itemsSerialized = new ArrayList<>();
            for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
                Map<String, Object> itemSerialized = new LinkedHashMap<>();
                itemSerialized.put("amount", entry.getValue());
                itemSerialized.put("item", entry.getKey().serialize());
                itemsSerialized.add(itemSerialized);
            }
            result.put("items", itemsSerialized);
        }

        return result;
    }

    // @TODO: Implement (has not been tested)
    @NotNull
    public static ESDrive deserialize(@NotNull Map<String, Object> args) {
        UUID uuid = (UUID) args.get("uuid");
        int size = ((Number)args.get("size")).intValue();
        Map<ItemStack, Integer> items = new HashMap<>();

        if (args.containsKey("items")) {
            Object raw = args.get("items");
            if (raw instanceof Map) {
                Map<?, ?> map = (Map)raw;

                items.put(ItemStack.deserialize((Map<String, Object>) map.get("item")), ((Number)map.get("amount")).intValue());
            }
        }

        return new ESDrive(uuid, size, items);
    }
}
