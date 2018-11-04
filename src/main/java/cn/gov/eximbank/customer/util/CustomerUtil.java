package cn.gov.eximbank.customer.util;

import cn.gov.eximbank.customer.model.Customer;

public class CustomerUtil {

    public static String normalizeBranchName(String branch) {
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

    public static boolean isBankCustomer(String customerName) {
        if (customerName.contains("银行") || customerName.contains("bank")
                || customerName.contains("BANK") || customerName.contains("Bank")) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isForeignDepartment(Customer customer) {
        if (customer.getBranch().equals("主权客户户")) {
            return true;
        }
        else if (customer.getName().contains("部") || customer.getName().contains("Department")) {
            return true;
        }
        else {
            return false;
        }
    }
}
