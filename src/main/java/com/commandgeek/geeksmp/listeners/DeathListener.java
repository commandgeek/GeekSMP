package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.Setup;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!TeamManager.isUndead(player) && !TeamManager.isRevived(player)) {
            Player killer = player.getKiller();
            if (killer != null && TeamManager.isUndead(killer)) {

                MorphManager.unmorph(killer,true);
                EntityManager.showPlayerForAll(killer);
                TeamManager.revive(killer);
                Setup.updatePlayerRole(killer);
                new MessageManager("revived-warning").send(killer);

                Bukkit.broadcastMessage(new MessageManager("revive").replace("%player%", killer.getName()).replace("%victim%", player.getName()).string());
                event.setDeathMessage(null);
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
                killer.playSound(killer.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
                return;
            }
        }

        if (TeamManager.isRevived(player)) {
            TeamManager.unrevive(player);
            MorphManager.unmorph(player,true);
            new MessageManager("smp-chat-death").replace("%message%", event.getDeathMessage()).sendDiscord(DiscordManager.smpChatChannel);
            Setup.updatePlayerRole(player);
            return;
        }

        if (TeamManager.isUndead(player)) {
            event.setDeathMessage(null);
            MorphManager.unmorph(player,true);
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 0.5, 0), 20, 0.2, 0.2, 0.2, 0.2);
        }

        if (event.getDeathMessage() != null && !event.getDeathMessage().isEmpty()) {
            new MessageManager("smp-chat-death")
                    .replace("%message%", event.getDeathMessage())
                    .sendDiscord(DiscordManager.smpChatChannel);
        }
        Setup.updatePlayerRole(player);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (TeamManager.isUndead(player)) {
            if (player.getBedSpawnLocation() == null)
                event.setRespawnLocation(ConfigManager.getDefaultWorldLocation(Main.config, "spawn"));
            Setup.join(player);
        }
    }
}
