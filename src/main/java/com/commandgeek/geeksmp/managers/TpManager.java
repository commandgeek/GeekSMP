package com.commandgeek.geeksmp.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class TpManager {
    public static void teleport(Player sender, String[] args, boolean here) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            new MessageManager("errors.invalid-player")
                    .replace("%player%", args[0])
                    .send(sender);
        } else if (here) {
            target.teleport(sender);
        } else {
            sender.teleport(target);
        }
    }
}
