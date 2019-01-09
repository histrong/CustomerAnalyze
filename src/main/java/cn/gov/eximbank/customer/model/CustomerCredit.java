package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class CustomerCredit {

    @Id
    private String customerId;

    private String credit;

    private Date startDate;

    private Date endDate;

    protected CustomerCredit() {

    }

    public CustomerCredit(String customerId, String credit, Date startDate, Date endDate) {
        this.customerId = customerId;
        this.credit = credit;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCredit() {
        return credit;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
