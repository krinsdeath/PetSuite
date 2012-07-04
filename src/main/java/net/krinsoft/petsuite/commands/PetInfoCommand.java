package net.krinsoft.petsuite.commands;

import net.krinsoft.petsuite.Pet;
import net.krinsoft.petsuite.PetCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;
import java.util.Set;

/**
 * @author krinsdeath
 */
public class PetInfoCommand extends PetSuiteCommand {

    public PetInfoCommand(PetCore instance) {
        super(instance);
        setName("PetSuite: View Info");
        setCommandUsage("/pet info [uuid]");
        setArgRange(1, 1);
        addKey("petsuite info");
        addKey("pet info");
        setPermission("petsuite.info", "Shows info about the provided pet.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Set<Pet> pets = plugin.getPetManager().matchPet(args.get(0));
        if (pets.size() > 0) {
            for (Pet pet : pets) {
                if (pet.getOwner().equals(sender.getName()) || sender.hasPermission("petsuite.admin.info")) {
                    plugin.showInfo(sender, pet);
                    return;
                }
            }
        }
        sender.sendMessage(ChatColor.GREEN + "[Pet] " + ChatColor.WHITE + "No matching pets (or insufficient access).");
    }
}
