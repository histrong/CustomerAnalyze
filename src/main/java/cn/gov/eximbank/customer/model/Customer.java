package cn.gov.eximbank.customer.model;

public class Customer {

    private String account;

    private String name;

    private String scale;

    private String branch;

    public Customer(String account, String name) {

    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof Customer){
            Customer other = (Customer) object;
            return this.account.equals(other.account);
        }
        else {
            return false;
        }
    }
}
