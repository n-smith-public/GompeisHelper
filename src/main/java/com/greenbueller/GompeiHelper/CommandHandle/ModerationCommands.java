package com.greenbueller.GompeiHelper.CommandHandle;

import com.greenbueller.GompeiHelper.ConsoleColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.requests.restaction.pagination.BanPaginationAction;

import java.awt.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.greenbueller.GompeiHelper.CommandHandle.CommandHandler.*;
import static com.greenbueller.GompeiHelper.CommandHandle.MessageFilter.*;
import static com.greenbueller.GompeiHelper.GompeiHelper.playStartupSound;

public class ModerationCommands extends ListenerAdapter {
        private List<User> u = new ArrayList<>();
        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            if (event.getName().equals("timeout")) {
                EntitySelectMenu users = EntitySelectMenu.create("timeout-user", EntitySelectMenu.SelectTarget.USER).setRequiredRange(1,1).setPlaceholder("Select user").build();

                event.reply("Choose the user to timeout").addActionRow(users).setEphemeral(true).queue();
            }

            if (event.getName().equals("remove-timeout")) {
                EntitySelectMenu user = EntitySelectMenu.create("remove-timeout", EntitySelectMenu.SelectTarget.USER).setRequiredRange(1,1).setPlaceholder("Select user").build();
                event.reply("Choose the user to remove the timeout from.").addActionRow(user).setEphemeral(true).queue();
            }

            if (event.getName().equals("grant-role")) {
                EntitySelectMenu user = EntitySelectMenu.create("grant-role-user", EntitySelectMenu.SelectTarget.USER).setRequiredRange(1,1).setPlaceholder("Select user").build();
                EntitySelectMenu role = EntitySelectMenu.create("grant-role-role", EntitySelectMenu.SelectTarget.ROLE).setPlaceholder("Select role").build();
                event.reply("Choose the user to grant a role").addActionRow(user).setEphemeral(true).queue();
                event.getHook().sendMessage("Choose the role to grant.").addActionRow(role).setEphemeral(true).delay(1, TimeUnit.SECONDS).queue();
            }

            if (event.getName().equals("announce")) {
                String userInput = Objects.requireNonNull(event.getOption("input")).getAsString();
                boolean bypassFilter;
                boolean pingEveryone;
                MessageChannel announceChannel;
                try {
                    bypassFilter = event.getOption("filter-bypass").getAsBoolean();
                }
                catch (NullPointerException e) {
                    bypassFilter = false;
                }
                try {
                    pingEveryone = event.getOption("ping-everyone").getAsBoolean();
                }
                catch (NullPointerException e) {
                    pingEveryone = false;
                }
                try {
                    announceChannel = (MessageChannel) event.getOption("channel").getAsChannel();
                }
                catch (NullPointerException e) {
                    announceChannel = event.getChannel();
                }
                boolean sendable = false;
                EmbedBuilder eb = new EmbedBuilder();
                if (isInFilter(userInput)) {
                    if (bypassFilter) {
                        eb = createAnnouncement(userInput);
                        sendable = true;
                    }
                }
                else {
                    eb = createAnnouncement(userInput);
                    sendable = true;
                }
                if (sendable) {
                    playStartupSound();
                    event.reply("Announcement sent.").setEphemeral(true).queue();
                    if (pingEveryone) {
                        announceChannel.sendMessage("@everyone" + System.lineSeparator() + System.lineSeparator() + "New announcement!").addEmbeds(eb.build()).queue();
                    }
                    else {
                        announceChannel.sendMessage("New announcement!").addEmbeds(eb.build()).queue();
                    }
                }
                else {
                    event.reply("Message not sent.").setEphemeral(true).queue();
                }
            }

            if (event.getName().equals("slowmode")) {
                Channel channel;
                try {
                    channel = event.getOption("channel").getAsChannel();
                }
                catch (NullPointerException e) {
                    channel = event.getChannel();
                }

                int time = event.getOption("time").getAsInt();
                if (time < 0) {
                    event.reply("Error: Slowmode cannot be negative.").setEphemeral(true).queue();
                }
                else if (time > 21600) {
                    event.reply("Error: Slowmode cannot be greater than 21,600.").setEphemeral(true).queue();
                }
                else {
                    String id = channel.getId();
                    if (event.getGuild().getTextChannelById(id).getSlowmode() != time) {
                        try {
                            TextChannel tc = event.getGuild().getTextChannelById(id);
                            tc.getManager().setSlowmode(time).queue();
                            event.reply("Slowmode set to " + time + " seconds.").setEphemeral(true).queue();
                        } catch (NullPointerException e) {
                            System.out.println("Attempt to set slowmode in " + channel.getName() + " in server " + event.getGuild().getName() + " was unsuccessful.");
                            event.reply("Error in setting slowmode.").setEphemeral(true).queue();
                        }
                    }
                }
            }

