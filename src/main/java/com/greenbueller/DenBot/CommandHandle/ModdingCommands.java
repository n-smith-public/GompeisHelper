package com.greenbueller.DenBot.CommandHandle;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Random;

import static com.greenbueller.DenBot.CommandHandle.CommandHandler.*;
public class ModdingCommands extends ListenerAdapter {
    private static final int MIN_COLOR_VALUE = 70;
    private static final int MAX_COLOR_VALUE = 180;

    public static Color getRandomModerateColor() {
        Random random = new Random();
        int red = MIN_COLOR_VALUE + random.nextInt(MAX_COLOR_VALUE - MIN_COLOR_VALUE);
        int green = MIN_COLOR_VALUE + random.nextInt(MAX_COLOR_VALUE - MIN_COLOR_VALUE);
        int blue = MIN_COLOR_VALUE + random.nextInt(MAX_COLOR_VALUE - MIN_COLOR_VALUE);
        return new Color(red, green, blue);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(getRandomModerateColor());
        if (event.getName().equals("user_directory")) {
            eb.setTitle("User Directory");
            eb.setDescription("The user directory is used to store save games, the mods, screenshots (including the ctrl+f11 map), logs (including the error log), crash logs, and other user-specific content. This is stored within the base game's launcher-settings.json file as gameDataPath. By default, these folder locations are used per operating system:\n\n" +
                    "• Windows: `C:\\Users\\<Username>\\Documents\\Paradox Interactive\\Hearts of Iron IV` or, if OneDrive is present, `C:\\Users\\<Username>\\OneDrive\\Documents\\Paradox Interactive\\Hearts of Iron IV`\n" +
                    "• Mac OS: `~/Documents/Paradox Interactive/Hearts of Iron IV`\n" +
                    "• Linux: `~/.local/share/Paradox Interactive/Hearts of Iron IV`\n\n" +
                    "If there are any special characters (Such as diacritics or Cyrillic) in the path to the user directory, it might not load entirely properly and it's better to change via the launcher or the previous launcher settings file directly. This includes the mods being stored there not being loaded properly and the game being unable to open the error log.\n" +
                    "**Notably, the nudge stores its outputs there**, which'll get loaded when opening the base game. These may include the `history/`, `localisation/`, and `map/` folders and their contents, such as `Paradox Interactive/Hearts of Iron IV/history/states`. After generating the files with nudge, dispose of the files as needed by moving them to the mod where they're intended to be or, if inapplicable, deleting them.");
            event.reply("").addEmbeds(eb.build()).queue();
        }
        
        if (event.getName().equals("localisation")) {
            eb.setTitle("Localisation");
            eb.setDescription("Most localisation-related errors appear from one of the 6 possible causes.\n" +
                    "- Wrong file name (**A common issue**). In order for a localisation file to be loaded, it must be a .yml file that specifies the language at the end of the filename, e.g. `modname_l_english.yml`. In this case, the language name is `l_english`, **where l is a lowercase L**. If the filename doesn't follow this format, then the file won't be loaded. For example,`modname_I_english.yml` would be incorrect due to the malformed language name with an uppercase i. Make sure that the Windows explorer is set to show the file extension.\n" +
                    "\n" +
                    "- Wrong encoding (**A common issue**). Localisation files must have the UTF-8 byte order mark. Commonly, this is done as a separate encoding in text editors as UTF-8-BOM. This shows up in the error log. **Only localisation files must be in UTF-8-BOM**, most other files will break with the byte order mark present. This is changed as such: \n" +
                    "  - Notepad++: Top bar's \"encoding\" button.\n" +
                    "  - Sublime Text: File -> Save with encoding.\n" +
                    "  - VSCode: The button in the bottom-right corner, where the current encoding is shown.\n" +
                    "    \n" +
                    "\n" +
                    "- Wrong path: English localisation is within the `localisation/english/` folder. Make sure there is no error in the path, such as naming the folder `localization` or omitting the `english` folder.\n" +
                    "\n" +
                    "- The first line in the localisation file must be `l_language:`, like `l_english:` or `l_french:`.\n" +
                    "\n" +
                    "- Lines afterwards must be formatted as `loc_key:0 \"String\"` (0 is optional). It must be on one line and the string must be enclosed by quotes on both sides. The localisation key (before the colon) can't have non-ASCII characters (e.g. diacritics or non-Latin) or spaces in it. Getting either one of these wrong breaks the rest of the file and shows up in the error log.\n" +
                    "\n" +
                    "- The debug mode only loads files when they are edited, not when they are created. In order to make the game recognise a new localisation file, you must restart it, even on debug mode.");
            event.reply("").addEmbeds(eb.build()).queue();
        }
        
        if (event.getName().equals("debug")) {
            eb.setTitle("Debugging");
            eb.setDescription("The debug mode has two ways to be enabled in launch options:\n" +
                    "• Add `-debug` to the launch options in Steam, within the properties menu.\n" +
                    "• Create a shortcut to the .exe file (Either hoi4.exe directly or dowser.exe for the launcher). Within the shortcut's settings, add `-debug` after the target of the shortcut, separated from the quotes with a space, as in `Hearts of Iron IV/hoi4.exe\" -debug`.\n" +
                    "`-crash_data_log`, which adds the last read file in the meta.yml in the crashes folder in user directory (See `!user_directory` and `!crash_data_log`), also turns on debug mode by default. However, this substantially decreases performance and doesn't turn on automatic reloading (unless together with `-debug`), so it may be better to only use `-debug` in most cases.\n" +
                    "\n" +
                    "There are these benefits when debug mode is turned on via launch options *only*:\n" +
                    "• **Edits to files automatically get loaded in** when the file gets saved. This is done with almost every file, with the exception of state/country history, new countries, and the map folder. This is only done on files which got indexed in the main menu loading, so any new files require a restart anyway.\n" +
                    "• Error log automatically opens in main menu/singleplayer loading as well with the error dog showing up when new errors are detected. This eases checking for new errors dramatically.\n" +
                    "• The nudge button appears in the main menu. This can be helpful if the game crashes on starting in singleplayer or even to speed up going to the nudge.\n" +
                    "• If map errors occur, there is no crash and the errors actually get logged. Even though the map definition crash says to check error.log, **they don't get logged without debug**.\n" +
                    "• Pressing the `~` button in the main menu, used to open the console by default, enables the equivalent to the `gui` console command. This can be useful to find the location of some image knowing the sprite's name (See `!search_in_files`)\n" +
                    "**None of these get enabled when debug is turned on in console**.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("workshop")) {
            eb.setTitle("Workshop");
            eb.setDescription("Steam workshop mods are stored in the same location for every videogame on Steam: `<...>/steam/steamapps/workshop/content/<GAME ID>/<MOD ID>/`. In this case, the steam folder is not necessarily the one that contains steam itself, but the one that contains the game. If the game was set to be installed in a non-default folder, the workshop will be moved there too.\n" +
                    "\n" +
                    "The game's ID can be found within the steam marketplace page for the game before the game's name. For Hearts of Iron IV, it is **394360**, as the link of it is <https://store.steampowered.com/app/394360/Hearts_of_Iron_IV/>.\n" +
                    "The mod's ID can be found within the URL of the steam workshop page for the mod. For example, a mod with the URL of <https://steamcommunity.com/sharedfiles/filedetails/?id=1678247250> will have the ID of 1678247250 and so would be located in `<...>/steam/steamapps/workshop/content/394360/1678247250/`.\n" +
                    "\n" +
                    "This can be confirmed by checking the mod folder within the user directory (See `!user_directory`), as there will be `ugc_<MOD ID>.mod` files all throughout it, which contain the paths to the folders used for steam mods if checked. ");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("crash_data_log")) {
            eb.setTitle("Crash Data Log");
            eb.setDescription("Crash data log mode is a feature added in 1.9, enabled by adding `-crash_data_log` in the launch settings the same way as `-debug` for debug mode: \"Launch Options\" in Steam or after the path to the file if opening the game directly via a shortcut. This automatically enables debug mode and, additionally, keeps track of the last read file for crashes, appending it to the `meta.yml` file in the crash logs (`/crashes/` folder in user directory, see `!user_directory`). This `meta.yml` file is directly within the folder related to the crash, rather than being in the `/logs`/ subfolder. While the debug mode is turned on, the feature of automatically loading edits to files is not enabled by default. **To reiterate, `lastRead` requires the launch option to have been turned on when the game crashed to appear.**\n" +
                    "\n" +
                    "This is not perfect: **if it's the last line in the file, it might be the next one causing the crash**, which is difficult to know. Additionally, for some common crashes, the crash might be difficult to recognise since the game can skip reading files if they're entirely empty. For such crashes you can:\n" +
                    "· Fix every error in the log and hope this gets fixed by collateral damage: even innocent-looking errors can hint at or cause crashes.\n" +
                    "• Remove all replace_paths other than essential ones: `history/states/` and `map/strategicregions/`.\n" +
                    "• Remove and re-add files large chunks at a time until the crash's cause is determined. Binary searching can be used to optimise this.\n" +
                    "\n" +
                    "For some common crash causes identified by the last read file, use the following commands:\n" +
                    "• `!crash_mainmenu` – For crashes during the main menu loading.\n" +
                    "• `!crash_country` – For crashes during or after the country selection, before the country becomes playable.\n" +
                    "• `!client_ping` – For crashes with either `hourly_tick` or `client_ping` as last read.\n");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("search_in_files")) {
            eb.setTitle("Search In Files");
            eb.setDescription("At times, it can be helpful to know how to search every single file within a given folder: if an error doesn't give the folder it comes from, needing to modify an existing localisation key or to find where the image is stored knowing the sprite's name. Notepad++, Sublime Text, and Visual Studio Code each use the Ctrl+Shift+F hotkey for this tool.\n" +
                    "This can be used in combination with the `gui` console command (`~` button in main menu) in order to find out the file of a certain image, as it allows the see the name of the sprite beginning with `GFX` that can be searched in every file in the interface folder.\n" +
                    "A filter on the file extension can be set to speed up the search. This depends on the text editor. Note that `*` is used to mark any amount of any characters, and this is universal.\n" +
                    "\n" +
                    "In Notepad++, this is done with the 'filter' menu. Filters are separated with spaces, and an exclamation point in the beginning marks it as an exclude filter. For example, `*.yml !*french.yml` will result in searching every localisation file aside from the French ones.\n" +
                    "In Sublime Text, this is in the 'where' menu. Filters are separated with commas, and a minus sign in the beginning marks it as an exclude filter. For example, `C:\\Program Files (x86)\\Steam\\steamapps\\common\\Hearts of Iron IV\\, *.txt, -GER*` will search every text file in the base game (Assuming the default location within Windows on Steam) with the exception of those that begin with GER.\n" +
                    "In Visual Studio Code, this is in the menu triggered with the 'Toggle Search Details' button, represented with an ellipsis. This menu has separate \"Files to include\" and \"Files to exclude\" menus, used accordingly. Additionally, this allows representing folder names within the menus, with the doubled `*` (as in `**`) used to represent an arbitrary folder name. For example, `*.gfx` in the \"Files to include\" and `dlc/**` in \"Files to exclude\" will search every single file with the .gfx extension with the exception of those in the dlc folder.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("crash_mainmenu")) {
            eb.setTitle("Crash in Main Menu");
            eb.setDescription("These are some common crashes that can happen during the main menu loading, identified by the last read file as stated by the crash data log (see `!crash_data_log`) and the portion of the main menu they're crashing on.\n" +
                    "\n" +
                    "• `gfx/models/supply/railroad.shader` – There is an issue within the map-creating .bmp files. This can be provinces.bmp exceeding 40 MiB in size, the proportions being inconsistent between different .bmp files, an incorrect header option when saving (e.g. encoding or colour space information), dimensions not being divisible by 128, and many other possible causes.\n" +
                    "\n" +
                    "• `common/countries/cosmetic.txt` – The game fails to detect any focus tree or palette and crashes, as there being no country has a focus was unaccounted for. This is typically replacing by a reckless replace_path to `common/national_focus/` or `common/continuous_focus/` (also can provide `LastScript` in case of the latter).\n" +
                    "\n" +
                    "• `map/rocketsites.txt` – There are two causes for this: the game failed to find any state (caused by a reckless replace_path to `history/states/`) or the game failed to find any unit leader traits (caused by a reckless replace_path to `common/unit_leader/`, also can provide `LastScript`)\n" +
                    "\n" +
                    "• `common/national_focus/filename.txt` where the last line of the last focus file in the folder is given – A focus tree is set to use a shared focus branch via `shared_focus = focus_name`, however the focus does not actually exist.\n" +
                    "\n" +
                    "• `savegame.hoi4` (random savegame that existed or exists) or `map/cities.txt` – There's a large amount (~40–80) of countries with few (such as 15) or no dynamic countries defined. Typically caused by a reckless `replace_path`.\n" +
                    "If either reckless `replace_path` error is met, ensure that every other `replace_path` also abides by the guidelines set in https://hoi4.paradoxwikis.com/replace_path: every replaced folder must have at least one non-empty file in the mod and the base game's folder should be manually checked for anything to port over.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("crash_country")) {
            eb.setTitle("Crash in Country Selection");
            eb.setDescription("These are some common crashes during the country selection screen, identified by the last read file as stated by the crash data log (see `!crash_data_log`) and the part of the selection process that they crash on. Most of these are after selecting a country during the loading.\n" +
                    "\n" +
                    "• `set_controller` at selecting a specific country from the \"Interesting countries\" menu – The country in question does not have a capital state set correctly within their country history file. This crashes the game because the game attempts to zoom onto the capital state after selecting a country, but fails to find a location to zoom onto.\n" +
                    "\n" +
                    "· `tutorial/tutorial.txt` – The tutorial file is set up incorrectly: either it's completely empty (should have at least an empty `tutorial = { }`) or it refers to an invalid state ID within (the file can be *mostly* emptied, keeping the first crash into mind)\n" +
                    "\n" +
                    "• `history/units/filename.txt` – One of OoBs (not always the `lastread` one) puts planes on carriers with the pre-1.12 approach or a country that this order of battle is assigned to doesn't have any `common/ai_templates/` entry that it can use to expand onto ones defined in this OoB (usually caused by a reckless `replace_path`).\n" +
                    "\n" +
                    "• `map/supply_nodes.txt` or `map/railways.txt` – The files in question include supply nodes or railways over invalid provinces, e.g. those without a state assigned. Adjust them as needed: emptying and generating new ones via nudge works.\n" +
                    "\n" +
                    "If either reckless `replace_path` error is met, ensure that every other `replace_path` also abides by the guidelines set in https://hoi4.paradoxwikis.com/replace_path: every replaced folder must have at least one non-empty file in the mod and the base game's folder should be manually checked for anything to port over.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("client_ping")) {
            eb.setTitle("Client Ping");
            eb.setDescription("These are some common crashes marked with either `client_ping` or `hourly_tick` as the last read script within the crash data log (see `!crash_data_log`), and occur in the middle of the game only if the AI is turned on.\n" +
                    "\n" +
                    "• There are states without an owner and the game attempts to evaluate placing airwings on a mission over them. Fixed by giving every state an owner.\n" +
                    "\n" +
                    "• An AI country obtains a division template in the middle of the game (such as by researching a technology) and attempts to expand onto it using an entry in `common/ai_templates/`, but doesn't find anything that can apply to this country. Commonly caused by a reckless overwriting of the folder in question with replace_path.\n" +
                    "\n" +
                    "• There are naval bases or floating harbours in the game that lack a definition in `map/buildings.txt`. The error log contains this after starting a singleplayer playthrough as `Province 12345 is setup as coastal but has no port building in the nudger. This will likely crash the game.`\n" +
                    "Note that the game can fail to assign these models when generating them with the nudge with `Validate in All` or `Random in All`. Instead, it's possible to generate them in each state individually.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("unexpected_token")) {
            eb.setTitle("Unexpected Token");
            eb.setDescription("A class of errors is the game encountering a token that it doesn't expect as an argument or the target of an argument. This includes such like `Unexpected token`, `Malformed token`, `Invalid trigger`, or more specific ones like `Unknown category`. If it doesn't say the file, check everything in the loaded files (e.g. mod and base game, see `!search_in_files` fast searching)\n" +
                    "\n" +
                    "There's a large quantity of causes for this, such as the following:\n" +
                    "• **Wrong scoping** (*Most common*). Usually a bracketing error earlier, such as putting a focus outside of the focus tree. Making the file's code fit guidelines in https://hoi4.paradoxwikis.com/Modding#Indenting usually makes bracket errors stick out easily. This can also be actual mistakes, such as putting `targeted_modifier` inside of an idea's `modifier = { ... }`.\n" +
                    "\n" +
                    "• **Typo in the token/Forgetting the closing quote**. Double-check if it is spelled correctly. This may include variants of English spelling or regular typos. It's useful to use copying and pasting into the Ctrl+F menu for comparison instead of manually checking.\n" +
                    "\n" +
                    "• **Reference to a non-existing database entry**. Check if this is a reference and what's being referenced exists. Such references include trying to use scripted effects, putting decisions in categories, etc.\n" +
                    "\n" +
                    "• **Formatting errors *earlier* in the file**. Violations of code structure (https://hoi4.paradoxwikis.com/Modding#Code_structure) may cause an unexpected token later in the file, such as `is_subject_of = { every_country = { ... } }`: `is_subject_of` only expects a country tag and interprets `{` as a tag instead of a bracket.\n" +
                    "Leaving an argument empty, e.g. `icon = `, causes the game to read the next token as the target as `icon = x = 6`, and then `= 6` is impossible to read.\n" +
                    "The error is usually right before the first unexpected token.\n" +
                    "\n" +
                    "• **File-related errors**, causes the error on the first line. Check if it's in the right folder and has the right encoding (UTF-8, no BOM outside localisation).");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("nudge_states")) {
            eb.setTitle("Nudge States");
            eb.setDescription("This is the process of making states using the nudge:\n" +
                    "\n" +
                    "The nudge is opened through the main menu with the debug mode on (see `!debug`) or from the console with `nudge`. The most stable (i.e. least crashes) approach to open it is starting a playthrough to initialise countries, then going back to the main menu and opening it from there. Afterwards, the state section can be selected.\n" +
                    "A province is added or removed from a state by right-clicking on it. There are the following most important buttons:\n" +
                    "• `Update` – The game reads the state history files and updates the borders shown in-game.\n" +
                    "• `Save` – The current borders of state files get saved, and the borders get updated. **Important:** the results will be within the user directory (see `!user_directory`) within history, map, and localisation folders, where they'll get loaded in base game: always port the results to the mod. If it seems like the borders reset after saving, this is because mod files have higher priority over the user directory; the results still get saved and can be ported.\n" +
                    "\n" +
                    "After doing so, it's **mandatory** to validate buildings to avoid crashes. To do so, navigate to the buildings section. Pressing `Validate All States` and then saving is enough, and the results appear in the map folder in the user directory.\n" +
                    "\n" +
                    "Additionally, adjusting strategic regions is **mandatory** to avoid crashes. While the nudge automatically changes them when editing states, it also means that the newly-created states aren't in any strategic region. A similar process is done as with assigning states to provinces to assign a state to the strategic region in nudge. The results would be in the map and localisation folders in the user directory.\n" +
                    "\n" +
                    "The game crashes when creating a state where the name contains special characters (e.g. diacritics). This can be done by instead editing the localisation file after creating a state.\n" +
                    "The game also removes any quotes from the files, breaking has_dlc checks: correct that if needed.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("githelp")) {
            eb.setTitle("Git Help");
            eb.setDescription("Github and Gitlab are services that provide storing an online copy of the files, also allowing collaboration between people. Using it is the best way to work as a team, also allowing storing backups of the mod in case some data gets lost or a disasterous error appears in a change.\n" +
                    "\n" +
                    "In order to use Github, first create an account on the site. It is best to use a client, as it allows automatically tracking changes to a folder and changing more than one file at a time. Github Desktop (https://desktop.github.com/) is the most popular and the rest of the message will focus on it.\n" +
                    "To create a new repository, first set aside a completely empty folder for it anywhere on the PC. After logging into Github Desktop, select `File` -> `New repository` and create a new one. If entering an existing repository, instead go to `File` -> `Clone repository` and, within the URL section, enter the URL to the repository. The folder must be completely empty.\n" +
                    "\n" +
                    "After doing so, route the mod to link to the repository, so that any changes in it apply in-game automatically. **Not doing so will lead to conflicts**. To change the folder location of a mod, open the `Hearts of Iron IV/mod/modname.mod` file within the user directory (see `!user_directory` for more info) with a text editor, and change the folder that's stated in the `path = \"C:/Users/<...>\"` section of it to be the folder where the repository is. If the path is proper (uses forward slashes for separating folders instead of backslashes, routes to the folder with `descriptor.mod` in it, has no special characters), then the mod will work.\n" +
                    "\n" +
                    "In the top of Github Desktop, there's a button that allows synchronising the repository with the folder's contents, taking on these texts:\n" +
                    "• `Fetch origin` – Checks the repository for any new updates. **Check this regularly**.\n" +
                    "• `Pull from origin` – Downloads new updates from the repository.\n" +
                    "• `Push to origin` – Changes the repository to be your files. **Remember to press this after making a commit**.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("mod_error")) {
            eb.setTitle("Mod Error");
            eb.setDescription("The game reads mods through .mod files located directly within the user directory's (See `!user_directory`) `/mod/` folder, opened via a text editor. If it doesn't exist, the game would have no idea where to find the mod files.\n" +
                    "In particular, these arguments are notable for potential errors:\n" +
                    "• `path = \"mod/modname\"` is the path to the folder where the mod is stored, where the mostly-identical `descriptor.mod` (though without the path) should be present. If the mod doesn't work, check if the path leads to the exact folder with descriptor.mod, uses the forward slash \"/\" as the folder separator, and doesn't have non-ASCII characters (e.g. diacritics and non-Latin scripts will break the mod).\n" +
                    "If the default path contains non-ASCII characters, it's best to change the entire user directory's folder in the base game's `launcher-options.json` file with the `gameDataPath` line, since this will cause other issues in modding, such as failing to open the error log. Don't forget to port over the user directory's files to the new location: otherwise savegames and a lot of launcher data wouldn't be detected by the game.\n" +
                    "\n" +
                    "• `name = \"Mod name\"` is the name as it appears in the launcher. **This must be unique!** Duplicate mod names will fail to get launched by the game, which can easily happen when subscribing to your own Steam mod. Changing the local mod's name will make the Steam mod work, though subscribing to your own mod can still cause issues in updating it due to duplicate `remote_file_id`s. There's never a need to subscribe to your own Steam mod: if it works locally, it'll work on Steam.\n" +
                    "\n" +
                    "Sometimes, the launcher can show a mod's path as erroneous even after the error was fixed. Deleting the mod playset information (user directory's `launcher-v2.sqlite` or `launcher-v2_openbeta.sqlite`) will enforce the game to re-interpret mods. Local mods will not be in any playset, but will appear in the \"All installed mods\" menu, given that the .mod file in the `/mod/` folder exists.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("date_event")) {
            eb.setTitle("Event on X Date");
            eb.setDescription("In order to fire an event on a specific date or date range, the event should be fired on startup with a delay, **with automatic firing disabled**, similarly to a event fired from a focus or another effect. If there are additional conditions, then `trigger = { ... }` within the event will check for it. Remember that leap days do not exist in-game.\n" +
                    "Any effect block executed before or during the game's start can be used, but commonly `on_startup` is used in on actions. \n" +
                    "Example code:\n" +
                    "```country_event = {\n" +
                    "    id = event.1\n" +
                    "    title = event.1.t\n" +
                    "    desc = event.1.d\n" +
                    "    is_triggered_only = yes # Important\n" +
                    "    trigger = {\n" +
                    "        NOT = { has_completed_focus = TAG_focus } # Additional prerequisite\n" +
                    "    } # Options omitted\n" +
                    "}```\n" +
                    "Meanwhile, this would go into any `common/on_actions/*.txt` file:\n" +
                    "```on_actions = {\n" +
                    "    on_startup = {\n" +
                    "        effect = {\n" +
                    "            TAG = {\n" +
                    "                country_event = { id = event.1 days = 90 random_days = 10 } # Fires in 90-100 days\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}```\n" +
                    "\n" +
                    "With a large quantity of events, firing a lot of them on startup can lead to decreased game performance. In these cases, using `on_monthly_TAG` in on actions, with the TAG replaced, can lead to better results, checking for the date in the trigger. See https://hoi4.paradoxwikis.com/On_actions for details.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("mapmodding")) {
            eb.setTitle("Map Modding Guidelines");
            eb.setImage("https://cdn.discordapp.com/attachments/1126589959680311366/1126638168586854430/f6e16879a37ad98e.png");
            eb.setDescription("Before making a custom map, look at the flow chart");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("hoi4moddingedge")) {
            eb.setTitle("HOI4 Modding Edge");
            eb.setDescription("Hello, you mentioned the website https://www.hoi4modding.com in your post. Please be aware that this tool is not recommended by our moderation staff and other experienced modders. The code output from this website tends to result in numerous bugs, leaving more posts on here than if you had just learned to make focus trees properly. The site also discourages you from learning how to make focus trees properly, and the changes take much longer to test compared to them instantly being loaded in-game once the file is saved by the -debug launch option.\n" +
                    "\n" +
                    "In addition, the site is missing many important features that are useful to change in mods (such as on_actions, any sort of AI management, traits, dynamic modifiers, or scripted effects/triggers/localisation) and even what it does cover is still missing a lot. Most notably, focuses lack ai_will_do, shared focuses, search filters or filters; decisions lack ai_will_do, targeted decisions, state highlighting, or a custom cost.\n" +
                    "\n" +
                    "If you are using the tool in order to visualize what you wish to code, we recommend that you use the website https://draw.io instead.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        if (event.getName().equals("shine")) {
            eb.setTitle("Focus GFX Shine");
            eb.setDescription("If your focus icon fails to show up when the focus it is assigned to is available, but does show up otherwise, it means that the focus sprite is missing a shine variant. The shine variant has a spritetype just like the regular variant, and should be placed in a .gfx file inside the interface folder, within `spriteTypes = {...}`.\n" +
                    "\n" +
                    "A shine variant should look something like this:\n" +
                    "\n" +
                    "```\n" +
                    "spriteType = {\n" +
                    "        name = GFX_<icon name>_shine # Change the name , note to keep _shine in the end\n" +
                    "        texturefile = gfx/interface/goals/filename.dds # Change the path\n" +
                    "        effectFile = gfx/FX/buttonstate.lua\n" +
                    "        animation = {\n" +
                    "            animationmaskfile = gfx/interface/goals/filename.dds # Change the path\n" +
                    "\n" +
                    "            animationtexturefile = gfx/interface/goals/shine_overlay.dds\n" +
                    "            animationrotation = -90.0\n" +
                    "            animationlooping = no\n" +
                    "            animationtime = 0.75\n" +
                    "            animationdelay = 0\n" +
                    "            animationblendmode = \"add\"\n" +
                    "            animationtype = \"scrolling\"\n" +
                    "            animationrotationoffset = { x = 0.0 y = 0.0 }\n" +
                    "            animationtexturescale = { x = 1.0 y = 1.0 }\n" +
                    "        }\n" +
                    "        animation = {\n" +
                    "            animationmaskfile = gfx/interface/goals/filename.dds # Change the path\n" +
                    "            animationtexturefile = gfx/interface/goals/shine_overlay.dds\n" +
                    "            animationrotation = 90.0\n" +
                    "            animationlooping = no\n" +
                    "            animationtime = 0.75\n" +
                    "            animationdelay = 0\n" +
                    "            animationblendmode = \"add\"\n" +
                    "            animationtype = \"scrolling\"\n" +
                    "            animationrotationoffset = { x = 0.0 y = 0.0 }\n" +
                    "            animationtexturescale = { x = 1.0 y = 1.0 }\n" +
                    "        }\n" +
                    "        legacy_lazy_load = no\n" +
                    "    }\n" +
                    "```\n" +
                    "\n" +
                    "As an additional note, it is also recommended to not copy over any vanilla interface files or have them be named uniformly, such as `goals.gfx` or `goals_shine.gfx`. Instead, one should create an interface file that only contains the entries needed for any custom national focus ideas.");
            event.reply("").addEmbeds(eb.build()).queue();
        }

        /*if (event.getName().equals("announce")) {
            String userInput = Objects.requireNonNull(event.getOption("input")).getAsString();
            boolean bypassFilter;
            boolean pingEveryone;
            MessageChannel announceChannel;
            try {
                bypassFilter = event.getOption("filter-bypass").getAsBoolean();
            }
            catch (NullPointerException e) {
                bypassFilter = false;
            }
            try {
                pingEveryone = event.getOption("ping-everyone").getAsBoolean();
            }
            catch (NullPointerException e) {
                pingEveryone = false;
            }
            try {
                announceChannel = (MessageChannel) event.getOption("channel").getAsChannel();
            }
            catch (NullPointerException e) {
                announceChannel = event.getChannel();
            }
            boolean sendable = false;
            EmbedBuilder eb = new EmbedBuilder();
            if (isInFilter(userInput)) {
                if (bypassFilter) {
                    eb = createAnnouncement(userInput);
                    sendable = true;
                }
            }
            else {
                eb = createAnnouncement(userInput);
                sendable = true;
            }
            if (sendable) {
                event.reply("Announcement sent.").setEphemeral(true).queue();
                if (pingEveryone) {
                    announceChannel.sendMessage("@everyone" + System.lineSeparator() + System.lineSeparator() + "New announcement!").addEmbeds(eb.build()).queue();
                }
                else {
                    announceChannel.sendMessage("New announcement!").addEmbeds(eb.build()).queue();
                }
            }
            else {
                event.reply("Message not sent.").setEphemeral(true).queue();
            }
        }*/
    }
}