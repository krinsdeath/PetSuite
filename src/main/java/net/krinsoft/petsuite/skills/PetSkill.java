package net.krinsoft.petsuite.skills;

/**
 * @author krinsdeath
 */
public enum PetSkill {
    ;
    public enum FANG {
        STONE_FANG("Stone Fang", "Increases damage output by 25%."),
        IRON_FANG("Iron Fang", "Increases damage output by 50%."),
        DIAMOND_FANG("Diamond Fang", "Increases damage output by 75%.");

        private String name;
        private String description;

        FANG(String name, String desc) {
            this.name           = name;
            this.description    = desc;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public static PetSkill.FANG forName(String name) throws TypeNotPresentException {
            for (PetSkill.FANG fang : values()) {
                if (fang.name().equalsIgnoreCase(name)) {
                    return fang;
                }
            }
            throw new TypeNotPresentException(name, new Throwable("Unknown Fang type."));
        }
    }
    public enum SKIN {
        STONE_SKIN("Stone Skin", "Mitigates 25% of incoming damage."),
        IRON_SKIN("Iron Skin", "Mitigates 50% of incoming damage."),
        DIAMOND_SKIN("Diamond Skin", "Mitigates 75% of incoming damage.");

        private String name;
        private String description;

        SKIN(String name, String desc) {
            this.name           = name;
            this.description    = desc;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public static PetSkill.SKIN forName(String name) throws TypeNotPresentException {
            for (PetSkill.SKIN skin : values()) {
                if (skin.name().equalsIgnoreCase(name)) {
                    return skin;
                }
            }
            throw new TypeNotPresentException(name, new Throwable("Unknown Skin type."));
        }
    }



}
