package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class CommandTitle implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        new BukkitRunnable() {
            int timer = 11;

            @Override
            public void run() {
                if (timer == 1) cancel();
                timer--;
                for (Player online : Bukkit.getOnlinePlayers()) {
                    ChatColor color = ChatColor.GREEN;
                    if (timer == 3) color = ChatColor.YELLOW;
                    if (timer == 2) color = ChatColor.GOLD;
                    if (timer == 1) color = ChatColor.RED;
                    if (timer == 0) {
                        color = ChatColor.DARK_RED;
                        online.spawnParticle(Particle.EXPLOSION_HUGE, online.getLocation().add(0, 1, 0), 1);
                    }

                    online.sendTitle(color + String.valueOf(timer), "", 0, 20, 10);

                    Firework fw = (Firework) online.getWorld().spawnEntity(online.getLocation().add(0, 2, 0), EntityType.FIREWORK);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    fwm.setPower(2);
                    fwm.addEffect(FireworkEffect.builder().withColor(Color.FUCHSIA).withColor(Color.RED).build());
                    fw.setFireworkMeta(fwm);
                }
            }
        }.runTaskTimer(Main.instance, 0, 20);

        return true;
    }
}
