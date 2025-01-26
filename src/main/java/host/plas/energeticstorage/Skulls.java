package host.plas.energeticstorage;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

public enum Skulls {

    LeftGreenArrow("LeftGreenArrow", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWExZWYzOThhMTdmMWFmNzQ3NzAxNDUxN2Y3ZjE0MWQ4ODZkZjQxYTMyYzczOGNjOGE4M2ZiNTAyOTdiZDkyMSJ9fX0=", "b683cce5-08d5-539e-a101-98766c6ccca9"),
    UpGreenArrow("UpGreenArrow", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlMzZmY2IxZTVmNmIzNjUxN2ZiYmViOWNiZjRiMGMwNWMzMGQ4YmRiNTE1NDgyNGU2MGU2ZDU1MGY1MjhlOSJ9fX0=", "54a1f8d9-8545-5f89-8b87-7ff911f593d5"),
    Computer("Computer", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQwN2ViMjBhMjdlZDdiYzc2YTRlMmNlMGI0OTg4YzY1Y2E2NmQwZTU2Zjk3MWRjNDE5YmIzMWUwZmRhMGYzNiJ9fX0=", "ca8fef39-69c1-6a61-7ff4-bfe17d7f55a9");

    private ItemStack item;
    private String name;
    private String textures;
    private String uuid;

    Skulls(String name, String textures, String uuid) {
        this.textures = textures;
        this.name = name;
        this.uuid = uuid;
        item = createSkull();
    }

    private static ItemStack createSkull(String textures) {
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        head.editMeta(SkullMeta.class, skullMeta -> {
            final UUID uuid = UUID.randomUUID();
            final PlayerProfile playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
            playerProfile.setProperty(new ProfileProperty("textures", textures));

            skullMeta.setPlayerProfile(playerProfile);
        });
        return head;
    }

    private ItemStack createSkull() {
        return createSkull(textures);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public String getName() {
        return name;
    }

    public String getTextures() { return textures; }
}
