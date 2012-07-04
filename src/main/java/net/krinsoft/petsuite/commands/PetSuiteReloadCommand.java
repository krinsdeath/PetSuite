package net.krinsoft.petsuite.commands;

import net.krinsoft.petsuite.PetCore;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class PetSuiteReloadCommand extends PetSuiteCommand {

    public PetSuiteReloadCommand(PetCore instance) {
        super(instance);
        setName("PetSuite: Reload");
        setCommandUsage("/pet reload");
        setArgRange(0, 0);
        addKey("petsuite reload");
        addKey("pet reload");
        setPermission("petsuite.reload", "Reloads PetSuite's config files and managers.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        long time = System.nanoTime();
        plugin.reload();
        time = System.nanoTime() - time;
        sender.sendMessage("PetSuite reloaded in " + time + "ns (" + (time / 1000000) + "ms)");
    }
}
