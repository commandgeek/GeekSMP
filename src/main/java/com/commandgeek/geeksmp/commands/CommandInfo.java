package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.MessageManager;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandInfo implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.info")) {
            new MessageManager("errors.no-permission").send(sender);
            return true;
        }

        ConfigurationSection section = Main.info.getConfigurationSection("info");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (Main.info.isList("info." + key)) {
                    String header = WordUtils.capitalizeFully(key.replace("-", " "));
                    new MessageManager("information.info.header").replace("%header%", header).send(sender);
                    for (String item : Main.info.getStringList("info." + key)) {
                        item = item.replaceAll("\\*", "");
                        item = item.replaceAll("__", "");
                        item = item.replaceAll("~~", "");
                        new MessageManager("information.info.item").replace("%item%", item).send(sender);
                    }
                }
            }
        }
        return true;
    }
}
