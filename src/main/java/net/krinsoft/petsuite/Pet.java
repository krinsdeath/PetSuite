package net.krinsoft.petsuite;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import java.util.UUID;

/**
 * @author krinsdeath
 */
public class Pet {
    private PetCore plugin;
    private String owner;
    private String name;
    private int level;
    private int kills;

    private LivingEntity reference;

    /**
     * Constructs a default pet object with level 1 and 0 kills.
     * @param instance The PetSuite plugin handle.
     * @param entity The entity reference.
     * @param name The name of the pet.
     * @param owner The name of the owner of the pet.
     */
    public Pet(PetCore instance, LivingEntity entity, String name, String owner) {
        this(instance, entity, name, owner, 1);
    }

    public Pet(PetCore instance, LivingEntity entity, String name, String owner, int level) {
        this(instance, entity, name, owner, level, 0);
    }

    public Pet(PetCore instance, LivingEntity entity, String name, String owner, int level, int kills) throws IllegalArgumentException {
        Validate.isTrue(entity instanceof Tameable, "Entity is not tameable.");
        Validate.isTrue(((Tameable)entity).isTamed(), "Entity isn't tamed.");
        Validate.isTrue(name != null, "Invalid name.");
        Validate.isTrue(owner != null, "Invalid owner.");
        Validate.isTrue(level > 0, "Level starts at 1.");
        this.plugin     = instance;
        this.reference  = entity;
        this.owner      = owner;
        this.name       = name;
        this.level      = level;
        this.kills      = kills;
    }

    /**
     * Sets the pet's name.
     * @param name The pet's new name.
     * @return The pet's old name.
     */
    public String setName(String name) {
        String tmp = this.name;
        this.name = name;
        return tmp;
    }

    /**
     * Gets this pet's owner's name.
     * @return The pet's owner's name.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets this pet's name.
     * @return The pet's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets this pet's level, based on kills. The pet's level determines which skills they have access to.
     * @return The pet's level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets this pet's kill count. Each unique kill is equivalent to 1 experience point, which contributes to the pet's level.
     * @return The pet's kill count.
     */
    public int getKills() {
        return kills;
    }

    /**
     * Increments this pet's kill count by one, and notifies the player (if the option is set)
     */
    public void addKill() {
        kills++;
        if (plugin.getConfig().getBoolean("plugin.notify_on_kill", false)) {
            Player player = plugin.getServer().getPlayer(owner);
            if (player != null) {
                String message = ChatColor.GREEN + "[Pet] " + ChatColor.AQUA + name + ChatColor.WHITE + " just killed something.";
                if (player.getListeningPluginChannels().contains("SimpleNotice")) {
                    player.sendPluginMessage(plugin, "SimpleNotice", message.getBytes(java.nio.charset.Charset.forName("UTF-8")));
                } else {
                    player.sendMessage(message);
                }
            }
        }
    }

    /**
     * Gets this pet's current health / max health.
     * @return The pet's health as "[(health)/(max health)]".
     */
    public String getHealth() {
        int health = reference.getHealth(), max = reference.getMaxHealth();
        String current;
        if (health / max < .3) {
            current = ChatColor.GRAY + "" + reference.getHealth() + "" + ChatColor.WHITE;
        } else if (health / max >= .3 && health / max < .6) {
            current = ChatColor.RED + "" + reference.getHealth() + "" + ChatColor.WHITE;
        } else {
            current = ChatColor.GREEN + "" + reference.getHealth() + "" + ChatColor.WHITE;
        }
        return ChatColor.WHITE + "[" + current + "/" + max + "]";
    }

    /**
     * Gets this pet's entity unique ID.
     * @return The pet's unique ID.
     */
    public UUID getUniqueId() {
        return reference.getUniqueId();
    }

}
