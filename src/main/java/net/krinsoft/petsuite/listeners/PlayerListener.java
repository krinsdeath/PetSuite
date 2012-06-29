package net.krinsoft.petsuite.listeners;

import net.krinsoft.petsuite.Pet;
import net.krinsoft.petsuite.PetCore;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @author krinsdeath
 */
public class PlayerListener implements Listener {
    private PetCore plugin;

    public PlayerListener(PetCore instance) {
        plugin = instance;
    }

    @EventHandler
    void playerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof Tameable && ((Tameable)event.getRightClicked()).isTamed()) {
            plugin.debug(event.getPlayer().getName() + " interacted with a pet.");
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
