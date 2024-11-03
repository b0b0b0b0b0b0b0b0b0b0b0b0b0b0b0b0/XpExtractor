package com.bobobo.plugins.xpextractor.commands;
import com.bobobo.plugins.xpextractor.XpExtractor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class TabCompl implements TabCompleter {
    private final XpExtractor plugin;
    public TabCompl(XpExtractor plugin) {
        this.plugin = plugin;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission(plugin.getConfig().getString("permissions.use", "expbottle.use"))) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    int maxLevels = plugin.getConfig().getInt("max_tab_complete_levels", 10);
                    int levels = Math.min(player.getLevel(), maxLevels);
                    for (int i = 1; i <= levels; i++) {
                        completions.add(String.valueOf(i));
                    }
                }
            }
            if (sender.hasPermission(plugin.getConfig().getString("permissions.admin", "expbottle.admin"))) {
                completions.add("reload");
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
