package com.commandgeek.geeksmp.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class TpManager {
    public static boolean teleport(Player sender, String[] args) {
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                new MessageManager("errors.invalid-player")
                        .replace("%player%", args[0])
                        .send(sender);
                return false;
            }
            if (target != sender) {
                sender.teleport(target);
            } else {
                target.teleport(sender);
            }
            return true;
        }
        return false;
    }
}
