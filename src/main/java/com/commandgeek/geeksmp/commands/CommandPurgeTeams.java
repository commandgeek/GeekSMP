package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class CommandPurgeTeams implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.purgeteams") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                team.unregister();
            }
        }
        return true;
    }
}
