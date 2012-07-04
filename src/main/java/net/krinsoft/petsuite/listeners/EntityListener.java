package net.krinsoft.petsuite.listeners;

import net.krinsoft.petsuite.Pet;
import net.krinsoft.petsuite.PetCore;
import net.krinsoft.petsuite.skills.PetSkill;
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

    public void clean() {

    }

    @EventHandler(priority = EventPriority.MONITOR)
    void petTame(final EntityTameEvent event) {
        if (event.isCancelled()) { return; }
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getPetManager().addPet(event.getEntity());
            }
        }, 1L);
    }

    /**
     * Calculates a new damage set for applicable pets, by increasing damage (fang) and then decreasing total (skin)
     * @param event The EntityDamageByEntityEvent that caused this method to run
     */
    @EventHandler
    void petDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Tameable && ((Tameable) event.getDamager()).isTamed()) {
            int damage = event.getDamage();
            Pet pet = plugin.getPetManager().getPet(event.getDamager());
            PetSkill.FANG fang = plugin.getSkillManager().getHighestFang(pet.getLevel());
            if (fang != null) {
                switch (fang) {
                    case STONE_FANG: event.setDamage((int) Math.floor(event.getDamage() * .25) + event.getDamage());
                        break;
                    case IRON_FANG: event.setDamage((int) Math.floor(event.getDamage() * .50) + event.getDamage());
                        break;
                    case DIAMOND_FANG: event.setDamage((int) Math.floor(event.getDamage() * .75) + event.getDamage());
                        break;
                    default:
                        break;
                }
                plugin.debug(pet.getColoredName() + " is attacking! Calculated " + fang.name() + " from " + damage + " to " + event.getDamage() + "...");
            }
        }
        if (event.getEntity() instanceof Tameable && ((Tameable) event.getEntity()).isTamed()) {
            int damage = event.getDamage();
            Pet pet = plugin.getPetManager().getPet(event.getEntity());
            PetSkill.SKIN skin = plugin.getSkillManager().getHighestSkin(pet.getLevel());
            if (skin != null) {
                switch (skin) {
                    case STONE_SKIN: event.setDamage((int) Math.floor(event.getDamage() * .75));
                        break;
                    case IRON_SKIN: event.setDamage((int) Math.floor(event.getDamage() * .50));
                        break;
                    case DIAMOND_SKIN: event.setDamage((int) Math.floor(event.getDamage() * .25));
                        break;
                    default:
                        break;
                }
                if (event.getDamage() <= 0) {
                    event.setCancelled(true);
                }
                plugin.debug(pet.getColoredName() + " is defending! Calculated " + skin.name() + " from " + damage + " to " + event.getDamage() + "...");
            }
        }
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
