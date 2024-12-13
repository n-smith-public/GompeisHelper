 package com.greenbueller.DenBot;

import com.greenbueller.DenBot.CommandHandle.*;
import com.greenbueller.DenBot.EventHandle.MessageSent;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;


import java.io.*;
import java.net.InetSocketAddress;
import java.util.Scanner;

import static com.greenbueller.DenBot.CommandHandle.MessageFilter.*;

public class DenBot {
    private ShardManager shardManager;
    public ShardManager getShardManager() {
        return shardManager;
    }
    private final String token;

    // Config Files
    private static final String filterConfigPath = "/app/config/.filterConfig.txt";
    private static final String complimentConfigPath = "/app/config/.complimentList.txt";
    private static long startTime;


    public DenBot() {
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            System.err.println("Failed to load Dotenv config: " + e.getMessage());
        }

        if (dotenv != null && dotenv.get("TOKEN") != null) {
            token = dotenv.get("TOKEN");
        } else {
            token = System.getenv("TOKEN");
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Bot token not found in environment variables or .env file");
            }
        }

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.customStatus("Vibing"));
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        try {
            shardManager = builder.build();
            shardManager.addEventListener(new MessageSent());
            shardManager.addEventListener(new ModerationCommands());
            shardManager.addEventListener(new CommandHandler());
            shardManager.addEventListener(new MessageFilter());
            shardManager.addEventListener(new FunCommands());
            shardManager.addEventListener(new UtilityCommands());
            shardManager.addEventListener(new AdministratorCommands());
            shardManager.addEventListener(new ModdingCommands());
            shardManager.addEventListener(new ReputationCommands());
        }
        catch(Exception IllegalArgumentException) {
            System.out.println("Invalid or missing token.");
        }
    }

    public static String getFilterPath() {
        return filterConfigPath;
    }

    public static String getComplimentConfigPath() {
        return complimentConfigPath;
    }

    public static long getStartTime() {
        return startTime;
    }

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        File filterConfig = new File(getFilterPath());
        if (filterConfig.exists()) {
            try {
                Scanner s = new Scanner(filterConfig);
                System.out.println("Filter config file found.");
                while (s.hasNextLine()) {
                    addFilteredWord(s.nextLine());
                }
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            filterConfig.getParentFile().mkdirs();
            try {
                if (filterConfig.createNewFile()) {
                    System.out.println("Filter config file created.");
                } else {
                    System.out.println("Filter config file already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            System.out.println(Console.RED_BOLD + "EXCEPTION: " + e.getMessage());
        }
        DenBot bot = new DenBot();
    }
}
