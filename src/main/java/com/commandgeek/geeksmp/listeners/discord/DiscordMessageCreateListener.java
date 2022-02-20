package com.commandgeek.geeksmp.listeners.discord;


import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.Setup;
import com.commandgeek.geeksmp.managers.*;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("ConstantConditions")
public class DiscordMessageCreateListener implements MessageCreateListener {
    public void onMessageCreate(MessageCreateEvent event) {

        // Ignore Bots
        if (event.getMessage().getAuthor().isBotUser()) return;

        // Run Discord Commands
        if (command(event.getMessage())) return;

        // Run SMP Chat Command
        if (smpChatCommand(event.getMessage())) return;

        // Check Linking
        if (DiscordManager.linkChannels.contains(event.getChannel())) {
            event.getMessage().delete();

            User user = DiscordManager.getUserFromMessage(event.getMessage());
            if (user == null) return;

            String code = event.getMessage().getContent();
            if (!LinkManager.linkingCodes.containsValue(code)) {
                DiscordManager.privateMessage(user, new MessageManager("link-invalid-code").string());
                return;
            }
            UUID uuid = LinkManager.getUUIDFromCode(code);
            if (uuid != null) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    DiscordManager.privateMessage(user, new MessageManager("link-invalid-player").string());
                    return;
                }

                LinkManager.unlink(user.getIdAsString());
                LinkManager.unlink(player.getUniqueId());
                LinkManager.link(user.getIdAsString(), uuid);
            }
        }
    }

    public static boolean command(Message message) {
        String[] args = message.getContent().split(" ");
        if (args[0].equalsIgnoreCase(Main.botPrefix + "send-info-message") && DiscordManager.server.hasPermission(DiscordManager.getUserFromMessage(message), PermissionType.ADMINISTRATOR)) {
            if (args.length == 2 && Main.info.contains(args[1])) {
                message.delete();
                DiscordManager.sendInfo(args[1], message.getChannel());
                return true;
            }
        }

        if (args[0].equalsIgnoreCase(Main.botPrefix + "mute") && DiscordManager.server.hasPermission(DiscordManager.getUserFromMessage(message), PermissionType.MANAGE_MESSAGES)) {
            if (message.getMentionedUsers().size() == 1) {
                User user = message.getMentionedUsers().get(0);
                TextChannel channel = message.getChannel();

                OfflinePlayer player = DiscordManager.getPlayerFromUser(user);
                if (player == null && (args.length == 2 || args.length == 3)) {
                    new MessageManager("discord-mute-fail")
                            .replace("%user%", user.getName())
                            .sendDiscord(channel);
                    DiscordManager.server.addRoleToUser(user, DiscordManager.mutedRole);
                    return true;
                }

                CommandSender sender = Bukkit.getConsoleSender();
                if (args.length == 2) {
                    MuteManager.mute(player.getName(), null, null, sender);
                    new MessageManager("discord-mute-permanent")
                            .replace("%user%", user.getMentionTag())
                            .replace("%player%", player.getName(), true)
                            .replace("%uuid%", player.getUniqueId().toString())
                            .sendDiscord(channel);
                    return true;
                }
                if (args.length == 3) {
                    if (NumberManager.stringIsDuration(args[2])) {
                        MuteManager.mute(player.getName(), args[2], null, sender);
                        new MessageManager("discord-mute-temporary")
                                .replace("%user%", user.getName())
                                .replace("%duration%", NumberManager.getTimeFrom(MuteManager.checkMuted(player.getUniqueId().toString())))
                                .replace("%player%", player.getName(), true)
                                .replace("%uuid%", player.getUniqueId().toString())
                                .sendDiscord(channel);
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("p")) {
                        MuteManager.mute(player.getName(), null, null, sender);
                        new MessageManager("discord-mute-permanent")
                                .replace("%user%", user.getMentionTag())
                                .replace("%player%", player.getName(), true)
                                .replace("%uuid%", player.getUniqueId().toString())
                                .sendDiscord(channel);
                        return true;
                    }
                    new MessageManager("discord-invalid-duration").sendDiscord(channel);
                    return true;
                }
                if (args.length == 4) {
                    StringBuilder reason = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }

                    if (NumberManager.stringIsDuration(args[2])) {
                        MuteManager.mute(args[1], args[2], reason.toString().trim(), sender);
                        new MessageManager("discord-mute-temporary")
                                .replace("%user%", user.getName())
                                .replace("%duration%", NumberManager.getTimeFrom(MuteManager.checkMuted(player.getUniqueId().toString())))
                                .replace("%player%", player.getName(), true)
                                .replace("%uuid%", player.getUniqueId().toString())
                                .sendDiscord(channel);
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("p")) {
                        MuteManager.mute(args[1], null, reason.toString().trim(), sender);
                        new MessageManager("discord-mute-permanent")
                                .replace("%user%", user.getMentionTag())
                                .replace("%player%", player.getName(), true)
                                .replace("%uuid%", player.getUniqueId().toString())
                                .sendDiscord(channel);
                        return true;
                    }
                    new MessageManager("invalid-duration").send(sender);
                    return true;
                }
            }
        }

        if (args[0].equalsIgnoreCase(Main.botPrefix + "unmute") && DiscordManager.server.hasPermission(DiscordManager.getUserFromMessage(message), PermissionType.MANAGE_MESSAGES)) {
            if (message.getMentionedUsers().size() == 1) {
                User user = message.getMentionedUsers().get(0);
                TextChannel channel = message.getChannel();

                OfflinePlayer player = DiscordManager.getPlayerFromUser(user);
                if (player == null && args.length == 2) {
                    new MessageManager("discord-unmute-fail")
                            .replace("%user%", user.getName())
                            .sendDiscord(channel);
                    DiscordManager.server.addRoleToUser(user, DiscordManager.mutedRole);
                    return true;
                }

                CommandSender sender = Bukkit.getConsoleSender();
                if (args.length == 2) {
                    MuteManager.unmute(player.getName(), sender);
                    new MessageManager("discord-unmute")
                            .replace("%user%", user.getMentionTag() )
                            .replace("%player%", player.getName(), true)
                            .replace("%uuid%", player.getUniqueId().toString())
                            .sendDiscord(channel);
                    return true;
                }
            }
        }

        if (args[0].equalsIgnoreCase(Main.botPrefix + "smp") && args.length == 1) {
            TextChannel channel = message.getChannel();
            new MessageManager("discord-command-smp").sendDiscord(channel);
            return true;
        }

        if (args[0].equalsIgnoreCase(Main.botPrefix + "invite") && args.length == 1) {
            TextChannel channel = message.getChannel();
            new MessageManager("discord-command-invite").sendDiscord(channel);
            return true;
        }

        if (args[0].equalsIgnoreCase(Main.botPrefix + "discord") && args.length == 1) {
            TextChannel channel = message.getChannel();
            new MessageManager("discord-command-discord").sendDiscord(channel);
            return true;
        }

        if (args[0].equalsIgnoreCase(Main.botPrefix + "update-linked-role") && args.length == 1 && DiscordManager.server.hasPermission(DiscordManager.getUserFromMessage(message), PermissionType.ADMINISTRATOR)) {
            try {
                new BukkitRunnable() {
                    final List<User> users = DiscordManager.linkedRole.getUsers().stream().toList();
                    final TextChannel channel = message.getChannel();
                    final Message sent = channel.sendMessage("**Scanning Members** - 0/" + users.size()).get();

                    int i = 0;

                    public void run() {
                        try {
                            User user = users.get(i);
                            if (!Main.linked.contains(user.getIdAsString())) {
                                user.removeRole(DiscordManager.linkedRole);
                            }
                            i++;
                            sent.edit("**Scanning Members** - " + i + "/" + users.size() + " `" + user.getDiscriminatedName() + "`");
                        } catch (IndexOutOfBoundsException ignored) {
                            sent.edit("**Scanning Members** - `Complete`");
                            cancel();
                        }
                    }
                }.runTaskTimer(Main.instance, 40, 40);
            } catch (InterruptedException | ExecutionException ignored) {
                return false;
            }
            return true;
        }

        if (args[0].equalsIgnoreCase(Main.botPrefix + "online") && args.length == 1) {
            Setup.updateSetupTimer();
            TextChannel channel = message.getChannel();
            StringBuilder result = new StringBuilder("**" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + " Online Players:** ```");

            if (Bukkit.getOnlinePlayers().size() > 0) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    result.append(online.getName()).append(", ");
                }
                result = new StringBuilder(result.toString().replaceAll(", $", ""));
            } else {
                result.append("No Players Online");
            }
            result.append("```");

            try {
                Message sent = channel.sendMessage(result.toString()).get();
                new BukkitRunnable() {
                    public void run() {
                        sent.delete();
                    }
                }.runTaskLater(Main.instance, 200);
            } catch (InterruptedException | ExecutionException ignored) {}
            message.delete();
            return true;
        }

        if (args[0].equalsIgnoreCase(Main.botPrefix + "reason") && DiscordManager.server.hasPermission(DiscordManager.getUserFromMessage(message), PermissionType.MANAGE_MESSAGES)) {
            if (message.getMentionedUsers().size() == 1) {
                User user = message.getMentionedUsers().get(0);
                TextChannel channel = message.getChannel();

                UUID uuid = LinkManager.getPlayerUUID(user.getIdAsString());
                if (uuid == null) {
                    new MessageManager("discord-reason-missing")
                            .replace("%user%", user.getDiscriminatedName())
                            .sendDiscord(channel);
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                long muteTime = MuteManager.checkMuted(target.getUniqueId().toString());
                long banTime = BanManager.checkBanned(target.getUniqueId().toString());

                if (!MuteManager.isMuted(target.getUniqueId()) && !BanManager.isBanned(target.getUniqueId())) {
                    new MessageManager("discord-reason-fail")
                            .replace("%user%", user.getDiscriminatedName())
                            .replace("%player%", target.getName(), true)
                            .replace("%uuid%", target.getUniqueId().toString())
                            .sendDiscord(channel);
                } else {
                    if (MuteManager.isMuted(target.getUniqueId())) {
                        if (muteTime == -1) {
                            new MessageManager("discord-reason-mute-permanent")
                                    .replace("%user%", user.getDiscriminatedName())
                                    .replace("%reason%", MuteManager.getReason(target.getUniqueId()))
                                    .replace("%player%", target.getName(), true)
                                    .replace("%uuid%", target.getUniqueId().toString())
                                    .sendDiscord(channel);
                        } else {
                            new MessageManager("discord-reason-mute-temporary")
                                    .replace("%user%", user.getDiscriminatedName())
                                    .replace("%duration%", NumberManager.getTimeFrom(muteTime))
                                    .replace("%reason%", MuteManager.getReason(target.getUniqueId()))
                                    .replace("%player%", target.getName(), true)
                                    .replace("%uuid%", target.getUniqueId().toString())
                                    .sendDiscord(channel);
                        }
                    }
                    if (BanManager.isBanned(target.getUniqueId())) {
                        if (banTime == -1) {
                            new MessageManager("discord-reason-ban-permanent")
                                    .replace("%user%", user.getDiscriminatedName())
                                    .replace("%reason%", MuteManager.getReason(target.getUniqueId()))
                                    .replace("%player%", target.getName(), true)
                                    .replace("%uuid%", target.getUniqueId().toString())
                                    .sendDiscord(channel);
                        } else {
                            new MessageManager("discord-reason-ban-temporary")
                                    .replace("%user%", user.getDiscriminatedName())
                                    .replace("%duration%", NumberManager.getTimeFrom(banTime))
                                    .replace("%reason%", MuteManager.getReason(target.getUniqueId()))
                                    .replace("%player%", target.getName(), true)
                                    .replace("%uuid%", target.getUniqueId().toString())
                                    .sendDiscord(channel);
                        }
                    }
                }
                return true;
            }
        }


        return false;
    }

    public static boolean smpChatCommand(Message message) {
        if (message.getChannel () != DiscordManager.smpChatChannel) return false;
        if (message.getAuthor().asUser().isEmpty()) return false;
        User user = message.getAuthor().asUser().get();

        if (DiscordManager.server.hasPermission(user, PermissionType.ADMINISTRATOR)) {
            try {
                Bukkit.getScheduler().callSyncMethod(Main.instance, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message.getContent())).get();
                message.addReaction("greencheck:884155019530752001");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}