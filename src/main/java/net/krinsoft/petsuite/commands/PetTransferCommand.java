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
public class PetTransferCommand extends PetSuiteCommand {

    public PetTransferCommand(PetCore instance) {
        super(instance);
        setName("PetSuite: Transfer");
        setCommandUsage("/pet transfer [target]");
        setArgRange(1, 1);
        addKey("petsuite transfer");
        addKey("pet transfer");
        setPermission("petsuite.transfer", "Transfers ownership of a pet to another person.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Only players can transfer pet ownership.");
            return;
        }
        if (plugin.getServer().getPlayer(args.get(0)) == null) {
            sender.sendMessage(ChatColor.RED + "You must specify a valid player to transfer the pet to.");
            return;
        }
        plugin.setTransferring(sender.getName(), args.get(0));
        sender.sendMessage(ChatColor.GREEN + "[Pet] " + ChatColor.WHITE + "Right click your pet to transfer it to " + ChatColor.AQUA + args.get(0) + ChatColor.WHITE + ".");
    }
}
