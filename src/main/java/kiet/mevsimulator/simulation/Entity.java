package kiet.mevsimulator.simulation;

public class Entity {
    public String name;
    public String category;
    public int totalStake;
    public int effectiveStake;
    public double mevEarned;
    public String blockchainAddress;

    public Entity(String name, String category, int stake) {
        this.name = name;
        this.category = category;
        this.totalStake = stake;
        this.effectiveStake = stake;
        this.mevEarned = 0.0;
        this.blockchainAddress = "";
    }
    // inside Entity.java
    public Entity(Entity other) {
        this.name = other.name;
        this.category = other.category;
        this.totalStake = other.totalStake;
        this.effectiveStake = other.effectiveStake;
        this.mevEarned = other.mevEarned;
        this.blockchainAddress = other.blockchainAddress;
    }

}
