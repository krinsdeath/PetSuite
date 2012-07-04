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
public class PetListCommand extends PetSuiteCommand {

    public PetListCommand(PetCore instance) {
        super(instance);
        setName("PetSuite: List Pets");
        setCommandUsage("/pet list");
        setArgRange(0, 1);
        addKey("petsuite list");
        addKey("pet list");
        setPermission("petsuite.list", "Lists available pets.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Set<Pet> pets;
        if (args.size() == 1 && args.get(0).equalsIgnoreCase("-all") && hasPermission(sender, "petsuite.admin.list")) {
            pets = plugin.getPetManager().getAllPets();
        } else {
            pets = plugin.getPetManager().getPets(sender);
        }
        if (pets.size() > 0) {
            sender.sendMessage(ChatColor.GREEN + "=== " + ChatColor.AQUA + "Pet List" + ChatColor.GREEN + " ===");
            for (Pet pet : pets) {
                sender.sendMessage(ChatColor.GREEN + pet.getName() + ChatColor.WHITE + " (Owned by " + ChatColor.AQUA + pet.getOwner() + ChatColor.WHITE + ") - " + pet.getUniqueId().toString().substring(0, 10) + "...");
            }
        } else {
            sender.sendMessage("No pets found.");
        }
    }
}
