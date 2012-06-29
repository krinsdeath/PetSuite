package net.krinsoft.petsuite;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author krinsdeath
 */
public class PetManager {
    private PetCore plugin;

    private Map<UUID, Pet> pets = new HashMap<UUID, Pet>();

    public PetManager(PetCore instance) {
        plugin = instance;
    }

    /**
     * Uses the given entity's unique id to fetch a pet object
     * @param tamed The entity whose unique ID we're checking
     * @return The pet, if applicable. Otherwise null.
     */
    public Pet getPet(Entity tamed) {
        return pets.get(tamed.getUniqueId());
    }

    /**
     * Clears the currently cached pet list and caches the given set of pets in its place.
     * @param validPets The new pet list.
     */
    protected void cacheAll(Set<Pet> validPets) {
        pets.clear();
        for (Pet pet : validPets) {
            pets.put(pet.getUniqueId(), pet);
        }
    }

}
