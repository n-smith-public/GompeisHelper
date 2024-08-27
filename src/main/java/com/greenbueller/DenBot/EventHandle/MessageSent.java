package com.greenbueller.DenBot.EventHandle;

import com.greenbueller.DenBot.Console;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.greenbueller.DenBot.CommandHandle.MessageFilter.*;

public class MessageSent extends ListenerAdapter {

    /*private int noSpam = 0;
    private int spamFilter = 4;*/
    ArrayList<String> filter = getFilteredWords();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.isFromGuild()) {
            for (String word : filter) {
                if (!event.getAuthor().isBot()) {
                    if (event.getMessage().getContentRaw().toLowerCase().contains(word.toLowerCase())) {
                        System.out.println("User " + Console.CYAN_UNDERLINED + event.getAuthor().getName() + Console.RESET + " sent a filtered message to " + event.getChannel().getName() + " in " + event.getGuild().getName() + ": " + word);
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
}
