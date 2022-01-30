package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
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
        int[] entityArray = { entity.getEntityId() };

        PacketContainer packet = Main.protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegers().write(0, 1);
        packet.getIntegerArrays().write(0, entityArray);

        sendPacket(packet);
    }

    public void animateEntity(Entity entity, int id) {
        PacketContainer packet = Main.protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
        packet.getIntegers().write(0, entity.getEntityId());
        packet.getIntegers().write(1, id);

        sendPacket(packet);
    }

    public void addPlayer(Player player) {
        PacketContainer packet = createPlayerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        sendPacket(packet);
    }

    public void removePlayer(Player player) {
        PacketContainer packet = createPlayerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        sendPacket(packet);
    }

    public PacketContainer createPlayerInfoPacket(Player player, EnumWrappers.PlayerInfoAction action) {
        WrappedGameProfile playerProfile = WrappedGameProfile.fromPlayer(player);
        WrappedChatComponent playerDisplayName = WrappedChatComponent.fromText(player.getDisplayName());
        PlayerInfoData playerData = new PlayerInfoData(playerProfile, player.getPing(), EnumWrappers.NativeGameMode.NOT_SET, playerDisplayName);
        List<PlayerInfoData> playerArray = List.of(playerData);

        PacketContainer packet = Main.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, action);
        packet.getIntegers().write(0, 1);
        packet.getPlayerInfoDataLists().write(0, playerArray);

        return packet;
    }

    @SuppressWarnings({"all"})
    public void sendPacket(PacketContainer packet) {
        for (Player player : players) {
            try {
                Main.protocolManager.sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
