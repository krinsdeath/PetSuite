package net.krinsoft.petsuite.skills;

import net.krinsoft.petsuite.PetCore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author krinsdeath
 */
public class SkillManager {
    private PetCore plugin;
    private Map<Integer, PetSkill.SKIN> skin_skills = new HashMap<Integer, PetSkill.SKIN>();
    private Map<Integer, PetSkill.FANG> fang_skills = new HashMap<Integer, PetSkill.FANG>();

    public SkillManager(PetCore instance) {
        plugin = instance;
        for (String key : plugin.getConfig().getConfigurationSection("skills.skins").getKeys(false)) {
            PetSkill.SKIN test = skin_skills.put(plugin.getConfig().getInt("skills.skins." + key), PetSkill.SKIN.forName(key));
            if (test != null) {
                plugin.getLogger().warning("Duplicate skin levels found! " + test.name() + " and " + key);
            }
        }
        for (String key : plugin.getConfig().getConfigurationSection("skills.fangs").getKeys(false)) {
            PetSkill.FANG test = fang_skills.put(plugin.getConfig().getInt("skills.fangs." + key), PetSkill.FANG.forName(key));
            if (test != null) {
                plugin.getLogger().warning("Duplicate fang levels found! " + test.name() + " and " + key);
            }
        }
    }

    /**
     * Gets the highest fang skill based on the level input
     * @param level The level of the pet we're fetching a fang for.
     * @return The fang type if applicable, otherwise null.
     */
    public PetSkill.FANG getHighestFang(int level) {
        int diff = Integer.MAX_VALUE;
        PetSkill.FANG fang = null;
        for (Integer lv : fang_skills.keySet()) {
            if (level - lv >= 0 && level - lv < diff) {
                diff = level - lv;
                fang = fang_skills.get(lv);
            }
        }
        return fang;
    }

    /**
     * Gets the highest skin skill based on the level input
     * @param level The level of the pet we're fetching a skin for.
     * @return The skin type if applicable, otherwise null.
     */
    public PetSkill.SKIN getHighestSkin(int level) {
        int diff = Integer.MAX_VALUE;
        PetSkill.SKIN skin = null;
        for (Integer lv : skin_skills.keySet()) {
            if (level - lv >= 0 && level - lv < diff) {
                diff = level - lv;
                skin = skin_skills.get(lv);
            }
        }
        return skin;
    }

}
