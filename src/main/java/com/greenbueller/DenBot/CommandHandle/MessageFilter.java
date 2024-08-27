package com.greenbueller.DenBot.CommandHandle;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import static com.greenbueller.DenBot.CommandHandle.CommandHandler.addDescriptions;
import static com.greenbueller.DenBot.DenBot.getFilterPath;


public class MessageFilter extends ListenerAdapter {
    private static final ArrayList<String> filteredWords = new ArrayList<>();

    public static ArrayList<String> getFilteredWords() {
        return filteredWords;
    }

    public static void addFilteredWord(String word) {
        filteredWords.add(word);
    }

    public static void removeFilteredWord(String word) {
        if (filteredWords.contains(word)) {
            filteredWords.remove(word);
        }
        else {
            System.out.println("The word " + word + " is not in the list");
        }
    }

    public static void printFilteredWords() {
        String path = getFilterPath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String word : filteredWords) {
                writer.write(word);
                writer.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshFilteredWords() {
        ArrayList<String> filteredWords = getFilteredWords();
        ArrayList<String> newFilteredWords = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(getFilterPath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                newFilteredWords.add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (String word : newFilteredWords) {
            if (!filteredWords.contains(word)) {
                filteredWords.add(word);
            }
        }
    }

    public static boolean isInFilter(String w) {
        ArrayList<String> filteredWords = getFilteredWords();
        for (String word : filteredWords) {
            if (w.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("filter")) {
            StringBuilder filter = new StringBuilder();
            refreshFilteredWords();
            for (String word : filteredWords) {
                filter.append(word).append(", ");
            }
            if (getFilteredWords().isEmpty()) {
                event.reply("Filter is currently empty.").setEphemeral(true).queue();
            }
            else {
                filter.setLength(filter.length() - 2);
                event.reply(filter.toString()).setEphemeral(true).queue();
            }
        }

        if (event.getName().equals("add-filter")) {
            String newWord = Objects.requireNonNull(event.getOption("word")).getAsString();
            refreshFilteredWords();
            addFilteredWord(newWord);
            printFilteredWords();
            event.reply("Word added to the list").setEphemeral(true).queue();
        }

        if (event.getName().equals("remove-filter")) {
            String newWord = Objects.requireNonNull(event.getOption("word")).getAsString();
            refreshFilteredWords();
            removeFilteredWord(newWord);
            printFilteredWords();
            event.reply("Word removed from the list").setEphemeral(true).queue();
        }
    }
}
