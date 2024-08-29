package com.greenbueller.DenBot.CommandHandle;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import static com.greenbueller.DenBot.CommandHandle.CommandHandler.*;
import static com.greenbueller.DenBot.CommandHandle.MessageFilter.*;
import static com.greenbueller.DenBot.DenBot.getComplimentConfigPath;

public class FunCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("dice")) {
            try {
                int f = Objects.requireNonNull(event.getOption("sides")).getAsInt();
                int modifier = 0;
                try {
                    modifier = event.getOption("modifier").getAsInt();
                }
                catch (Exception e) {
                    //
                }
                int n = 1;
                try {
                    n = event.getOption("numdie").getAsInt();
                }
                catch (Exception e) {
                    System.out.println("numdie is null, ignoring \nCommand called by" + event.getUser().getName());
                }
                finally {
                    if (f == 1) {
                        event.reply("You rolled a one sided die. You broke the universe.").queue();
                    }
                    else if (f <= 0) {
                        event.reply("Invalid number of sides.").queue();
                    }
                    else {
                        int range = f + 1;
                        int[] val = new int[n];

                        for (int i = 0; i < n; i++) {
                            int rand = (int)(Math.random() * range);
                            val[i] = rand;
                        }

                        if (n == 1) {
                            int total = val[0] + modifier;
                            if (modifier != 0) {
                                event.reply("On your " + f + "-sided die, you rolled a " + total + " with a modifier of " + modifier + ".").queue();
                            }
                            else {
                                event.reply("On your " + f + "-sided die, you rolled a " + total + ".").queue();
                            }
                        }
                        else {
                            StringBuilder r = new StringBuilder();
                            int i2 = 1;
                            r.append("You are rolling *").append(n).append(" ").append(f).append("-sided die*.").append(System.lineSeparator()).append(System.lineSeparator());
                            for (int i = 0; i < n; i++) {
                                int total = val[i] + modifier;
                                if(modifier != 0) {
                                    r.append("On die ").append(i2).append(" you rolled a ").append(total).append(" with a modifier of ").append(modifier).append(".")
                                            .append(System.lineSeparator());
                                }
                                else {
                                    r.append("On die ").append(i2).append(" you rolled a ").append(total).append(".")
                                            .append(System.lineSeparator());
                                }
                                i2++;
                            }
                            int sum = 0;
                            for (int a : val) {
                                sum+=a;
                            }
                            int avg = sum/val.length;
                            avg += modifier * val.length;
                            if (modifier != 0) {
                                r.append(System.lineSeparator()).append("On average, you got ").append(avg).append(" with a modifier of ").append(modifier).append(".").append(System.lineSeparator());
                            }
                            else {
                                r.append(System.lineSeparator()).append("On average, you got ").append(avg).append(".").append(System.lineSeparator());
                            }
                            event.reply(r.toString()).queue();

                        }
                    }
                }
            }
            catch (Exception e) {
                System.out.println("An error occurred while trying to roll dice");
                event.reply("An error occurred, please contact developer.").setEphemeral(true).queue();
                e.printStackTrace();
            }
        }

        if (event.getName().equals("rps")) {
            String userInput = Objects.requireNonNull(event.getOption("bet")).getAsString();
            int userPick = 0;

            int rand = (int)(Math.random()*3);

            if (userInput.equalsIgnoreCase("Rock")) {
                userPick = 1;
            }

            else if (userInput.equalsIgnoreCase("Paper")) {
                userPick = 2;
            }

            else if (userInput.equalsIgnoreCase("Scissors")) {
                userPick = 3;
            }

            else {
                event.reply("Invalid option. Please try again.").setEphemeral(true).queue();
            }

            // userPick 1 is Rock, Rock beats Scissors but loses to Paper
            // userPick 2 is Paper, Paper beats rock, but loses to Scissors
            // userPick 3 is Scissors, Scissors beats paper, but loses to Rock

            if(rand == 1) {
                if (userPick == 1) {
                    event.reply("Rock v Rock... Tie!").queue();
                }

                else if (userPick == 2) {
                    event.reply("Rock v Paper... You win!").queue();
                }

                else {
                    event.reply("Rock v Scissors... I win!").queue();
                }
            }

            if (rand == 2) {
                if (userPick == 1) {
                    event.reply("Paper v Rock... I win!").queue();
                }

                else if (userPick == 2) {
                    event.reply("Paper v Paper... Tie!").queue();
                }

                else {
                    event.reply("Paper v Scissors... You win!").queue();
                }
            }

            if (rand == 3) {
                if (userPick == 1) {
                    event.reply("Scissors v Rock... You win!").queue();
                }

                else if (userPick == 2) {
                    event.reply("Scissors v Paper... I win!").queue();
                }

                else {
                    event.reply("Scissors v Scissors... Tie!").queue();
                }
            }
        }

        if (event.getName().equals("8ball")) {
            String userInput = Objects.requireNonNull(event.getOption("question")).getAsString();
            String[] results = {"Yes","It is certain","It is decidedly so", "Without a doubt", "Yes definitely", "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes", "Signs point to yes",
            "Reply hazy, try again", "Ask again later", "Better not tell you now", "Cannot predict now", "Concentrate and ask again",
            "Don't count on it", "My reply is no", "My sources say no", "Outlook not so good", "Very doubtful", "Nah"};

            int numResults = results.length;
            String[] check = userInput.split(" ");
            boolean isAllowed = true;

            for ( String s : check) {
                if (getFilteredWords().contains(s)) {
                    isAllowed = false;
                    break;
                }
            }

            if (isAllowed) {
                int rand = (int) (Math.random() * numResults);
                event.reply("*" + userInput + "*\n" + results[rand]).queue();
            }
            else {
                event.reply("Message not allowed.").setEphemeral(true).queue();
            }
        }

        if (event.getName().equals("avatar")) {
            User user;
            user = Objects.requireNonNull(event.getOption("username")).getAsUser();

            EmbedBuilder eb = new EmbedBuilder();
            String avatar = user.getAvatarUrl();
            eb.setTitle(user.getName());
            eb.setColor(Color.PINK);
            eb.setImage(avatar);
            event.reply("Here's the specified avatar.").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("clap")) {
            String userInput = Objects.requireNonNull(event.getOption("input")).getAsString();
            String[] results = userInput.split(" ");
            if (isInFilter(userInput)) {
                event.reply("Message not sent. Ask server moderators for more details.").setEphemeral(true).queue();
            } else {
                StringBuilder sb = new StringBuilder();
                for (String s : results) {
                    sb.append(s).append(" :clap: ");
                }
                String result = sb.toString();
                event.reply(result).queue();
            }
        }

        if (event.getName().equals("compliment")) {
            sendCompliment(event);
        }

        if (event.getName().equals("new-compliment")) {
            String compliment;
            ArrayList<String> compliments = new ArrayList<>();
            File complimentList = new File(getComplimentConfigPath());
            try {
                compliment = event.getOption("compliment").getAsString();
                getFile(complimentList, compliments);
                if (!compliment.endsWith(".") || !compliment.endsWith("!")) {
                    compliment = compliment + ".";
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(getComplimentConfigPath()))) {
                    for (String word : compliments) {
                        writer.write(word);
                        writer.newLine();
                    }
                    writer.write(compliment + "\n");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                event.reply("Compliment successfully added. \n*" + compliment + "*").setEphemeral(true).queue();
            }
            catch (Exception e) {
                sendCompliment(event);
            }
        }

        if (event.getName().equals("pop")) {
            EmbedBuilder eb = popBuilder();
            event.reply("Pop!").addEmbeds(eb.build()).queue();
        }

        // TODO: Poll

        // TODO: Timer

        // TODO: Color

        // TODO: DadJoke

        // TODO: Choose4Me
    }

    private @NotNull EmbedBuilder popBuilder() {
        Random randomGenerator = new Random();
        int r = randomGenerator.nextInt(256);
        int g = randomGenerator.nextInt(256);
        int b = randomGenerator.nextInt(256);
        Color randomColor = new Color(r, g, b);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Pop!");
        eb.setColor(randomColor);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sb.append("||Pop|| ");
                if (j == 8) {
                    sb.append(System.lineSeparator());
                }
            }
        }
        eb.setDescription(sb.toString());
        return eb;
    }

    public void sendCompliment(SlashCommandInteractionEvent event) {
        ArrayList<String> compliments = new ArrayList<>();
        File complimentList = new File(getComplimentConfigPath());
        getFile(complimentList, compliments);

        if (compliments.isEmpty()) {
            event.reply("Please contact server moderators").setEphemeral(true).queue();
        }
        else {
            int rand = (int) (Math.random() * compliments.size());
            event.reply(event.getUser().getAsMention() + ", " + compliments.get(rand)).queue();
        }
    }

    public static void getFile(File f, ArrayList<String> a) {
        if (f.exists()) {
            try {
                Scanner s = new Scanner(f);
                while (s.hasNextLine()) {
                    a.add(s.nextLine());
                }
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            f.getParentFile().mkdirs();
            try {
                if (f.createNewFile()) {
                    System.out.println("File created.");
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
