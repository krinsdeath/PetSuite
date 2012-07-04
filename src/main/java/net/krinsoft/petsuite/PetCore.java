package net.krinsoft.petsuite;

import com.pneumaticraft.commandhandler.CommandHandler;
import net.krinsoft.petsuite.commands.PermissionHandler;
import net.krinsoft.petsuite.commands.PetInfoCommand;
import net.krinsoft.petsuite.commands.PetListCommand;
import net.krinsoft.petsuite.commands.PetNameCommand;
import net.krinsoft.petsuite.listeners.EntityListener;
import net.krinsoft.petsuite.listeners.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author krinsdeath
 */
public class PetCore extends JavaPlugin {
    private boolean isDebugging;

    private FileConfiguration configuration;
    private File configFile;

    private PetManager petManager;

    private CommandHandler commands;

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        initializeConfiguration();
        initializeEvents();
        initializeCommands();
        initializeManagers();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "SimpleNotice");

        // create a scheduled task to invalidate unloaded pets
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Set<Pet> pets = new HashSet<Pet>();
                for (World world : getServer().getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Tameable && ((Tameable) entity).isTamed()) {
                            Pet pet = petManager.getPet(entity);
                            if (pet != null) {
                                pets.add(pet);
                            }
                        }
                    }
                }
                petManager.cacheAll(pets);
            }

        }, 1L, 300L * 20L);

        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                petManager.save();
            }
        }, 300L * 20L, 300L * 20L);

        isDebugging = getConfig().getBoolean("plugin.debug", false);

        log("Enabled successfully in " + (System.currentTimeMillis() - time) + "ms.");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        petManager.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        List<String> args = new ArrayList<String>(Arrays.asList(arguments));
        args.add(0, label);
        return commands.locateAndRunCommand(sender, args);
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public void debug(String message) {
        if (isDebugging) {
            getLogger().info("[Debug] " + message);
        }
    }

    @Override
    public FileConfiguration getConfig() {
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(configFile);
        }
        return configuration;
    }

    private void initializeConfiguration() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getConfig().setDefaults(YamlConfiguration.loadConfiguration(this.getClass().getResourceAsStream("/config.yml")));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    private void initializeEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }

    private void initializeCommands() {
        PermissionHandler permissions = new PermissionHandler();
        commands = new CommandHandler(this, permissions);
        commands.registerCommand(new PetInfoCommand(this));
        commands.registerCommand(new PetListCommand(this));
        commands.registerCommand(new PetNameCommand(this));
    }

    private void initializeManagers() {
        petManager = new PetManager(this);
    }

    public PetManager getPetManager() {
        return petManager;
    }

    private Map<String, Long> info_cooldown = new HashMap<String, Long>();

    public void showInfo(CommandSender sender, Pet pet) {
        Long cd = info_cooldown.get(sender.getName());
        if (cd == null || System.currentTimeMillis() - cd >= 10000) {
            info_cooldown.put(sender.getName(), System.currentTimeMillis());
            sender.sendMessage(ChatColor.GREEN + "=== Pet Info: " + ChatColor.AQUA + pet.getName() + ChatColor.GREEN + " ===");
            if (!sender.getName().equals(pet.getOwner())) {
                sender.sendMessage(ChatColor.GREEN + "Owner: " + ChatColor.WHITE + pet.getOwner());
            }
            if (sender.hasPermission("petsuite.admin.info")) {
                try {
                    sender.sendMessage(ChatColor.GREEN + "UUID: " + ChatColor.WHITE + pet.getUniqueId().toString());
                } catch (NullPointerException e) {
                    sender.sendMessage(ChatColor.GREEN + "UUID: " + ChatColor.GRAY + "unknown");
                }
            }
            if (pet.getOwner().equals(sender.getName()) || sender.hasPermission("petsuite.admin.health")) {
                try {
                    sender.sendMessage(ChatColor.GREEN + "Health: " + pet.getHealth());
                } catch (NullPointerException e) {
                    sender.sendMessage(ChatColor.GREEN + "Health: " + ChatColor.GRAY + "unknown");
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Level: " + ChatColor.WHITE + pet.getLevel());
            sender.sendMessage(ChatColor.GREEN + "Kills: " + ChatColor.WHITE + pet.getKills());
        }
    }

    private Map<String, String> naming = new HashMap<String, String>();

    /**
     * After setting this, the next owned pet the player clicks will be set to the given name.
     * @param player The player initiating the naming command
     * @param name The new name of the pet
     */
    public void setNaming(String player, String name) {
        naming.put(player, name);
    }

    /**
     * Gets and removes the name from the naming list for the specified player
     * @param player The player initiating the name change
     * @return The new name for the pet that was clicked
     */
    public String getNaming(String player) {
        return naming.remove(player);
    }

}
