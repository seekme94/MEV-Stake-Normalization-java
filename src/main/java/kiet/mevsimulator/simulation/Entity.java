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
}
