package cn.gov.eximbank.customer.model;

import cn.gov.eximbank.customer.util.CustomerUtil;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IntermediateContribution {

    @Id
    private String contractId;

    private String customerId;

    private double contribution;

    private String branch;

    protected IntermediateContribution() {

    }

    public IntermediateContribution(String contractId, String customerId, double contribution, String branch) {
        this.contractId = contractId;
        this.customerId = customerId;
        this.contribution = contribution;
        this.branch = CustomerUtil.normalizeBranchName(branch);
    }

    public String getContractId() {
        return contractId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getContribution() {
        return contribution;
    }

    public String getBranch() { return branch; }
}
