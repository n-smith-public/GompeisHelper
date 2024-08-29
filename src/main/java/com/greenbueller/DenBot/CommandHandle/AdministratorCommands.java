package com.greenbueller.DenBot.CommandHandle;

import com.greenbueller.DenBot.Console;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.greenbueller.DenBot.CommandHandle.CommandHandler.*;

public class AdministratorCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Reload command
        if (event.getName().equals("reload")) {
            if (event.isFromGuild()) {
                if (event.getMember().getId().equals("174645565206953984")) {
                    System.out.println(Console.RED + "[ALERT] Command list has been reloaded by " + event.getUser().getName() + Console.RESET);
                    event.deferReply(true).queue((deferred) -> {
                        ArrayList<CommandData> commandList = new ArrayList<>(getCommandList());

                        // Retrieve existing commands
                        event.getGuild().retrieveCommands().queue(existingCommands -> {
                            List<CommandData> commandsToAdd = commandList.stream()
                                    .filter(cmd -> existingCommands.stream().noneMatch(existingCmd -> existingCmd.getName().equals(cmd.getName())))
                                    .collect(Collectors.toList());

                            if (!commandsToAdd.isEmpty()) {
                                // Update commands
                                event.getGuild().updateCommands()
                                        .addCommands(commandsToAdd)
                                        .queue(
                                                // Success callback
                                                success -> deferred.editOriginal("Commands reloaded successfully.").queue(),
                                                // Error callback
                                                error -> {
                                                    System.out.println("An error occurred while updating commands: " + error.getMessage());
                                                    deferred.editOriginal("An error occurred while updating commands. Please see console.").queue();
                                                }
                                        );
                            } else {
                                // Commands are already up-to-date
                                deferred.editOriginal("Commands are already up to date.").queue();
                            }
                        });
                    });
                } else {
                    event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                }
            }
        }


        if (event.getName().equals("forcereload")) {
            if (event.isFromGuild()) {
                if (event.getMember().getId().equals("174645565206953984")) {
                    String specifiedCommand = event.getOption("command").getAsString();
                    System.out.println(Console.RED + "[ALERT] Command '" + specifiedCommand + "' has been force reloaded by " + event.getUser().getName() + Console.RESET);

                    event.deferReply(true).queue((deferred) -> {
                        CommandData commandData = getCommandData(specifiedCommand);
                        ArrayList<CommandData> fullList = getCommandList();
                        ArrayList<CommandData> updatedList = new ArrayList<>();
                        Set<String> commandNames = new HashSet<>(); // Set to track unique command names

                        // Iterate over the full list of commands and filter out duplicates
                        for (CommandData cmd : fullList) {
                            String cmdName = cmd.getName();
                            if (!cmdName.equals(specifiedCommand) && commandNames.add(cmdName)) {
                                updatedList.add(cmd);
                            }
                        }

                        if (commandData != null) {
                            // Add the specified command to the updated list
                            updatedList.add(commandData);

                            // Update commands
                            event.getGuild().updateCommands()
                                    .addCommands(updatedList)
                                    .queue(
                                            // Success callback
                                            success -> {
                                                deferred.editOriginal("Command '" + specifiedCommand + "' force reloaded successfully.").queue();
                                            },
                                            // Error callback
                                            error -> {
                                                System.out.println("An error occurred while updating commands: " + error.getMessage());
                                                deferred.editOriginal("An error occurred while updating commands. Please see console.").queue();
                                            }
                                    );
                        } else {
                            // Specified command not found
                            deferred.editOriginal("Command '" + specifiedCommand + "' does not exist.").queue();
                        }
                    });
                } else {
                    event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                }
            }
        }


    }
}
