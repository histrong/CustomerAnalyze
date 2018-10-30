package cn.gov.eximbank.customer.model;

public class Deal {

    private String account;

    private double remaining;

    private String customerAccount;

    public Deal(String account, double remaining, String customerAccount) {
        this.account = account;
        this.remaining = remaining;
        this.customerAccount = customerAccount;
    }

    public String getAccount() {
        return account;
    }

    public double getRemaining() {
        return remaining;
    }

    public String getCustomerAccount() {
        return customerAccount;
    }
}
