package kiet.mevsimulator.simulation;

public enum EntityType {
    WHALE("Whale"),
    SHARK("Shark"),
    DOLPHIN("Dolphin"),
    FISH("Fish"),
    OCTOPUS("Octopus"),
    CRAB("Crab"),
    SHRIMP("Shrimp");

    private final String displayName;

    EntityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EntityType fromString(String text) {
        for (EntityType b : EntityType.values()) {
            if (b.displayName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
