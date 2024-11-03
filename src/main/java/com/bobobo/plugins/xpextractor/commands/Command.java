package com.bobobo.plugins.xpextractor.commands;
import com.bobobo.plugins.xpextractor.XpExtractor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
public class Command implements CommandExecutor {
    private final XpExtractor plugin;
    public Command(XpExtractor plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(plugin.getConfig().getString("permissions.admin", "expbottle.admin"))) {
                sender.sendMessage(colorize(plugin.getMessageManager().getMessage("no_permission")));
                return true;
            }
            plugin.reloadConfig();
            plugin.getMessageManager().reloadMessages();
            sender.sendMessage(colorize(plugin.getMessageManager().getMessage("reload_success")));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize(plugin.getMessageManager().getMessage("not_a_player")));
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission(plugin.getConfig().getString("permissions.use", "expbottle.use"))) {
            player.sendMessage(colorize(plugin.getMessageManager().getMessage("no_permission")));
            return true;
        }
        boolean allowConvertAll = plugin.getConfig().getBoolean("allow_convert_all", true);
        int expToConvert;
        if (args.length > 0) {
            try {
                int levelsToConvert = Integer.parseInt(args[0]);
                if (levelsToConvert <= 0 || levelsToConvert > player.getLevel()) {
                    player.sendMessage(colorize(plugin.getMessageManager().getMessage("not_enough_levels")));
                    return true;
                }
                expToConvert = getExperienceAtLevel(player.getLevel()) - getExperienceAtLevel(player.getLevel() - levelsToConvert);
            } catch (NumberFormatException e) {
                player.sendMessage(colorize(plugin.getMessageManager().getMessage("invalid_number")));
                return true;
            }
        } else {
            if (!allowConvertAll) {
                player.sendMessage(colorize(plugin.getMessageManager().getMessage("must_specify_levels")));
                return true;
            }
            expToConvert = getTotalExperience(player);
        }
        if (expToConvert <= 0) {
            player.sendMessage(colorize(plugin.getMessageManager().getMessage("not_enough_exp")));
            return true;
        }
        double conversionRate = plugin.getConfig().getDouble("conversion_rate", 1.0);
        double expPerBottle = plugin.getConfig().getDouble("exp_per_bottle", 7.0);
        int minBottles = plugin.getConfig().getInt("min_bottles", 1);
        int maxBottles = plugin.getConfig().getInt("max_bottles", 64);
        String bottleMaterial = plugin.getConfig().getString("bottle_material", "EXP_BOTTLE").toUpperCase();

        Material bottleItem;
        try {
            bottleItem = Material.valueOf(bottleMaterial);
        } catch (IllegalArgumentException e) {
            bottleItem = Material.EXP_BOTTLE;
        }
        int bottlesToGive = (int) Math.floor((expToConvert / expPerBottle) * conversionRate);
        if (bottlesToGive < minBottles) {
            bottlesToGive = minBottles;
        } else if (bottlesToGive > maxBottles) {
            bottlesToGive = maxBottles;
        }
        if (bottlesToGive <= 0) {
            player.sendMessage(colorize(plugin.getMessageManager().getMessage("not_enough_exp")));
            return true;
        }
        expToConvert = (int) Math.ceil((bottlesToGive / conversionRate) * expPerBottle);
        int playerTotalExp = getTotalExperience(player);
        if (expToConvert > playerTotalExp) {
            expToConvert = playerTotalExp;
            bottlesToGive = (int) Math.floor((playerTotalExp / expPerBottle) * conversionRate);
            if (bottlesToGive < minBottles) {
                player.sendMessage(colorize(plugin.getMessageManager().getMessage("not_enough_exp")));
                return true;
            }
        }
        removeExperience(player, expToConvert);
        int maxStackSize = bottleItem.getMaxStackSize();
        int fullStacks = bottlesToGive / maxStackSize;
        int remainder = bottlesToGive % maxStackSize;
        for (int i = 0; i < fullStacks; i++) {
            ItemStack stack = new ItemStack(bottleItem, maxStackSize);
            player.getInventory().addItem(stack);
        }
        if (remainder > 0) {
            ItemStack stack = new ItemStack(bottleItem, remainder);
            player.getInventory().addItem(stack);
        }
        String successMessage = plugin.getMessageManager().getMessage("convert_success")
                .replace("%exp%", String.valueOf(expToConvert))
                .replace("%bottles%", String.valueOf(bottlesToGive));
        player.sendMessage(colorize(successMessage));
        return true;
    }
    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    private int getTotalExperience(Player player) {
        int level = player.getLevel();
        int exp = Math.round(player.getExp() * player.getExpToLevel());
        return getExperienceAtLevel(level) + exp;
    }
    private int getExperienceAtLevel(int level) {
        if (level <= 0) return 0;
        if (level <= 16) {
            return level * level + 6 * level;
        } else if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }
    private void removeExperience(Player player, int exp) {
        int currentExp = getTotalExperience(player);
        player.setExp(0);
        player.setLevel(0);
        int newExp = currentExp - exp;
        if (newExp > 0) {
            player.giveExp(newExp);
        }
    }
}
