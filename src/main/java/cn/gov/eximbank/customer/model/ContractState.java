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

    protected ContractState() {

    }

    public ContractState(String contractId, String period, double remaining, String qualityLevelStr) {
        this.contractId = contractId;
        this.period = period;
        this.remaining = remaining;
        this.qualityLevel = toLevel(qualityLevelStr);
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
}
