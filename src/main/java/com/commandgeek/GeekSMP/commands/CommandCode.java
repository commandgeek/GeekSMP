package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.LinkManager;
import com.commandgeek.GeekSMP.managers.MessageManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCode implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.code")) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        String code = LinkManager.generateCode(player.getUniqueId());
        if (LinkManager.getDiscordID(player.getUniqueId()) == null) {
            new MessageManager("link-generate-code").replace("%code%", code).send(player);
        } else {
            new MessageManager("link-generate-code-already-linked").replace("%code%", code).send(player);
        }
        return true;
    }
}
