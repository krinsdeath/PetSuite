package net.krinsoft.petsuite;

import net.krinsoft.petsuite.databases.Database;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private Database database;

    private Map<UUID, Pet> pets = new HashMap<UUID, Pet>();

    public PetManager(PetCore instance) {
        plugin = instance;
        database = new Database(plugin);
    }

    public void save() {
        plugin.debug("Saving database...");
        PreparedStatement statement = database.prepare("REPLACE INTO petsuite_base (pet_uuid, owner, name, level, kills) VALUES(?, ?, ?, ?, ?);");
        try {
            for (Pet pet : new HashSet<Pet>(pets.values())) {
                plugin.debug("Adding query to update '" + pet.getOwner() + "." + pet.getName() + "'");
                statement.setString(1, pet.getUniqueId().toString());
                statement.setString(2, pet.getOwner());
                statement.setString(3, pet.getName());
                statement.setInt(4, pet.getLevel());
                statement.setInt(5, pet.getKills());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
        }
        database.save();
    }

    /**
     * Gets a copy of all the currently registered pets.
     * @return All of the currently registered pets.
     */
    public Set<Pet> getAllPets() {
        return new HashSet<Pet>(pets.values());
    }

    public Set<Pet> getPets(CommandSender sender) {
        Set<Pet> temp = new HashSet<Pet>();
        for (Pet pet : pets.values()) {
            if (pet.getOwner().equals(sender.getName())) {
                temp.add(pet);
            }
        }
        return temp;
    }

    /**
     * Uses the given entity's unique id to fetch a pet object
     * @param tamed The entity whose unique ID we're checking
     * @return The pet, if applicable. Otherwise null.
     */
    public Pet getPet(Entity tamed) {
        addPet((LivingEntity) tamed);
        return pets.get(tamed.getUniqueId());
    }

    /**
     * Attempts to match a pet by the given UUID or name
     * @param id The UUID or name of the pet
     * @return A first pet matching all or part of the specified UUID or name, or null
     */
    public Set<Pet> matchPet(String id) {
        Set<Pet> temp = new HashSet<Pet>();
        for (Pet pet : pets.values()) {
            if (pet.getUniqueId().toString().contains(id) || pet.getName().equalsIgnoreCase(id)) {
                temp.add(pet);
            }
        }
        return temp;
    }

    /**
     * Adds the specified pet to the pets list.
     * @param tamed The entity handle of the pet we're adding to the pets list.
     */
    public void addPet(LivingEntity tamed) {
        if (pets.containsKey(tamed.getUniqueId())) {
            return;
        }
        String name, owner = ((Tameable) tamed).getOwner().getName();
        if (tamed instanceof Wolf) {
            name = "dog";
        } else if (tamed instanceof Ocelot) {
            name = "cat";
        } else {
            name = "unknown";
        }
        int level = 1, kills = 0;
        PreparedStatement statement = database.prepare("SELECT * FROM petsuite_base WHERE pet_uuid = ? LIMIT 1;");
        try {
            plugin.debug("Checking database for entry with UUID = " + tamed.getUniqueId().toString() + "...");
            statement.setString(1, tamed.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                name = result.getString("name");
                owner = result.getString("owner");
                level = result.getInt("level");
                kills = result.getInt("kills");
                plugin.debug("Found pet '" + owner + "." + name + "'!");
            }
            result.close();
        } catch (SQLException e) {
            return;
        }
        Pet pet = new Pet(plugin, tamed, name, owner, level, kills);
        pets.put(pet.getUniqueId(), pet);
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

    /**
     * Checks whether the specified kill count is enough to level a pet up
     * @param level The current level of the pet
     * @param kills The current kill count of the pet
     * @return true if the pet's kill count is high enough, otherwise false
     */
    public boolean nextLevel(int level, int kills) {
        int next = plugin.getConfig().getInt("levels." + (level+1));
        plugin.debug("Current (Level " + level + "): " + kills);
        plugin.debug("Next (Level " + (level+1) + "): " + next);
        return kills >= next;
    }

}
