package net.seanomik.energeticstorage.configs;

import net.seanomik.energeticstorage.EnergeticStorage;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", EnergeticStorage.getInstance());
    }

    @Override
    public void init() {
        getDriveMaxTypes();

        allowHopperInput();
    }

    public int getDriveMaxTypes() {
        reloadResource();

        return getOrSetDefault("driveMaxTypes", 128);
    }

    public boolean allowHopperInput() {
        reloadResource();

        return getOrSetDefault("allowHopperInput", true);
    }
}
