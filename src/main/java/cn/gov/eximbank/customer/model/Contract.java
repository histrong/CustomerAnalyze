package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Contract {

    @Id
    private String id;

    private String customerId;

    protected Contract() {}

    public Contract(String id, String customerId) {
        this.id = id;
        this.customerId = customerId;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }
}
