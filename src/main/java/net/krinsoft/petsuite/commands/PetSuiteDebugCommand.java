package net.krinsoft.petsuite.commands;

import net.krinsoft.petsuite.PetCore;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class PetSuiteDebugCommand extends PetSuiteCommand {

    public PetSuiteDebugCommand(PetCore instance) {
        super(instance);
        setName("PetSuite: Debug");
        setCommandUsage("/pet debug");
        setArgRange(0, 1);
        addKey("petsuite debug");
        addKey("pet debug");
        setPermission("petsuite.debug", "Turns debug mode on or off.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String arg;
        if (args.size() == 0) {
            arg = "false";
        } else {
            arg = args.get(0);
        }
        plugin.setDebugging(Boolean.parseBoolean(arg));
    }
}