            if (event.getName().equals("ban")) {
                int backlog;
                User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
                Member u = Objects.requireNonNull(event.getOption("user")).getAsMember();
                String reason;
                String tu;
                int delay = 15;
                try {
                    backlog = event.getOption("backlog").getAsInt();
                }
                catch (NullPointerException e) {
                    backlog = 0;
                }
                try {
                    tu = event.getOption("backlog-time").getAsString().toUpperCase();
                }
                catch (NullPointerException e) {
                    tu = "SECONDS";
                }
                try {
                    reason = event.getOption("reason").getAsString();
                }
                catch (NullPointerException e) {
                    reason = "";
                }
                String banDM = "You have been banned from " + event.getGuild().getName();
                if (event.getMember().canInteract(u)) {
                    if (event.getGuild().getMemberById("1224566113132744814").canInteract(u)) {
                        if (!reason.isEmpty()) {
                            banDM += " for " + reason;
                        }
                        if (tu.equals("SECONDS") || tu.equals("MINUTES") || tu.equals("HOURS") || tu.equals("DAYS")) {
                            sendDM(banDM, user);
                            if (!reason.isEmpty()) {
                                event.getGuild().ban(user, backlog, TimeUnit.valueOf(tu)).reason(reason).queue();
                                event.reply("User " + user.getName() + " has successfully been banned.").setEphemeral(true).delay(delay, TimeUnit.SECONDS).queue();
                            } else {
                                event.getGuild().ban(user, backlog, TimeUnit.valueOf(tu)).queue();
                                event.reply("User " + user.getName() + " has successfully been banned.").setEphemeral(true).delay(delay, TimeUnit.SECONDS).queue();
                            }
                        } else {
                            if (!reason.isEmpty()) {
                                event.getGuild().ban(user, 0, TimeUnit.SECONDS).reason(reason).queue();
                                event.reply("User " + user.getName() + " has successfully been banned.").setEphemeral(true).delay(delay, TimeUnit.SECONDS).queue();
                            } else {
                                event.getGuild().ban(user, 0, TimeUnit.SECONDS).queue();
                                event.reply("User " + user.getName() + " has successfully been banned.").setEphemeral(true).delay(delay, TimeUnit.SECONDS).queue();
                            }
                        }
                    }
                    else {
                        event.reply("I lack permission to ban " + user.getName()).setEphemeral(true).queue();
                    }
                }
                else {
                    event.reply("You lack permissions to ban " + user.getName()).setEphemeral(true).queue();
                }
            }

            if (event.getName().equals("unban")) {
                Guild g = event.getGuild();
                String idString = event.getOption("id").getAsString();
                long id = Long.parseLong(idString);
                assert g != null;
                BanPaginationAction bannedUsers = g.retrieveBanList();

                if (bannedUsers.stream().noneMatch(b -> b.getUser().equals(User.fromId(id)))) {
                    event.reply("There are no banned users or the user you are trying to unban is not currently banned.")
                            .setEphemeral(true).queue();
                }
                else {
                    g.retrieveBanList().queue(
                            (success) -> {
                                g.unban(User.fromId(id)).queue();
                                event.reply("User was successfully unbanned.").setEphemeral(true).queue();
                            },
                            (failure) -> {
                                event.reply("User was not currently banned.").setEphemeral(true).queue();
                            }
                    );
                }
            }

            if (event.getName().equals("softban")) {
                Guild g = event.getGuild();
                User u = Objects.requireNonNull(event.getOption("user")).getAsUser();
                Member u2 = event.getOption("user").getAsMember();
                if (event.getMember().canInteract(u2)) {
                    if (g.getMemberById("1224566113132744814").canInteract(u2)) {
                        sendDM("You have been banned from " + g.getName(), u);
                        g.ban(u, 1, TimeUnit.DAYS).reason("Softban initiated").delay(15, TimeUnit.SECONDS).queue();
                        event.reply("User " + u.getName() + " successfully softbanned.").setEphemeral(true).queue();
                        try {
                            TimeUnit.MINUTES.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        g.unban(u).queue();
                    }
                    else {
                        event.reply("I was not able to softban " + u.getName()).setEphemeral(true).queue();
                    }
                }
                else {
                    event.reply("You lack permissions to softban " + u.getName()).setEphemeral(true).queue();
                }
            }

            if (event.getName().equals("kick")) {
                Guild g = event.getGuild();
                User u = Objects.requireNonNull(event.getOption("user")).getAsUser();
                Member u2 = event.getOption("user").getAsMember();
                String reason;
                try {
                    reason = event.getOption("reason").getAsString();
                }
                catch (NullPointerException e) {
                    reason = "";
                }

                if (event.getMember().canInteract(u2)) {
                    if (g.getMemberById("1224566113132744814").canInteract(u2)) {
                        if (reason.isEmpty()) {
                            g.kick(u2).reason(reason).queue();
                        }
                        else {
                            g.kick(u2).queue();
                        }
                        event.reply("User " + u.getName() + " has successfully been kicked.").setEphemeral(true).queue();
                    }
                    else {
                        event.reply("I do not have permission to kick " + u.getName()).setEphemeral(true).queue();
                    }
                }
                else {
                    event.reply("You lack permission to kick " + u.getName()).setEphemeral(true).queue();
                }
            }

            if (event.getName().equals("nick")) {
                String nick = "";
                Guild g = event.getGuild();
                User u = Objects.requireNonNull(event.getOption("user")).getAsUser();
                Member u2 = event.getOption("user").getAsMember();
                try {
                    nick = event.getOption("nickname").getAsString();
                }
                catch (NullPointerException e) {
                    nick = "";
                }
                if (event.getMember().canInteract(u2)) {
                    if (g.getMemberById("1224566113132744814").canInteract(u2)) {
                        if (nick.isEmpty() || nick.equalsIgnoreCase("clear") || nick.equals(" ")) {
                            u2.modifyNickname("").queue();
                            event.reply("User " + u.getName() + " has had their nickname cleared.").setEphemeral(true).queue();
                        }
                        else {
                            u2.modifyNickname(nick).queue();
                            event.reply("User " + u.getName() + " has successfully been nicked.").setEphemeral(true).queue();
                        }
                    }
                    else {
                        event.reply("I do not have permission to nick " + u.getName()).setEphemeral(true).queue();
                    }
                }
                else {
                    event.reply("You do not have permission to nick " + u.getName()).setEphemeral(true).queue();
                }
            }

            if (event.getName().equals("lockdown")) {
                if (event.isFromGuild()) {
                    Guild g = event.getGuild();
                    Role everyone = g.getPublicRole();
                    ArrayList<GuildChannel> channels = new ArrayList<>(event.getGuild().getChannels());
                    for (GuildChannel channel : channels) {
                        channel.getPermissionContainer().upsertPermissionOverride(everyone).deny(Permission.MESSAGE_SEND).queue();
                    }
                    event.reply("Server is now on lockdown mode.").setEphemeral(true).queue();
                }
            }
        }

