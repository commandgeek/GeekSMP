package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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
        PacketContainer packet = Main.protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists().write(0, List.of(entity.getEntityId()));

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
        EnumWrappers.NativeGameMode playerGameMode = EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode());
        WrappedChatComponent playerDisplayName;

        Team team = TeamManager.getPlayerTeam(player);
        if (team != null) {
            String teamName = team.getName().replaceAll("^[0-9]+_", "");
            String color = Main.config.getString("groups." + teamName + ".color");

            playerDisplayName = WrappedChatComponent.fromText(ChatColor.valueOf(color) + player.getName());
        } else {
            playerDisplayName = WrappedChatComponent.fromText(player.getDisplayName());
        }

        PlayerInfoData playerData = new PlayerInfoData(playerProfile, player.getPing(), playerGameMode, playerDisplayName);
        List<PlayerInfoData> playerArray = new ArrayList<>();
        playerArray.add(playerData);

        PacketContainer packet = Main.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, action);
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
