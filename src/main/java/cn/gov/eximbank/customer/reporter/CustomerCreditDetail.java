package cn.gov.eximbank.customer.reporter;

public class CustomerCreditDetail {

    private int count;

    private double remaining;

    public CustomerCreditDetail() {
        this.count = 0;
        this.remaining = 0;
    }

    public int getCount() {
        return count;
    }

    public double getRemaining() {
        return remaining;
    }

    public void increaseCount() {
        ++this.count;
    }

    public void addRemaining(double remaining) {
        this.remaining += remaining;
    }
}
