package com.greenbueller.DenBot.CommandHandle;

import com.greenbueller.DenBot.EventHandle.DatabaseConnection;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ReputationCommands extends ListenerAdapter {
    //private static final Bucket bucket = Bucket.builder().addLimit(Bandwidth.simple(1, Duration.ofHours(1))).build();
    private static final ConcurrentHashMap<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        if (event.getName().equals("rep")) {
            User user = event.getOption("user") != null ? event.getOption("user").getAsUser() : null;
            String action = event.getOption("action") != null ? event.getOption("action").getAsString() : null;

            eb.setTitle("Reputation");
            eb.setColor(Color.RED);

            if (user == null && !("leaderboard".equalsIgnoreCase(action))) {
                // No user specified, show default help message
                eb.setDescription("Welcome to the Den Reputation System\n\n" +
                        "Use `/rep user:<user> action:add` to increase someone's reputation.\n" +
                        "Use `/rep user:<user>` to view someone's reputation. \n" +
                        "Use `/rep action:leaderboard` to view the top 10 users.");
                event.replyEmbeds(eb.build()).queue();
                return;
            }

            try (Connection connection = DatabaseConnection.getConnection()) {
                if (connection == null) {
                    event.reply("Database connection error.").setEphemeral(true).queue();
                    return;
                }

                else if ("leaderboard".equalsIgnoreCase(action)) {
                    String maxQuery = "SELECT user_id, rep FROM Reputation ORDER BY rep DESC LIMIT 10";
                    try (PreparedStatement maxStmt = connection.prepareStatement(maxQuery)) {
                        ResultSet rs = maxStmt.executeQuery();
                        StringBuilder leaderboard = new StringBuilder();
                        int rank = 1;
                        while (rs.next()) {
                            String username = rs.getString("user_id");
                            Guild guild = event.getGuild();
                            String usersName = null;
                            if (guild != null) {
                                try {
                                    usersName = Objects.requireNonNull(guild.getMemberById(username)).getEffectiveName();
                                } catch (NullPointerException e) {
                                    usersName = username;
                                }
                            }
                            int rep = rs.getInt("rep");
                            leaderboard.append(rank++).append(". ").append(usersName).append(": ").append(rep).append("\n");
                        }
                        if (leaderboard.length() > 0) {
                            eb.setTitle("The Current Den Reputation Leaderboard");
                            eb.setDescription(leaderboard.toString());
                            event.replyEmbeds(eb.build()).queue();
                            return;
                        } else {
                            eb.setDescription("Not enough users to establish a leaderboard yet. Please try again in the future.");
                            event.replyEmbeds(eb.build()).queue();
                            return;
                        }
                    }
                }

                // Ensure the user exists in the database, creating a record if necessary
                String checkQuery = "SELECT rep FROM Reputation WHERE user_id = ?";
                int reputation = 0;
                try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, user.getId());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        // User exists, fetch current reputation
                        reputation = rs.getInt("rep");
                    } else {
                        // User does not exist, create new entry with reputation = 0
                        String insertQuery = "INSERT INTO Reputation (user_id, rep) VALUES (?, 0)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, user.getId());
                            insertStmt.executeUpdate();
                        }
                    }
                }

                if ("add".equalsIgnoreCase(action)) {
                    event.deferReply().queue();
                    boolean isModerator = event.getMember().hasPermission(Permission.ADMINISTRATOR);
                    Bucket bucket = userBuckets.computeIfAbsent(event.getUser().getId(), id ->
                            Bucket.builder()
                                    .addLimit(Bandwidth.simple(1, Duration.ofHours(1)))
                                    .build()
                    );
                    if (!isModerator) {
                        System.out.println("Trying to give rep -- Cooldown");
                        if (bucket.tryConsume(1)) {
                            if (user.equals(event.getUser())) {
                                System.out.println("Self rep");
                                eb.setDescription("You can't give yourself reputation.");
                                event.getHook().editOriginalEmbeds(eb.build()).queue();
                                return;
                            }
                            System.out.println("Other rep");
                            // Add 1 to the user's reputation
                            String updateQuery = "UPDATE Reputation SET rep = rep + 1 WHERE user_id = ?";

                            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, user.getId());
                                updateStmt.executeUpdate();
                            }
                            reputation += 1; // Reflect the change in the reputation variable
                            eb.setDescription(user.getName() + " has been granted 1 reputation! They now have " + reputation + " reputation.");
                            event.getHook().editOriginalEmbeds(eb.build()).queue();
                            return;
                        } else {
                            System.out.println("Bucket moment");
                            eb.setDescription("Unable to add reputation, you can only give reputation once per hour.");
                            event.getHook().editOriginalEmbeds(eb.build()).queue();
                            return;
                        }
                    } else {
                        if (user.equals(event.getUser())) {
                            eb.setDescription("You can't give yourself reputation.");
                            event.getHook().editOriginalEmbeds(eb.build()).queue();
                            return;
                        }
                        // Add 1 to the user's reputation
                        String updateQuery = "UPDATE Reputation SET rep = rep + 1 WHERE user_id = ?";

                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, user.getId());
                            updateStmt.executeUpdate();
                        }
                        reputation += 1; // Reflect the change in the reputation variable
                        eb.setDescription(user.getName() + " has been granted 1 reputation! They now have " + reputation + " reputation.");
                        event.getHook().editOriginalEmbeds(eb.build()).queue();
                        return;
                    }
                } else if ("clear".equalsIgnoreCase(action)) {
                    // Check if the executor has moderation permissions
                    boolean isModerator = event.getMember().hasPermission(Permission.MANAGE_SERVER);
                    if (isModerator) {
                        // Clear the user's reputation
                        String clearQuery = "UPDATE Reputation SET rep = 0 WHERE user_id = ?";
                        try (PreparedStatement clearStmt = connection.prepareStatement(clearQuery)) {
                            clearStmt.setString(1, user.getId());
                            clearStmt.executeUpdate();
                        }

                        eb.setDescription(user.getName() + "'s reputation has been reset to 0 by a moderator.");
                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                        return;
                    } else {
                        // Non-moderator attempting to use "clear"
                        eb.setDescription("Invalid action: You do not have permission to clear a user's reputation.\n" +
                                "Use `/rep user:<user> action:add` to increase someone's reputation.\n" +
                                "Use `/rep user:<user>` to view someone's reputation. \n" +
                                "Use `/rep action:leaderboard` to view the top 10 users.");
                        event.replyEmbeds(eb.build()).queue();
                        return;
                    }
                } else if (action == null) {
                    // No action specified, show the user's reputation
                    eb.setDescription(user.getName() + " has " + reputation + " reputation.");
                    event.replyEmbeds(eb.build()).queue();
                    return;
                } else {
                    // Invalid action specified, show default help message with an error
                    eb.setDescription("Invalid action: `" + action + "` is not recognized.\n" +
                            "Use `/rep <user> add` to increase someone's reputation.\n" +
                            "Use `/rep <user>` to view someone's reputation. \n" +
                            "Use `/rep leaderboard` to view the top 10 users.");
                    event.replyEmbeds(eb.build()).queue();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                event.reply("An error occurred while interacting with the database.").setEphemeral(true).queue();
                return;
            }

            event.replyEmbeds(eb.build()).queue();
        }
    }


}
