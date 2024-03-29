package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CommandPet implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }
        if (!MorphManager.pets()) {
            new MessageManager("pets.disabled").send(player);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.pet") && (TeamManager.isUndead(player) || TeamManager.isRevived(player))) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<OfflinePlayer> pets = new ArrayList<>();
                for (String key : Main.pets.getKeys(false)) {
                    List<String> owners = Main.pets.getStringList(key);
                    if (owners.contains(player.getUniqueId().toString())) {
                        pets.add(Bukkit.getOfflinePlayer(UUID.fromString(key)));
                    }
                }
                new MessageManager("pets.pet.list.header").send(player);
                if (pets.size() == 0) {
                    new MessageManager("pets.pet.list.empty").send(player);
                } else {
                    for (OfflinePlayer pet : pets) {
                        new MessageManager("pets.pet.list.item")
                                .replace("%player%", pet.getName())
                                .send(player);
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("clear")) {
                for (String key : Main.pets.getKeys(false)) {
                    List<String> owners = Main.pets.getStringList(key);
                    if (owners.contains(player.getUniqueId().toString())) {
                        Main.pets.set(key, null);
                    }
                }
                ConfigManager.saveData("pets.yml", Main.pets);
                new MessageManager("pets.pet.clear").send(player);
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                OfflinePlayer target = EntityManager.getOfflinePlayer(args[1]);
                if (target == null) {
                    new MessageManager("errors.invalid-player")
                            .replace("%player%", args[1])
                            .send(player);
                    return true;
                }
                MorphManager.pet(target, player);
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                OfflinePlayer target = EntityManager.getOfflinePlayer(args[1]);
                if (target == null) {
                    new MessageManager("errors.invalid-player")
                            .replace("%player%", args[1])
                            .send(player);
                    return true;
                }
                MorphManager.unpet(target, player);
                return true;
            }
        }

        new MessageManager("errors.invalid-arguments").send(player);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return results;
        }

        if (args.length == 1) {
            suggestions.add("add");
            suggestions.add("remove");
            suggestions.add("list");
            suggestions.add("clear");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (args[1].length() == 0) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (!MorphManager.isPettedBy(online, player)) {
                            suggestions.add(online.getName());
                        }
                    }
                } else {
                    for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
                        if (!MorphManager.isPettedBy(offline, player)) {
                            suggestions.add(offline.getName());
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                for (String key : Main.pets.getKeys(false)) {
                    List<String> owners = Main.pets.getStringList(key);
                    if (owners.contains(player.getUniqueId().toString())) {
                        suggestions.add(Bukkit.getOfflinePlayer(UUID.fromString(key)).getName());
                    }
                }
            }
        }

        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(suggestion);
            }
        }
        return results;
    }
}
