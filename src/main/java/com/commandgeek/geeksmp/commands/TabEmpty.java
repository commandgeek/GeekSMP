package com.commandgeek.geeksmp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabEmpty implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return new ArrayList<>();
    }
}
