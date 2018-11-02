package cn.gov.eximbank.customer.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Customer {

    @Id
    private String id;

    private String name;

    private String groupId;

    private String scale;

    private String branch;

    private Date relationshipDate;

    private Date firstDealDate;

    private Date lastCreditDate;

    private Date lastDealClearDate;

    private String manager;

    private int inSystem;

    protected Customer() {
    }

    public Customer(String id, String name, String  groupId, String scale, String branch,
                    Date relationshipDate, Date firstDealDate, Date lastCreditDate,
                    Date lastDealClearDat, String manager, int inSystem) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.scale = scale;
        this.branch = toBranch(branch);
        this.relationshipDate = relationshipDate;
        this.firstDealDate = firstDealDate;
        this.lastCreditDate = lastCreditDate;
        this.lastDealClearDate = lastDealClearDat;
        this.manager = manager;
        this.inSystem = inSystem;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getScale() {
        return scale;
    }

    public String getBranch() {
        return branch;
    }

    public Date getRelationshipDate() {
        return relationshipDate;
    }

    public Date getFirstDealDate() {
        return firstDealDate;
    }

    public Date getLastCreditDate() {
        return lastCreditDate;
    }

    public Date getLastDealClearDate() {
        return lastDealClearDate;
    }

    public String getManager() {
        return manager;
    }

    public boolean isInSystem() {
        return inSystem == 1;
    }

    private String toBranch(String branch) {
        if (branch.contains("分行")) {
            return branch;
        }
        if (branch.contains("公司")) {
            return "公司客户部";
        }
        if (branch.contains("交通")) {
            return "交通运输融资部";
        }
        if (branch.contains("铁路")) {
            return "铁路电力融资部";
        }
        if (branch.contains("主权") || branch.contains("优惠")) {
            return "主权客户部";
        }
        if (branch.contains("转贷")) {
            return "转贷部";
        }
        if (branch.contains("投资")) {
            return "投资管理部";
        }
        else {
            return branch;
        }
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
