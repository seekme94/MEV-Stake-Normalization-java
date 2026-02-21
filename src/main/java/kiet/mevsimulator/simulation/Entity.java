package kiet.mevsimulator.simulation;

public class Entity {

    public String name;

    public EntityType type;

    public int totalStake;

    public int effectiveStake;

    public double mevEarned;

    public double normalizedMevEarned;

    public String blockchainAddress;

    public double totalMevEarned;

    public Entity(String name, EntityType type, int stake) {
        this.name = name;
        this.type = type;
        this.totalStake = stake;
        this.effectiveStake = stake;
        this.mevEarned = 0.0;
        this.normalizedMevEarned = 0.0;
        this.blockchainAddress = "";
        this.totalMevEarned = 0;
    }

    public Entity(Entity other) {
        this.name = other.name;
        this.type = other.type;
        this.totalStake = other.totalStake;
        this.effectiveStake = other.effectiveStake;
        this.mevEarned = other.mevEarned;
        this.normalizedMevEarned = other.normalizedMevEarned;
        this.blockchainAddress = other.blockchainAddress;
        this.totalMevEarned = other.totalMevEarned;
    }

    public boolean isValidator() {
        return this.type != EntityType.SHRIMP;
    }
}
