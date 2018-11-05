package cn.gov.eximbank.customer.reporter;

class CustomerDetail {

    private int bankCustomer;

    private int governmentCustomer;

    private int insideCustomer;

    private int outsideCustomer;

    private int[] scaleCustomer;

    private int inGroupCustomer;

    private int[] ownershipCustomer;

    public CustomerDetail() {
        this.bankCustomer = 0;
        this.governmentCustomer = 0;
        this.insideCustomer = 0;
        this.outsideCustomer = 0;
        this.scaleCustomer = new int[] {0, 0, 0, 0};
        this.inGroupCustomer = 0;
        this.ownershipCustomer = new int[] {0, 0, 0, 0, 0};
    }

    public int getBankCustomer() {
        return bankCustomer;
    }

    public int getGovernmentCustomer() {
        return governmentCustomer;
    }

    public int getInsideCustomer() {
        return insideCustomer;
    }

    public int getOutsideCustomer() {
        return outsideCustomer;
    }

    public int[] getScaleCustomer() {
        return scaleCustomer;
    }

    public int getInGroupCustomer() {
        return inGroupCustomer;
    }

    public int[] getOwnershipCustomer() {
        return ownershipCustomer;
    }

    public void increaseBankCustomer() {
        ++this.bankCustomer;
    }

    public void increaseGovernmentCustomer() {
        ++this.governmentCustomer;
    }

    public void increaseInsideCustomer() {
        ++this.insideCustomer;
    }

    public void increaseOutsideCustomer() {
        ++this.outsideCustomer;
    }

    public void increaseScaleCustomer(int index) {
        ++this.scaleCustomer[index];
    }

    public void increaseInGroupCustomer() {
        ++this.inGroupCustomer;
    }

    public void increaseOwnershipCustomer(int index) {
        ++this.ownershipCustomer[index];
    }
}
