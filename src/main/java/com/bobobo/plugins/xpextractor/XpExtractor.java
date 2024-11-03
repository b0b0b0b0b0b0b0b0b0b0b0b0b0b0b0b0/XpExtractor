package com.bobobo.plugins.xpextractor;
import com.bobobo.plugins.xpextractor.commands.Command;
import com.bobobo.plugins.xpextractor.commands.TabCompl;
import com.bobobo.plugins.xpextractor.util.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;
public final class XpExtractor extends JavaPlugin {
    private MessageManager messageManager;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        messageManager = new MessageManager(this);
        getCommand("expbottle").setExecutor(new Command(this));
        getCommand("expbottle").setTabCompleter(new TabCompl(this));
    }
    public MessageManager getMessageManager() {
        return messageManager;
    }
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        messageManager.reloadMessages();
    }
}
