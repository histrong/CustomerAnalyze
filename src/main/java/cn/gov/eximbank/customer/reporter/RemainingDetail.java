package cn.gov.eximbank.customer.reporter;

public class RemainingDetail {

    private double bankRemaining;

    private double governmentRemaining;

    private double insideRemaining;

    private double outsideRemaining;

    private double[] scaleRemaining;

    private double inGroupRemaining;

    private double[] ownershipRemaining;

    public RemainingDetail() {
        this.bankRemaining = 0;
        this.governmentRemaining = 0;
        this.insideRemaining = 0;
        this.outsideRemaining = 0;
        this.scaleRemaining = new double[] {0, 0, 0, 0};
        this.inGroupRemaining = 0;
        this.ownershipRemaining = new double[] {0, 0, 0, 0, 0};
    }

    public double getBankRemaining() {
        return bankRemaining;
    }

    public double getGovernmentRemaining() {
        return governmentRemaining;
    }

    public double getInsideRemaining() {
        return insideRemaining;
    }

    public double getOutsideRemaining() {
        return outsideRemaining;
    }

    public double[] getScaleRemaining() {
        return scaleRemaining;
    }

    public double getInGroupRemaining() {
        return inGroupRemaining;
    }

    public double[] getOwnershipRemaining() {
        return ownershipRemaining;
    }

    public void addBankRemaining(double bankRemaining) {
        this.bankRemaining += bankRemaining;
    }

    public void addGovernmentRemaining(double governmentRemaining) {
        this.governmentRemaining += governmentRemaining;
    }

    public void addInsideRemaining(double inGroupRemaining) {
        this.insideRemaining += inGroupRemaining;
    }

    public void addOutsideRemaining(double outsideRemaining) {
        this.outsideRemaining += outsideRemaining;
    }

    public void addScaleRemaining(int index, double scaleRemaining) {
        this.scaleRemaining[index] += scaleRemaining;
    }

    public void addInGroupRemaining(double inGroupRemaining) {
        this.inGroupRemaining += inGroupRemaining;
    }

    public void addOwnershipRemaining(int index, double ownershipRemaining) {
        this.ownershipRemaining[index] += ownershipRemaining;
    }
}
