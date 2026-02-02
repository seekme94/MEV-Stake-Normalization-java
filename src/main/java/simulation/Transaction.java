package simulation;

public class Transaction {
    public Entity sender;
    public String to;
    public double value;
    public boolean isMEV;

    public Transaction(Entity sender, String to, double value, boolean isMEV) {
        this.sender = sender;
        this.to = to;
        this.value = value;
        this.isMEV = isMEV;
    }
}
