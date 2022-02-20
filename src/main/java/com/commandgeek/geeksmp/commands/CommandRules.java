package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.MessageManager;

import org.apache.commons.lang.WordUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandRules implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.rules")) {
            new MessageManager("no-permission").send(sender);
            return true;
        }

        ConfigurationSection section = Main.info.getConfigurationSection("rules");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (Main.info.isList("rules." + key)) {
                    String header = WordUtils.capitalizeFully(key.replace("-", " "));
                    new MessageManager("info-header").replace("%header%", header).send(sender);
                    for (String item : Main.info.getStringList("rules." + key)) {
                        item = item.replaceAll("\\*", "");
                        item = item.replaceAll("__", "");
                        item = item.replaceAll("~~", "");
                        new MessageManager("info-item").replace("%item%", item).send(sender);
                    }
                }
            }
        }
        return true;
    }
}
