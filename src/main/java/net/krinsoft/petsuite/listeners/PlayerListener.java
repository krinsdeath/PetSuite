package net.krinsoft.petsuite.listeners;

import net.krinsoft.petsuite.Pet;
import net.krinsoft.petsuite.PetCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class PlayerListener implements Listener {
    private PetCore plugin;

    private Map<InfoCooldown, Long> cooldowns = new HashMap<InfoCooldown, Long>();

    public PlayerListener(PetCore instance) {
        plugin = instance;
    }

    public void clean() {
        cooldowns.clear();
    }

    @EventHandler
    void playerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof Tameable && ((Tameable)event.getRightClicked()).isTamed()) {
            String naming = plugin.getNaming(event.getPlayer().getName());
            String transferring = plugin.getTransferring(event.getPlayer().getName());
            Pet pet = plugin.getPetManager().getPet(event.getRightClicked());
            if (pet != null) {
                if (transferring != null) {
                    if (pet.getOwner().equals(event.getPlayer().getName()) || event.getPlayer().hasPermission("petsuite.admin.transfer")) {
                        String old = pet.getOwner();
                        pet.setOwner(transferring);
                        event.getPlayer().sendMessage(ChatColor.GREEN + "[Pet] " + ChatColor.WHITE + "You have transferred ownership of " + pet.getColoredName() + " to " + ChatColor.AQUA + pet.getOwner() + ChatColor.WHITE + ".");
                        plugin.getServer().getPlayer(pet.getOwner()).sendMessage(ChatColor.GREEN + "[Pet] " + ChatColor.WHITE + old + " has transferred ownership of his pet " + pet.getColoredName() + " to you.");
                    }
                    event.setCancelled(true);
                    return;
                }
                if (naming != null) {
                    if (pet.getOwner().equals(event.getPlayer().getName()) || event.getPlayer().hasPermission("petsuite.admin.name")) {
                        String old = pet.getColoredName();
                        pet.setName(naming);
                        event.getPlayer().sendMessage(ChatColor.GREEN + "[Pet] " + ChatColor.WHITE + "You have renamed " + ChatColor.GREEN + old + ChatColor.WHITE + " to " + ChatColor.AQUA + naming + ChatColor.WHITE + ".");
                    }
                    event.setCancelled(true);
                    return;
                }
                if (((Tameable)event.getRightClicked()).getOwner().equals(event.getPlayer()) || event.getPlayer().hasPermission("petsuite.admin.info")) {
                    InfoCooldown cd = new InfoCooldown(event.getPlayer().getName(), pet.getUniqueId().toString());
                    Long timeout = cooldowns.get(cd);
                    if (timeout == null || System.currentTimeMillis() - timeout > 10000) {
                        plugin.showInfo(event.getPlayer(), pet);
                        cooldowns.put(cd, System.currentTimeMillis());
                    }
                }
            }
        }
    }

    private class InfoCooldown {
        private String owner;
        private String pet;

        public InfoCooldown(String owner, String pet) {
            this.owner = owner;
            this.pet = pet;
        }

        @Override
        public String toString() {
            return "InfoCooldown{owner=" + owner + ",pet=" + pet + "}";
        }

        @Override
        public int hashCode() {
            int hash = 7 + 13;
            hash = hash * 7 + this.toString().hashCode();
            return hash + (pet.hashCode() + owner.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }
            InfoCooldown that = (InfoCooldown) o;
            return o.hashCode() == this.hashCode();
        }
    }
}
