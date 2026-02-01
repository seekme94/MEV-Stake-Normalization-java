package simulation;

public class Entity {
    public String name;
    public int totalStake;
    public int effectiveStake;
    public double mevEarned;

    public Entity(String name, int stake) {
        this.name = name;
        this.totalStake = stake;
        this.effectiveStake = stake;
        this.mevEarned = 0.0;
    }
}
