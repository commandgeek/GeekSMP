package com.commandgeek.GeekSMP.managers;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PacketManager {

    List<Player> players = new ArrayList<>();
    public PacketManager(Player... players) {
        Collections.addAll(this.players, players);
    }

    public PacketManager() {
        players.addAll(Bukkit.getOnlinePlayers());
    }

    public void hideEntity(Entity entity) {
        sendPacket(new PacketPlayOutEntityDestroy(entity.getEntityId()));
    }

    public void animateEntity(Entity entity, int id) {
        sendPacket(new PacketPlayOutAnimation(((CraftEntity)entity).getHandle(), id));
    }

    public void addPlayer(Player entity) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, ((CraftPlayer)entity).getHandle());
        sendPacket(packet);
    }

    public void removePlayer(Player entity) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, ((CraftPlayer)entity).getHandle());
        sendPacket(packet);
    }

    @SuppressWarnings({"all"})
    public void sendPacket(Packet packet) {
        for (Player player : players) {
            ((CraftPlayer)player).getHandle().b.sendPacket(packet);
        }
    }
}
