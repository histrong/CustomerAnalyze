package cn.gov.eximbank.customer.model;

import cn.gov.eximbank.customer.util.CustomerUtil;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Customer {

    @Id
    private String id;

    private String name;

    private String groupId;

    private String branch;

    private String manager;

    private Date relationshipDate;

    private Date firstDealDate;

    private Date lastCreditDate;

    private Date lastDealClearDate;

    protected Customer() {
    }

    public Customer(String id, String name, String  groupId, String branch,
                    String manager, Date relationshipDate, Date firstDealDate,
                    Date lastCreditDate, Date lastDealClearDate) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.branch = CustomerUtil.normalizeBranchName(branch);
        this.manager = manager;
        this.relationshipDate = relationshipDate;
        this.firstDealDate = firstDealDate;
        this.lastCreditDate = lastCreditDate;
        this.lastDealClearDate = lastDealClearDate;
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

    public String getBranch() {
        return branch;
    }

    public String getManager() {
        return manager;
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
