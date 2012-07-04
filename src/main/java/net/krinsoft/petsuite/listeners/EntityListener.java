package net.krinsoft.petsuite.listeners;

import net.krinsoft.petsuite.Pet;
import net.krinsoft.petsuite.PetCore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class EntityListener implements Listener {
    private PetCore plugin;

    public EntityListener(PetCore instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void petTame(EntityTameEvent event) {
        if (event.isCancelled()) { return; }
        plugin.getPetManager().addPet(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void petKill(EntityDeathEvent e) {
        if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
            if (event.getDamager() instanceof Tameable && ((Tameable)event.getDamager()).isTamed()) {
                Pet pet = plugin.getPetManager().getPet(event.getDamager());
                pet.addKill();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void petSpawn(ChunkLoadEvent event) {
        if (event.getChunk().getEntities().length == 0) { return; }
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Tameable && ((Tameable) entity).isTamed()) {
                plugin.getPetManager().getPet(entity);
            }
        }
    }

}
