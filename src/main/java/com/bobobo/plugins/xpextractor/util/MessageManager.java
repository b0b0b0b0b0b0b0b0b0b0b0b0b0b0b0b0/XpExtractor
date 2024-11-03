package com.bobobo.plugins.xpextractor.util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
public class MessageManager {
    private FileConfiguration messages;
    private final JavaPlugin plugin;
    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }
    public void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    public void reloadMessages() {
        loadMessages();
    }
    public String getMessage(String key) {
        return messages.getString(key, "Message not found: " + key);
    }
}
