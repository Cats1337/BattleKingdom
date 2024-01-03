package io.github.cats1337.battlekingdom.utils;

import io.github.cats1337.battlekingdom.BattleKingdom;

public class ConfigManager {

    private BattleKingdom plugin;

    public ConfigManager(BattleKingdom plugin) {
        this.plugin = plugin;

        // Load and initialize configuration on plugin startup
        loadConfig();
    }

    private void loadConfig() {
        // Implement logic to load configuration from config.yml
        // Example: plugin.getConfig().getString("key");
    }

    // Add other configuration-related methods as needed
    public void setConfig(String key, Object value) {
        // Implement logic to set configuration values
        // Example: plugin.getConfig().set(key, value);
    }

    public Object getConfig(String key) {
        // Implement logic to get configuration values
        // Example: plugin.getConfig().get(key);
        return null;
    }

    public void saveConfig() {
        // Implement logic to save configuration to config.yml
        // Example: plugin.saveConfig();
    }

    public void reloadConfig() {
        // Implement logic to reload configuration from config.yml
        // Example: plugin.reloadConfig();
    }

    public void saveDefaultConfig() {
        // Implement logic to save default configuration to config.yml
        // Example: plugin.saveDefaultConfig();
    }

    // get spawnpoint for team
    // set spawnpoint for team

}
