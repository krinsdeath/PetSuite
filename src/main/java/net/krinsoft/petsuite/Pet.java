package net.krinsoft.petsuite;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import java.util.UUID;

/**
 * @author krinsdeath
 */
public class Pet {
    private PetCore plugin;
    private String name;

    private LivingEntity reference;

    public Pet(PetCore instance, LivingEntity entity) {
        this(instance, entity, "wolf");
    }

    public Pet(PetCore instance, LivingEntity entity, String name) {
        this.plugin     = instance;
        this.reference  = entity;
        this.name       = name;
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
     * Gets this pet's name.
     * @return The pet's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets this pet's current health / max health.
     * @return The pet's health as "[(health)/(max health)]".
     */
    public String getHealth() {
        return "[" + reference.getHealth() + "/" + reference.getMaxHealth() + "]";
    }

    public UUID getUniqueId() {
        return reference.getUniqueId();
    }

}
