package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    private String id;

    private String name;

    private String scale;

    private String branch;

    protected Customer() {}

    public Customer(String account, String name) {

    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof Customer){
            Customer other = (Customer) object;
            return this.id.equals(other.id);
        }
        else {
            return false;
        }
    }
}