        @Override
        public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
            if (event.getComponentId().equals("timeout-user")) {
                List<User> user = event.getMentions().getUsers();
                User focus = user.get(0);
                try {
                    Objects.requireNonNull(event.getGuild()).timeoutFor(focus, 1, TimeUnit.DAYS).queue();
                    event.reply("User " + focus.getAsMention() + " was successfully timed out for 1 day.").setEphemeral(true).queue();
                }
                catch (Exception e) {
                    event.reply("An exception occurred when trying to time out " + focus.getAsMention()).setEphemeral(true).queue();
                    System.out.println(ConsoleColors.RED + "\n[Warning]: User " + ConsoleColors.RED_BOLD + focus.getName() + ConsoleColors.RED + " was not timed out properly." + ConsoleColors.RESET);
                }
            }

            if (event.getComponentId().equals("remove-timeout")) {
                List<User> user = event.getMentions().getUsers();
                List<Member> fo = event.getMentions().getMembers();
                User focus = user.get(0);
                try {
                    if (fo.get(0).isTimedOut()) {
                        Objects.requireNonNull(event.getGuild()).removeTimeout(focus).queue();
                        event.reply("User " + focus.getAsMention() + " is no longer timed out.").setEphemeral(true).queue();
                    }
                    else {
                        event.reply("User was not timed out.").setEphemeral(true).queue();
                    }
                }
                catch (Exception e) {
                    event.reply("An exception occurred when trying to remove the timeout of " + focus.getAsMention()).setEphemeral(true).queue();
                    System.out.println(ConsoleColors.RED + "\n[Warning]: User " + ConsoleColors.RED_BOLD + focus.getName() + ConsoleColors.RED + " was not timed out properly. They may not be timed out, the user may be lacking permissions, or the bot may be lacking permissions." + ConsoleColors.RESET);
                }
            }

            if (event.getComponentId().equals("grant-role-user")) {
                u = event.getMentions().getUsers();
                event.reply ("User " + u.get(0).getName() + " has been selected.").setEphemeral(true).queue();
            }

            if (event.getComponentId().equals("grant-role-role")) {
                List<Role> roles = event.getMentions().getRoles();
                Guild guild = event.getGuild();
                try {
                    if (!roles.isEmpty() && !u.isEmpty()) {
                        guild.addRoleToMember(u.get(0), roles.get(0)).queue();

                        event.reply("User granted role").setEphemeral(true).queue();
                    }
                    else {
                        System.out.println("Role list empty or user list empty.");
                        event.reply("User not granted role. See console.").setEphemeral(true).queue();
                    }
                }
                catch (Exception e) {
                    event.reply("User not granted role. See console.").setEphemeral(true).queue();
                    System.out.println(ConsoleColors.RED + "\n[Warning]: User " + ConsoleColors.RED_BOLD + u.get(0).getName() + ConsoleColors.RED + " was not granted roles properly." + ConsoleColors.RESET);
                }

                u = null;
            }
        }

        public EmbedBuilder createAnnouncement(String userInput) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Announcement");
            eb.setColor(Color.WHITE);
            eb.setDescription(userInput);
            return eb;
        }

        public void sendDM(String content, User user) {
            try {
                user.openPrivateChannel().flatMap(channel ->
                        channel.sendMessage(content)).complete();
            }
            catch (Exception e) {
                System.out.println("An error occurred sending message to " + user.getName());
            }
        }
}
