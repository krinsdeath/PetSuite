package net.krinsoft.petsuite;

import com.pneumaticraft.commandhandler.CommandHandler;
import net.krinsoft.petsuite.commands.PermissionHandler;
import net.krinsoft.petsuite.commands.PetInfoCommand;
import net.krinsoft.petsuite.commands.PetListCommand;
import net.krinsoft.petsuite.commands.PetNameCommand;
import net.krinsoft.petsuite.commands.PetSuiteDebugCommand;
import net.krinsoft.petsuite.commands.PetSuiteReloadCommand;
import net.krinsoft.petsuite.commands.PetTransferCommand;
import net.krinsoft.petsuite.listeners.EntityListener;
import net.krinsoft.petsuite.listeners.PlayerListener;
import net.krinsoft.petsuite.skills.PetSkill;
import net.krinsoft.petsuite.skills.SkillManager;
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
    private SkillManager skillManager;

    private PlayerListener players;
    private EntityListener entities;

    private CommandHandler commands;

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        initializeConfiguration();
        initializeEvents();
        initializeCommands();
        initializeManagers();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "SimpleNotice");

        startTasks();

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

    private void startTasks() {
        // create a scheduled task to invalidate unloaded pets
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                /**
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
                 **/
                petManager.cacheAll();
            }

        }, 1L, 300L * 20L);
        // create a scheduled task to save the pet database periodically
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                petManager.save();
            }
        }, 300L * 20L, 300L * 20L);
    }

    public void reload() {
        getServer().getScheduler().cancelTasks(this);
        petManager.save();
        configFile = null;
        configuration = null;
        players.clean();
        entities.clean();
        initializeConfiguration();
        initializeManagers();
        startTasks();
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public void debug(String message) {
        if (isDebugging) {
            getLogger().info("[Debug] " + message);
        }
    }

    public void setDebugging(boolean val) {
        isDebugging = val;
        log("Debug mode: " + (val ? "enabled" : "disabled"));
    }

    @Override
    public FileConfiguration getConfig() {
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(configFile);
            configuration.setDefaults(YamlConfiguration.loadConfiguration(configFile));
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
        setDebugging(getConfig().getBoolean("plugin.debug", false));
    }

    private void initializeEvents() {
        players = new PlayerListener(this);
        entities = new EntityListener(this);
        getServer().getPluginManager().registerEvents(players, this);
        getServer().getPluginManager().registerEvents(entities, this);
    }

    private void initializeCommands() {
        PermissionHandler permissions = new PermissionHandler();
        commands = new CommandHandler(this, permissions);
        commands.registerCommand(new PetInfoCommand(this));
        commands.registerCommand(new PetListCommand(this));
        commands.registerCommand(new PetNameCommand(this));
        commands.registerCommand(new PetTransferCommand(this));
        commands.registerCommand(new PetSuiteReloadCommand(this));
        commands.registerCommand(new PetSuiteDebugCommand(this));
    }

    private void initializeManagers() {
        petManager = new PetManager(this);
        skillManager = new SkillManager(this);
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public void showInfo(CommandSender sender, Pet pet) {
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
        PetSkill.FANG fang = getSkillManager().getHighestFang(pet.getLevel());
        PetSkill.SKIN skin = getSkillManager().getHighestSkin(pet.getLevel());
        if (fang != null || skin != null) {
            sender.sendMessage(ChatColor.GREEN + "Skills:");
            if (fang != null) {
                sender.sendMessage(fang.getName() + ": " + fang.getDescription());
            }
            if (skin != null) {
                sender.sendMessage(skin.getName() + ": " + skin.getDescription());
            }
        }
    }

    private Map<String, String> naming = new HashMap<String, String>();
    private Map<String, String> transferring = new HashMap<String, String>();

    /**
     * After setting this, the next owned pet the player clicks will be set to the given name.
     * @param player The player initiating the naming command
     * @param name The new name of the pet
     */
    public void setNaming(String player, String name) {
        naming.put(player, name);
        transferring.remove(player);
    }

    /**
     * Gets and removes the name from the naming list for the specified player
     * @param player The player initiating the name change
     * @return The new name for the pet that was clicked
     */
    public String getNaming(String player) {
        return naming.remove(player);
    }

    /**
     * After setting this, the ownership of the next owned pet the player clicks will be transferred to the target
     * @param player The name of the player initiating the transfer
     * @param target The target of the ownership transfer
     */
    public void setTransferring(String player, String target) {
        transferring.put(player, target);
        naming.remove(player);
    }

    /**
     * Gets and removes the target of a transfer for the specified player
     * @param player The name of the player initiating the transfer
     * @return The target of the ownership transfer
     */
    public String getTransferring(String player) {
        return transferring.remove(player);
    }

}
