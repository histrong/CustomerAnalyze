package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ContractState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String contractId;

    private String period;

    private double remaining;

    private int qualityLevel;

    private String customerId;

    private String scale;

    private String ownership;

    private String industry;

    private String province;

    protected ContractState() {

    }

    public ContractState(String contractId, String period, double remaining, String qualityLevelStr,
                         String customerId, String scale, String ownership, String industry, String province) {
        this.contractId = contractId;
        this.period = period;
        this.remaining = remaining;
        this.qualityLevel = toLevel(qualityLevelStr);
        this.customerId = customerId;
        this.scale = scale;
        this.ownership = ownership;
        this.industry = industry;
        this.province = province;
    }

    private int toLevel(String qualityLevelStr) {
        if (qualityLevelStr.equals("正常")) {
            return 1;
        }
        else if (qualityLevelStr.equals("关注")) {
            return 2;
        }
        else if (qualityLevelStr.equals("次级")) {
            return 3;
        }
        else if (qualityLevelStr.equals("可疑")) {
            return 4;
        }
        else if (qualityLevelStr.equals("损失")) {
            return 5;
        }
        else {
            return 0;
        }
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public Long getId() {
        return id;
    }

    public String getContractId() {
        return contractId;
    }

    public String getPeriod() {
        return period;
    }

    public double getRemaining() {
        return remaining;
    }

    public int getQualityLevel() {
        return qualityLevel;
    }

    public String getCustomerId() {
        return customerId;
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

    public String getProvince() {
        return province;
    }
}
