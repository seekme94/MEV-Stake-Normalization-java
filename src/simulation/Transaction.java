package simulation;

public class Transaction {
    public double value;
    public boolean isMEV;

    public Transaction(double value, boolean isMEV) {
        this.value = value;
        this.isMEV = isMEV;
    }
}
