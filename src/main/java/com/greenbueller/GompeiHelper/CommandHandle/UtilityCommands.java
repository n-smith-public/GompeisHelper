package com.greenbueller.GompeiHelper.CommandHandle;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

import static com.greenbueller.GompeiHelper.CommandHandle.CommandHandler.*;
import static com.greenbueller.GompeiHelper.GompeiHelper.*;

public class UtilityCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("roles")) {
            String s = getListOfRoles(event);

            if (s.isEmpty()) {
                event.reply("There are no user created roles in this server.").setEphemeral(true).queue();
            }
            else {
                event.reply(s).setEphemeral(true).queue();
            }
        }

        if (event.getName().equals("role")) {
            Role r = Objects.requireNonNull(event.getOption("role")).getAsRole();
            EmbedBuilder eb = new EmbedBuilder();
            StringBuilder sb = new StringBuilder();
            EnumSet<Permission> p = r.getPermissions();
            int cnt = 0;
            int max = p.size();
            if (max == 1) {
                sb.append("**Permission** - ");
            }
            else if (max > 1) {
                sb.append("**Permissions** - ");

                for (Permission permission : p) {
                    if (cnt == max - 1) {
                        sb.append(permission.getName());
                        break;
                    } else {
                        sb.append(permission.getName()).append(", ");
                        cnt++;
                    }
                }

                sb.append(System.lineSeparator()).append(System.lineSeparator());
            }
            OffsetDateTime dt = r.getTimeCreated();
            getDate(sb, dt);
            boolean o = r.isHoisted();
            if (o) {
                sb.append("**Is hoisted?** - True").append(System.lineSeparator());
            }
            else {
                sb.append("**Is hoisted?** - False").append(System.lineSeparator());
            }
            sb.append("**Role ID** - ").append(r.getId()).append(System.lineSeparator());
            if(r.getIcon() != null) {
                eb.setImage(r.getIcon().getIconUrl());
            }
            eb.setTitle(r.getName());
            eb.setColor(r.getColor());
            eb.setDescription(sb.toString());
            event.reply("Information about " + r.getName()).addEmbeds(eb.build()).setEphemeral(true).queue();
        }

        if (event.getName().equals("guild")) {
            Guild g = event.getGuild();
            assert g != null;
            EmbedBuilder eb = new EmbedBuilder();
            StringBuilder sb = new StringBuilder();
            sb.append("**Roles** (").append(getNumRoles(event)).append(") - ").append(getListOfRoles(event));
            sb.append(System.lineSeparator());

            OffsetDateTime dt = Objects.requireNonNull(g).getTimeCreated();
            getDate(sb, dt);
            sb.append("**Server ID** - ").append(g.getId()).append(System.lineSeparator());
            if(g.getIcon() != null) {
                eb.setImage(g.getIcon().getUrl());
            }
            eb.setTitle(g.getName());
            Random randomGenerator = new Random();
            int red = randomGenerator.nextInt(256);
            int green = randomGenerator.nextInt(256);
            int blue = randomGenerator.nextInt(256);
            Color randomColor = new Color(red, green, blue);
            eb.setColor(randomColor);
            eb.setDescription(sb.toString());
            event.reply("Information about " + g.getName()).addEmbeds(eb.build()).setEphemeral(true).queue();
        }

        if (event.getName().equals("commands")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Commands");
            eb.setColor(Color.CYAN);
            StringBuilder sb = new StringBuilder();
            ArrayList<CommandData> cl = getCommandList();
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> desc = new ArrayList<>(getDescriptionList());
            ArrayList<Permission> perms = getPermissionList();
            ArrayList<String> removedDesc = new ArrayList<>();
            ArrayList<String> finalDesc = new ArrayList<>();

            for (int i = 0; i < cl.size(); i++) {
                for(Permission perm : perms) {
                    if (!event.getMember().getPermissions().contains(perm)) {
                        if(cl.get(i).getDefaultPermissions().getPermissionsRaw() != null) {
                            removedDesc.add(desc.get(i));
                        }
                        else {
                            if (!list.contains(cl.get(i).getName())) {
                                list.add(cl.get(i).getName());
                                break;
                            }
                        }
                    }
                    else {
                        if (!list.contains(cl.get(i).getName())) {
                            list.add(cl.get(i).getName());
                            break;
                        }
                    }
                }
            }
            for(String p : desc) {
                if (!removedDesc.contains(p) && !finalDesc.contains(p)) {
                    finalDesc.add(p);
                }
            }
            for(int i = 0; i < list.size(); i++) {
                try {
                    if (i == 0 && list.get(i).equals("timeout")) {
                        sb.append(getModeration().getFormatted()).append(" **Moderation Commands**")
                                .append(System.lineSeparator()).append(System.lineSeparator());
                    }
                    else if (i == 0 && list.get(i).equals("dice")) {
                        sb.append(getFun().getFormatted()).append(" **Fun Commands**")
                                .append(System.lineSeparator()).append(System.lineSeparator());
                    }
                }
                catch (Exception e) {
                    if (list.get(i).equals("timeout")) {
                        sb.append("**Moderation Commands**").append(System.lineSeparator()).append(System.lineSeparator());
                    }
                    else if (list.get(i).equals("dice")) {
                        sb.append(" **Fun Commands**").append(System.lineSeparator()).append(System.lineSeparator());
                    }
                }
                sb.append("**/").append(list.get(i)).append("** - ").append(finalDesc.get(i)).append(System.lineSeparator());
            }
            eb.setDescription(sb.toString());
            event.reply("Here is every command I support").addEmbeds(eb.build()).setEphemeral(true).queue();
        }

        if (event.getName().equals("runtime")) {
            long start = getStartTime();
            long current = System.currentTimeMillis();
            String output = getString(current, start);

            event.reply(output).setEphemeral(true).queue();
        }
    }

    private @NotNull String getString(long current, long start) {
        String output = "";

        long runtime = current - start;
        Duration d = Duration.ofSeconds(runtime);
        long dy = d.toDays();
        long hr = d.toHours();
        long mn = d.toMinutes();
        long sc = d.getSeconds();

        hr = hr - dy*24;
        mn = mn - (hr*60 + dy*1440);
        sc = sc - (mn*60 + hr*3600 + dy*86400);

        String dayConditional = dy > 1 ? dy + " days" : dy + " day";
        String hoursConditional = hr > 1 ? hr + " hours" : hr + " hour";
        String minuteConditional = mn > 1 ? mn + " minutes" : mn + " minute";
        String secondConditional = sc > 1 ? sc + " seconds." : sc + " second.";
        if (sc > 60) {
            System.out.println("An error has occurred with seconds.");
        }
        else if (mn > 60) {
            System.out.println("An error has occurred with minutes.");
        }
        else if (hr > 60) {
            System.out.println("An error has occurred with hours.");
        }
        if (dy != 0) {
            output = "I have been running for " + dayConditional + ", " + hoursConditional + ", " + minuteConditional +
                    ", and " + secondConditional;
        }
        else if (hr != 0) {
            output = "I have been running for " + hoursConditional + ", " + minuteConditional + ", and " + secondConditional;
        }
        else if (mn != 0) {
            output = "I have been running for " + minuteConditional + ", and " + secondConditional;
        }
        else if (sc != 0) {
            output = "I have been running for " + secondConditional;
        }
        return output;
    }

    private String getListOfRoles(SlashCommandInteractionEvent event) {
        List<Role> r = Objects.requireNonNull(event.getGuild()).getRoles();
        StringBuilder sb = new StringBuilder();

        for (Role role : r) {
            if(!role.getName().equals("@everyone") && !role.getTags().isBot()) {
                if (r.size() == 1) {
                    sb.append(role.getName());
                    break;
                }
                else {
                    sb.append(role.getName()).append(", ");
                }
            }
        }

        if(sb.length() == 0) {
            return "";
        }

        return sb.toString();
    }

    private int getNumRoles(SlashCommandInteractionEvent event) {
        List<Role> r = Objects.requireNonNull(event.getGuild()).getRoles();
        int num = 0;
        for (Role role : r) {
            if (!role.getName().equals("@everyone") && !role.getTags().isBot()) {
                num++;
            }
        }
        return num;
    }

    private void getDate(StringBuilder sb, OffsetDateTime dt) {
        String m;
        String d;
        String h;
        String n;
        if (dt.getMonthValue() < 10) {
            m = "0" + dt.getMonthValue();
        }
        else {
            m = "" + dt.getMonthValue();
        }
        if (dt.getDayOfMonth() < 10) {
            d = "0" + dt.getDayOfMonth();
        }
        else {
            d = dt.getDayOfMonth() + "";
        }
        if (dt.getHour() < 10) {
            h = "0" + dt.getHour();
        }
        else {
            h = "" + dt.getHour();
        }
        if (dt.getMinute() < 10) {
            n = "0" + dt.getMinute();
        }
        else {
            n = "" + dt.getMinute();
        }
        sb.append("**Creation date** - ").append(dt.getYear()).append("-").append(m).append("-")
                .append(d).append(" ").append(h).append(":").append(n)
                .append(System.lineSeparator());
    }
}
