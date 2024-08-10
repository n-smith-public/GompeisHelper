package com.greenbueller.GompeiHelper;

import com.greenbueller.GompeiHelper.CommandHandle.*;
import com.greenbueller.GompeiHelper.EventHandle.MessageSent;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;


import javax.sound.sampled.*;
import java.io.*;
import java.util.Scanner;

import static com.greenbueller.GompeiHelper.CommandHandle.MessageFilter.*;

public class GompeiHelper {
    private ShardManager shardManager;
    private final Dotenv config;
    public ShardManager getShardManager() {
        return shardManager;
    }
    public Dotenv getConfig() {
        return config;
    }

    // Config Files
    private static final String filterConfigPath = "/app/config/.filterConfig.txt";
    private static final String complimentConfigPath = "/app/config/.complimentList.txt";
    private static long startTime;

    public GompeiHelper() {
        config = Dotenv.configure().load();
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(getConfig().get("TOKEN"));
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

        playStartupSound();
        try {
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            System.out.println(ConsoleColors.RED_BOLD + "EXCEPTION: " + e.getMessage());
        }
        GompeiHelper bot = new GompeiHelper();
    }

    public static void playStartupSound() {
        Clip clip = null;
        try {
            File audioFile = new File("config/Startup.wav");
            //System.out.println("Loading audio file: " + audioFile.getAbsolutePath());

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            //System.out.println("Audio input stream obtained.");

            // Get audio format
            AudioFormat format = audioInputStream.getFormat();
            //System.out.println("Audio format: " + format);

            // Ensure the format is supported
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                //System.out.println("Audio format not supported.");
                return;
            }
            //System.out.println("Audio format is supported.");

            // Set up audio data line
            clip = (Clip) AudioSystem.getLine(info);
            //System.out.println("Audio clip obtained.");

            // Open audio data line and start playback
            clip.open(audioInputStream);
            //System.out.println("Audio clip opened and loaded.");

            Clip finalClip = clip;
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    synchronized (finalClip) {
                        finalClip.notify();
                    }
                }
            });

            clip.start();
            //System.out.println("Playback started.");

            // Wait for playback to finish
            synchronized (clip) {
                while (clip.isRunning()) {
                    clip.wait();
                }
            }

            //System.out.println("Playback finished.");

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException ex) {
            System.out.println("EXCEPTION: " + ex.getMessage());
        } finally {
            if (clip != null) {
                clip.close();
                //System.out.println("Audio clip closed.");
            }
        }
    }
}
