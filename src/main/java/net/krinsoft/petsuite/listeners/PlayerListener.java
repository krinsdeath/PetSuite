package net.krinsoft.petsuite.listeners;

import net.krinsoft.petsuite.Pet;
import net.krinsoft.petsuite.PetCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class PlayerListener implements Listener {
    private PetCore plugin;

    public PlayerListener(PetCore instance) {
        plugin = instance;
    }

    @EventHandler
    void playerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof Tameable && ((Tameable)event.getRightClicked()).isTamed()) {
            String naming = plugin.getNaming(event.getPlayer().getName());
            if (naming != null) {
                Pet pet = plugin.getPetManager().getPet(event.getRightClicked());
                if (pet != null && (pet.getOwner().equals(event.getPlayer().getName()) || event.getPlayer().hasPermission("petsuite.admin.name"))) {
                    String old = pet.setName(naming);
                    event.getPlayer().sendMessage(ChatColor.GREEN + "[Pet] " + ChatColor.WHITE + "You have renamed " + ChatColor.GREEN + old + ChatColor.WHITE + " to " + ChatColor.AQUA + naming + ChatColor.WHITE + ".");
                }
                event.setCancelled(true);
                return;
            }
            if (((Tameable)event.getRightClicked()).getOwner().equals(event.getPlayer()) || event.getPlayer().hasPermission("petsuite.admin.info")) {
                Pet pet = plugin.getPetManager().getPet(event.getRightClicked());
                if (pet != null) {
                    plugin.showInfo(event.getPlayer(), pet);
                }
            } else {
            }
        }
    }

}
