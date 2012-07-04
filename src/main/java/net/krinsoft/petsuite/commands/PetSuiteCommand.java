package net.krinsoft.petsuite.commands;

import com.pneumaticraft.commandhandler.Command;
import net.krinsoft.petsuite.PetCore;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * @author krinsdeath
 */
public abstract class PetSuiteCommand extends Command {
    protected PetCore plugin;

    public PetSuiteCommand(PetCore instance) {
        super(instance);
        plugin = instance;
    }

    public boolean hasPermission(CommandSender sender, String node) {
        return sender instanceof ConsoleCommandSender || sender.hasPermission(node);
    }

}
