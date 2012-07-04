package net.krinsoft.petsuite.commands;

import net.krinsoft.petsuite.PetCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class PetNameCommand extends PetSuiteCommand {

    public PetNameCommand(PetCore instance) {
        super(instance);
        setName("PetSuite: Set Name");
        setCommandUsage("/pet name [name]");
        setArgRange(0, 1);
        addKey("petsuite name");
        addKey("pet name");
        setPermission("petsuite.name", "Sets a pet's name by punching it.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Only players can name pets.");
            return;
        }
        String name = (args.size() == 0 ? "DEFAULT" : args.get(0));
        plugin.setNaming(sender.getName(), name);
        sender.sendMessage(ChatColor.GREEN + "[Pet] " + ChatColor.WHITE + "Right click your pet to name it '" + ChatColor.AQUA + name + ChatColor.WHITE + "'");
    }
}
