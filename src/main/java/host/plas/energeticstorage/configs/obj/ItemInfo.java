package host.plas.energeticstorage.configs.obj;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter @Setter
public class ItemInfo {
    private Material material;
    private int customModelData;

    public ItemInfo(Material material, int customModelData) {
        this.material = material;
        this.customModelData = customModelData;
    }
}
