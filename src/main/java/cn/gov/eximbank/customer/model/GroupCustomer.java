package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class GroupCustomer {

    @Id
    private String id;

    private String name;

    protected GroupCustomer() {
    }

    public GroupCustomer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
