package com.arqologin;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHandler {
    private static ConfigHandler instance;
    private final ArqoLoginPlugin plugin;
    private FileConfiguration config;

    private ConfigHandler(ArqoLoginPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public static void init(ArqoLoginPlugin plugin) {
        if (instance == null) {
            instance = new ConfigHandler(plugin);
        }
    }

    public static ConfigHandler getInstance() {
        return instance;
    }

    public void load() {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public String getMessage(String path) {
        return config.getString("messages." + path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
}
