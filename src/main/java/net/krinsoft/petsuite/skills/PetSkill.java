package net.krinsoft.petsuite.skills;

/**
 * @author krinsdeath
 */
public enum PetSkill {
    STONE_SKIN("Mitigates 25% of incoming damage."),
    IRON_SKIN("Mitigates 50% of incoming damage."),
    DIAMOND_SKIN("Mitigates 75% of incoming damage.");

    private String description;

    PetSkill(String desc) {
        this.description = desc;
    }

    public static PetSkill forName(String name) throws TypeNotPresentException {
        for (PetSkill skill : values()) {
            if (skill.name().equalsIgnoreCase(name)) {
                return skill;
            }
        }
        throw new TypeNotPresentException(name, new Throwable("Unknown PetSkill."));
    }
}
