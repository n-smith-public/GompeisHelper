package com.greenbueller.DenBot.CommandHandle;

import com.greenbueller.DenBot.EventHandle.DatabaseConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.util.Objects;
import java.util.Random;

public class ReputationCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        if (event.getName().equals("rep")) {
            User user = event.getOption("user") != null ? event.getOption("user").getAsUser() : null;
            String action = event.getOption("action") != null ? event.getOption("action").getAsString() : null;
            long userId = 0;
            if (user != null) {
                userId = user.getIdLong();
            }
            eb.setTitle("Reputation");
            eb.setColor(Color.RED);
            StringBuilder sb = new StringBuilder();
            sb.append("Welcome to the Den Reputation System!").append(System.lineSeparator())
                    .append("Reputation is gained by helping people in modding help channels.")
                    .append(System.lineSeparator())
                    .append("If you were helped out, you should do `/rep <user> add`")
                    .append("If you want to check someone's reputation, you should do `/rep <user>`");
            if (user != null) {
                if (!action.equals("add")) {
                    try (Connection connection = DatabaseConnection.getConnection();
                         Statement stmt = connection.createStatement()) {

                        String query = "SELECT rep FROM Reputation WHERE userid = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setLong(1, userId);

                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            int reputation = resultSet.getInt("rep");
                            eb.setDescription("Welcome to the Den Reputation System!" + System.lineSeparator() + user.getName() + " has " + reputation + " reputation");
                            event.reply("").addEmbeds(eb.build()).queue();
                        }
                        else {
                            event.reply("An error occurred.").setEphemeral(true).queue();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        event.reply("An error occurred when connecting to database").setEphemeral(true).queue();
                    }
                }
                else {
                    try (Connection connection = DatabaseConnection.getConnection();
                    Statement stmt = connection.createStatement()) {
                        String checkQuery = "SELECT rep FROM Reputation WHERE user_id = ?";
                        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                            checkStmt.setString(1, user.getId());

                            ResultSet rs = checkStmt.executeQuery();
                            if (rs.next()) {
                                // If the user exists, update the reputation by adding 1
                                String updateQuery = "UPDATE Reputation SET rep = rep + 1 WHERE user_id = ?";
                                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                                    updateStmt.setString(1, user.getId());
                                    updateStmt.executeUpdate();
                                }
                                eb.setDescription("Welcome to the Den Reputation System!" + System.lineSeparator() + user.getName() + " has been granted 1 reputation.");
                            } else {
                                // If the user does not exist, insert a new entry with reputation 1
                                String insertQuery = "INSERT INTO Reputation (user_id, rep) VALUES (?, 1)";
                                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                                    insertStmt.setString(1, user.getId());
                                    insertStmt.executeUpdate();
                                    eb.setDescription("Welcome to the Den Reputation System!" + System.lineSeparator() + user.getName() + " has been gained their first reputation.");
                                }
                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        event.reply("An error occurred when connecting to the database.").setEphemeral(true).queue();
                    }
                }
            }
            else {
                eb.setDescription(sb.toString());
            }
        }
    }
}
