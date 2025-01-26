package host.plas.energeticstorage.configs;

import host.plas.energeticstorage.EnergeticStorage;
import host.plas.energeticstorage.configs.obj.ItemInfo;
import org.bukkit.Material;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", EnergeticStorage.getInstance());
    }

    @Override
    public void init() {
        getDriveMaxTypes();

        allowHopperInput();

        getItemInfo1k();
        getItemInfo4k();
        getItemInfo16k();
        getItemInfo64k();
    }

    public int getDriveMaxTypes() {
        reloadResource();

        return getOrSetDefault("drives.max-types", 128);
    }

    public boolean allowHopperInput() {
        reloadResource();

        return getOrSetDefault("hoppers.allow-input", true);
    }

    public ItemInfo getItemInfo(String amount) {
        reloadResource();

        String materialString = getOrSetDefault("items." + amount + ".material", "PAPER");
        int customModelData = getOrSetDefault("items." + amount + ".custom-model-data", 0);

        try {
            Material material = Material.valueOf(materialString);

            return new ItemInfo(material, customModelData);
        } catch (Throwable e) {
            EnergeticStorage.getInstance().logWarning("Invalid material in config.yml: " + materialString, e);

            Material material = Material.PAPER;

            return new ItemInfo(material, customModelData);
        }
    }

    public ItemInfo getItemInfo1k() {
        return getItemInfo("1k");
    }

    public ItemInfo getItemInfo4k() {
        return getItemInfo("4k");
    }

    public ItemInfo getItemInfo16k() {
        return getItemInfo("16k");
    }

    public ItemInfo getItemInfo64k() {
        return getItemInfo("64k");
    }

    public void onReload() {
        init();
    }
}
