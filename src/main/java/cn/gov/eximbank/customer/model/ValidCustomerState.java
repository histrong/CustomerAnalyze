package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ValidCustomerState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String customerId;

    private String customerName;

    private String period;

    private double remaining;

    private String scale;

    private String ownership;

    private String industry;

    private String branch;

    private String province;

    protected ValidCustomerState() {}

    public ValidCustomerState(String customerId, String customerName, String period,
                              double remaining, String scale, String ownership,
                              String industry, String branch, String province) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.period = period;
        this.remaining = remaining;
        this.scale = scale;
        this.ownership = ownership;
        this.industry = industry;
        this.branch = branch;
        this.province = province;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPeriod() {
        return period;
    }

    public double getRemaining() {
        return remaining;
    }

    public String getScale() {
        return scale;
    }

    public String getOwnership() {
        return ownership;
    }

    public String getIndustry() {
        return industry;
    }

    public String getBranch() {
        return branch;
    }

    public String getProvince() {
        return province;
    }

    public boolean isForeign() {
        return province == null || province.equals("");
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }
}
