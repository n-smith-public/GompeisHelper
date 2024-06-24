package com.greenbueller.GompeiHelper.CommandHandle;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandHandler extends ListenerAdapter {
    private static ArrayList<CommandData> commandListPrim = new ArrayList<>();
    private static final ArrayList<String> descriptionList = new ArrayList<>();
    private static ArrayList<Permission> permissionList = new ArrayList<>();
    private static ArrayList<CommandData> commandList = new ArrayList<>();
    private static final Emoji moderation = Emoji.fromUnicode("\uD83D\uDEE1\uFE0F");
    private static final Emoji fun = Emoji.fromUnicode("\uD83D\uDE42");
    private static final Emoji utility = Emoji.fromUnicode("\uD83D\uDEE0\uFE0F");
    private static final Emoji admin = Emoji.fromUnicode("\uD83D\uDC51");

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        ArrayList<CommandData> commandDataList = new ArrayList<>();
        // Permissions
        addPermissions(Permission.MODERATE_MEMBERS); // 0
        addPermissions(Permission.MANAGE_ROLES);     // 1
        addPermissions(Permission.MESSAGE_MANAGE);   // 2
        addPermissions(Permission.MANAGE_SERVER);    // 3
        addPermissions(Permission.MANAGE_CHANNEL);   // 4
        addPermissions(Permission.BAN_MEMBERS);      // 5
        addPermissions(Permission.ADMINISTRATOR);    // 6

        // Descriptions
        addDescriptions("Times out the specified user.");
        addDescriptions("Removes the timeout from the specified user.");
        addDescriptions("Grants specified roles to a specified user.");
        addDescriptions("Gets the current message filter");
        addDescriptions("Adds a new word to the message filter");
        addDescriptions("Removes a word from the message filter.");
        addDescriptions("Sends an announcement to the server.");
        addDescriptions("Bans a specified user.");
        addDescriptions("Unbans a specified user.");
        addDescriptions("Bans then unbans a user to clear their messages.");
        addDescriptions("Kicks a specified user.");
        addDescriptions("Engages server lockdown.");
        addDescriptions("Adds or clears the nickname of a specified user.");
        addDescriptions("Sets the slowmode of a specified channel." + System.lineSeparator() + System.lineSeparator() + getFun().getFormatted() + " **Fun Commands**" +  System.lineSeparator());

        addDescriptions("Roll some dice.");
        addDescriptions("Play Rock, Paper, Scissors!");
        addDescriptions("Rolls a magic 8-ball with your question.");
        addDescriptions("Gets a users avatar.");
        addDescriptions("Adds *emphasis*.");
        addDescriptions("Adds a compliment.");
        addDescriptions("Gives you a compliment :)");
        addDescriptions("Pop!"  + System.lineSeparator() + System.lineSeparator() + getUtility().getFormatted() +  " **Utility Commands**" + System.lineSeparator());

        addDescriptions("Gets a list of every role on the server.");
        addDescriptions("Gets a list of roles on the server.");
        addDescriptions("Gets information about a specific role.");
        addDescriptions("Gets a list of every supported command.");
        addDescriptions("Gets the runtime of the bot" + System.lineSeparator() + System.lineSeparator() + getAdmin().getFormatted() +  " **Admin Commands**" + System.lineSeparator());

        addDescriptions("Reloads every command");
        addDescriptions("Forces a specified command to reload");

        // Moderation

        commandDataList.add(Commands.slash("timeout", descriptionList.get(0)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(0))));
        commandDataList.add(Commands.slash("remove-timeout", descriptionList.get(1)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(0))));
        commandDataList.add(Commands.slash("grant-role", descriptionList.get(2)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(0), getPermissionList().get(1))));
        commandDataList.add(Commands.slash("filter", descriptionList.get(3)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(2), getPermissionList().get(1))));
        commandDataList.add(Commands.slash("add-filter", descriptionList.get(4)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(2), getPermissionList().get(1))).addOption(OptionType.STRING, "word", "The word to add", true));
        commandDataList.add(Commands.slash("remove-filter", descriptionList.get(5)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(2), getPermissionList().get(1))).addOption(OptionType.STRING, "word", "The word to remove", true));
        commandDataList.add(Commands.slash("announce", descriptionList.get(6)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(3))).addOption(OptionType.STRING, "input", "The message to announce", true).addOption(OptionType.BOOLEAN, "filter-bypass", "Do you want to bypass the filter? Defaults to false", false).addOption(OptionType.BOOLEAN, "ping-everyone", "Do you want to ping everyone?", false).addOption(OptionType.CHANNEL, "channel", "The channel to send the message to. Defaults to current channel", false));
        commandDataList.add(Commands.slash("ban", descriptionList.get(7)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(5))).addOption(OptionType.USER, "user", "The user to ban", true).addOption(OptionType.INTEGER, "backlog", "The duration to ban a user, as an integer").addOption(OptionType.STRING, "backlog-time", "How long do you want to ban for?", false).addOption(OptionType.STRING, "reason", "The reason for banning"));
        commandDataList.add(Commands.slash("unban", descriptionList.get(8)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(5))).addOption(OptionType.STRING, "id", "The ID of the user to unban", true));
        commandDataList.add(Commands.slash("softban", descriptionList.get(9)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(5))).addOption(OptionType.USER, "user", "The user to softban", true));
        commandDataList.add(Commands.slash("kick", descriptionList.get(10)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(0))).addOption(OptionType.USER, "user", "The user to kick", true).addOption(OptionType.STRING, "reason", "Why are they kicked?"));
        commandDataList.add(Commands.slash("lockdown", descriptionList.get(11)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(4))));
        commandDataList.add(Commands.slash("nick", descriptionList.get(12)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(0)).addOption(OptionType.USER, "user", "The user to nick", true).addOption(OptionType.STRING, "nickname", "What is their new name"));

        commandDataList.add(Commands.slash("slowmode", "Sets the slowmode of a specified channel.").setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(4))).addOption(OptionType.INTEGER, "time", "How long to set the slowmode to, in seconds", true).addOption(OptionType.CHANNEL, "channel", "What channel do you want to set the slowmode for?", false));

        // Fun

        commandDataList.add(Commands.slash("dice", descriptionList.get(14)).addOption(OptionType.INTEGER, "sides", "How many sides are on your dice?", true).addOption(OptionType.INTEGER, "numdie", "The number of dice to roll. Defaults to 1.").addOption(OptionType.INTEGER, "modifier", "A modifier to apply to your dice roll"));
        commandDataList.add(Commands.slash("rps", descriptionList.get(15)).addOption(OptionType.STRING, "bet", "What is your bet?", true));
        commandDataList.add(Commands.slash("8ball", descriptionList.get(16)).addOption(OptionType.STRING, "question", "What is your question?", true));
        commandDataList.add(Commands.slash("avatar", descriptionList.get(17)).addOption(OptionType.USER, "username", "What user do you want to get the avatar of?", true));
        commandDataList.add(Commands.slash("clap", descriptionList.get(18)).addOption(OptionType.STRING, "input", "What do you want emphasis on?", true));
        commandDataList.add(Commands.slash("new-compliment", descriptionList.get(19)).addOption(OptionType.STRING, "compliment", "Add a new compliment", false).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(2), getPermissionList().get(1))));
        commandDataList.add(Commands.slash("compliment", descriptionList.get(20)));
        commandDataList.add(Commands.slash("pop", "Pop!"));

        // Utility

        commandDataList.add(Commands.slash("roles", descriptionList.get(22)));
        commandDataList.add(Commands.slash("role", descriptionList.get(23)).addOption(OptionType.ROLE, "role", "What role do you want information on?", true));
        commandDataList.add(Commands.slash("guild", descriptionList.get(24)));
        commandDataList.add(Commands.slash("commands", descriptionList.get(25)));
        commandDataList.add(Commands.slash("runtime", "Gets the runtime of the bot"));

        // Administrator

        commandDataList.add(Commands.slash("reload", descriptionList.get(27)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(2), getPermissionList().get(6))));
        commandDataList.add(Commands.slash("forcereload", descriptionList.get(28)).setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissionList().get(6), getPermissionList().get(2))).addOption(OptionType.STRING, "command", "The command to be reloaded.", true));

        commandListPrim.addAll(commandDataList);
        event.getGuild().updateCommands().addCommands(commandDataList).queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        List<CommandData> unique = new ArrayList<>();
        Set<CommandData> cd = new HashSet<>();
        for (CommandData c : commandListPrim) {
            if (cd.add(c)) {
                unique.add(c);
            }
        }
        commandList.addAll(unique);
    }

    public static ArrayList<CommandData> getCommandList() {
        return commandList;
    }

    public static ArrayList<String> getDescriptionList() {
        return descriptionList;
    }

    public static void addDescriptions(String d) {
        descriptionList.add(d);
    }

    public static ArrayList<Permission> getPermissionList () {
        return permissionList;
    }

    public static void addPermissions(Permission p) {
        permissionList.add(p);
    }

    public static Emoji getModeration() {
        return moderation;
    }

    public static Emoji getFun() {
        return fun;
    }

    public static Emoji getUtility() {
        return utility;
    }

    public static Emoji getAdmin() { return admin; }

    public static CommandData getCommandData(String command) {
        CommandData data = null;
        for (CommandData c : commandList) {
            if (command.equals(c.getName())) {
                data = c;
            }
        }
        return data;
    }
}
