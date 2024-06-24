package com.greenbueller.GompeiHelper.EventHandle;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class UserJoin extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        String avatar = event.getUser().getAvatarUrl();
        eb.setTitle("Welcome!");
        eb.setDescription("User " + event.getUser().getName() + " joined!");
        eb.setColor(Color.PINK);
        eb.setImage(avatar);
        if (event.getGuild().getId().equals("1064551708275003544")) {
            MessageChannel welcome = event.getGuild().getTextChannelById("1064983292237062264");
            try {
                welcome.sendMessage("A new person has arrived!").addEmbeds(eb.build()).queue();
            }
            catch (Exception e) {
                System.out.println("An error occurred with user arrival in '27");
            }
        }
    }
}
