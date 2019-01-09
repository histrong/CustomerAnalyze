package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CustomerContribution {

    public enum EContributionType {
        Intermediate,
        DemandDeposit,
        TimeDeposit,
        Loan
    }

    @Id
    private String customerId;

    private String customerName;

    private double intermediateContribution;

    private double demandDepositContribution;

    private double timeDepositContribution;

    private double loanContribution;

    private String scale;

    private String ownership;

    private String industry;

    private String branch;

    private String province;

    protected CustomerContribution() {

    }

    public CustomerContribution(String customerId,
                                String customerName,
                                String scale,
                                String ownership,
                                String industry,
                                String branch,
                                String province) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.scale = scale;
        this.ownership = ownership;
        this.industry = industry;
        this.branch = branch;
        this.province = province;
    }

    public CustomerContribution(ValidCustomerState validCustomerState) {
        this(validCustomerState.getCustomerId(), validCustomerState.getCustomerName(),
                validCustomerState.getScale(), validCustomerState.getOwnership(),
                validCustomerState.getIndustry(), validCustomerState.getBranch(), validCustomerState.getProvince());
    }

    public void addContribution(EContributionType type, double contribution) {
        if (type.equals(EContributionType.Intermediate)) {
            this.intermediateContribution += contribution;
        }
        else if (type.equals(EContributionType.DemandDeposit)) {
            this.demandDepositContribution += contribution;
        }
        else if (type.equals(EContributionType.TimeDeposit)) {
            this.timeDepositContribution += contribution;
        }
        else if (type.equals(EContributionType.Loan)) {
            this.loanContribution += contribution;
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() { return customerName; }

    public double getIntermediateContribution() {
        return intermediateContribution;
    }

    public double getDemandDepositContribution() {
        return demandDepositContribution;
    }

    public double getTimeDepositContribution() {
        return timeDepositContribution;
    }

    public double getLoanContribution() {
        return loanContribution;
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
}
