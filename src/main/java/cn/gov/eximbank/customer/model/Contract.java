package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Contract {

    @Id
    private String id;

    private double remaining;

    private String customerId;

    protected Contract() {}

    public Contract(String id, double remaining, String customerId) {
        this.id = id;
        this.remaining = remaining;
        this.customerId = customerId;
    }

    public String getId() {
        return id;
    }

    public double getRemaining() {
        return remaining;
    }

    public String getCustomerId() {
        return customerId;
    }
}
