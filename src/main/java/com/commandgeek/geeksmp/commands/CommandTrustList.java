package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CommandTrustList implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.trustlist")) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        List<String> trusted = Main.trusted.getStringList(player.getUniqueId().toString());
        new MessageManager("trusting.trust.list.header").send(player);
        if (trusted.size() == 0) {
            new MessageManager("trusting.trust.list.empty").send(player);
        } else {
            for (String uuid : trusted) {
                new MessageManager("trusting.trust.list.item")
                        .replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName())
                        .send(player);
            }
        }
        return true;
    }
}
