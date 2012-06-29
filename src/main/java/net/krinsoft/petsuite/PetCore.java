package net.krinsoft.petsuite;

import com.pneumaticraft.commandhandler.CommandHandler;
import net.krinsoft.petsuite.commands.PermissionHandler;
import net.krinsoft.petsuite.listeners.PlayerListener;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * @author krinsdeath
 */
public class PetCore extends JavaPlugin {
    private boolean isDebugging;

    private PetManager petManager;

    private CommandHandler commands;

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        initializeEvents();
        initializeCommands();
        initializeManagers();

        // create a scheduled task to invalidate unloaded pets
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                Set<Pet> pets = new HashSet<Pet>();
                for (World world : getServer().getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Tameable && ((Tameable)entity).isTamed()) {
                            Pet pet = petManager.getPet(entity);
                            if (pet != null) {
                                pets.add(pet);
                            }
                        }
                    }
                }
                petManager.cacheAll(pets);
            }

        }, 300L * 20L, 300L * 20L);

        log("Enabled successfully in " + (System.currentTimeMillis() - time) + "ms.");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        return false;
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public void debug(String message) {
        if (isDebugging) {
            getLogger().info("[Debug] " + message);
        }
    }

    private void initializeEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void initializeCommands() {
        PermissionHandler permissions = new PermissionHandler();
        commands = new CommandHandler(this, permissions);
    }

    private void initializeManagers() {
        petManager = new PetManager(this);
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public void showInfo(CommandSender sender, Pet pet) {

    }

}
