package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.LinkManager;
import com.commandgeek.geeksmp.managers.MessageManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCode implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.code")) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        String code = LinkManager.generateCode(player.getUniqueId());
        if (LinkManager.getDiscordID(player.getUniqueId()) == null) {
            new MessageManager("linking.link.code.generate").replace("%code%", code).send(player);
        } else {
            new MessageManager("linking.link.code.already-linked").replace("%code%", code).send(player);
        }
        return true;
    }
}
