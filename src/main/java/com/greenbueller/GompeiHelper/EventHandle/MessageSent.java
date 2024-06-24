package com.greenbueller.GompeiHelper.EventHandle;

import com.greenbueller.GompeiHelper.ConsoleColors;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.greenbueller.GompeiHelper.CommandHandle.MessageFilter.*;

public class MessageSent extends ListenerAdapter {

    /*private int noSpam = 0;
    private int spamFilter = 4;*/
    ArrayList<String> filter = getFilteredWords();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.isFromGuild()) {
            //if (noSpam == 0) {
            String guildID = event.getGuild().getId();
            // Checks if event occurred in a valid server.

            ArrayList<String> servers = new ArrayList<>();
            servers.add("1064551708275003544");
            servers.add("730880861037264937");

            ArrayList<String> filteredUsers = new ArrayList<>();
            filteredUsers.add("305471652354064395");
            filteredUsers.add("401115575994155021");
            filteredUsers.add("174645565206953984");

            if (servers.contains(guildID)) {
                // Delays message sending by an integer number of seconds, set to 0 for instant sending.
                int delay = 2;

                // Gets the ID of the person who sent the message
                String sender = event.getAuthor().getId();


                if (filteredUsers.contains(sender)) {
                    // Gets the channel it was sent in, and a link to said message
                    String location = event.getChannel().getName();
                    String hyperlink = event.getJumpUrl();
                    // Gets the mod channel, and a backup channel on my test server
                    MessageChannel mod = event.getGuild().getTextChannelById("1064615477005004862");
                    MessageChannel test = event.getGuild().getTextChannelById("730880861594976389");
                    MessageChannel bots = event.getGuild().getTextChannelById("1081346537126375544");
                    //Role moderator = event.getGuild().getRoleById("1064552972186566686");
                    // Attempts to get the mod role, if it does not exist (i.e. is on my test server, it will give a warning in the console).
                    /*String modRole = "";
                    try {
                        modRole = moderator.getAsMention();
                    } catch (Exception NullPointerException) {
                        System.out.println(ConsoleColors.RED + "\n[Warning]: Moderator role does not exist." + ConsoleColors.RESET + "\nMessage sent by " + ConsoleColors.WHITE_BOLD_BRIGHT + event.getAuthor().getName() + ConsoleColors.RESET + " in " + ConsoleColors.WHITE_BOLD_BRIGHT + event.getGuild().getName() + ConsoleColors.RESET + ".");
                    } finally {*/
                    // If the message sender is Matt M.
                    if (sender.equals(filteredUsers.get(0))) {
                        String message = "Matt posted in [#" + location + "](<" + hyperlink + ">).";
                        sendMessage(message, test, mod);
                    } /*else if (sender.equals(filteredUsers.get(1))) {
                                    if (!event.getChannel().equals(bots)) {
                                        String message = "Rachel posted in [#" + location + "](<" + hyperlink + ">).";
                                        sendMessage(message, test, mod);
                                    }
                                }*/
                }
            }
        /*else {
            System.out.println(ConsoleColors.YELLOW + "[NOTICE]: Message not sent due to noSpam." + ConsoleColors.RESET);
            if (noSpam > spamFilter) {
                noSpam = 0;
            }
            else {
                noSpam++;
            }
        }*/

            for (String word : filter) {
                if (!event.getAuthor().isBot()) {
                    if (event.getMessage().getContentRaw().toLowerCase().contains(word.toLowerCase())) {
                        System.out.println("User " + ConsoleColors.CYAN_UNDERLINED + event.getAuthor().getName() + ConsoleColors.RESET + " sent a filtered message to " + event.getChannel().getName() + " in " + event.getGuild().getName() + ": " + word);
                        event.getMessage().delete().queue();
                        MessageChannel t;
                        // If in '27 server
                        if (event.getGuild().getId().equals("1064551708275003544")) {
                            t = event.getGuild().getTextChannelById("1097684099151437884");
                        } else {
                            t = event.getGuild().getTextChannelById("730880861594976389");
                        }

                        if (t != null) {
                            t.sendMessage("[ALERT] User " + event.getAuthor().getName() + " attempted to say a filtered word ||" + word.toLowerCase() + "|| in " + event.getChannel().getAsMention() + ".").queue();
                        }
                    }
                }
            }
        }
    }

    /**
     * Attempts to send a message to a specified channel
     * @param channel is the channel to send @message
     * @param message is the message being sent in @channel
     * Returns void
     */
    public void attemptMessage(MessageChannel channel, String message) {
        try {
            channel.sendMessage(message).queue();
        }
        catch (Exception NullPointerException) {
            System.out.println("Channel does not exist.");
        }
    }

    public void sendMessage(String message, MessageChannel test, MessageChannel mod) {
        /*if (!modRole.isEmpty()) {
            message = modRole + " " + message;
        }*/
        // Tries to send the message to mod channel, if it cannot, it will send it to my test server.
        try {
            mod.sendMessage(message).queue();
        } catch (Exception NullPointerException) {
            attemptMessage(test, message);
            System.out.println("Channel #wang-gang does not exist.");
        }
    }

    /*public void setSpamFilter(int spamFilter) {
        this.spamFilter = spamFilter;
    }*/
}
