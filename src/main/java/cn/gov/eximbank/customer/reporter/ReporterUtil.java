package cn.gov.eximbank.customer.reporter;

import cn.gov.eximbank.customer.util.CustomerUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReporterUtil {

    public static final String[] credits = new String[] {"未知", "AAA", "AA+", "AA", "AA-", "A+", "A", "A-", "BBB", "BB",
                "B", "CCC", "CC", "C", "D", "不予评级", "过期"};

    public static String[] getPeriods() {
        return new String[] {"201809", "201806", "201803", "201712",
                "201709", "201706", "201703"};
    }

    public static String ownershipMapping(String ownership) {
        if (ownership.equals("外商控股") || ownership.equals("港澳台绝对商控股") || ownership.equals("外商绝对控股")
                || ownership.equals("外商相对控股") || ownership.equals("港澳台相对商控股") || ownership.equals("港澳台商控股")) {
            return "外资";
        }
        else if (ownership.equals("国有绝对控股") || ownership.equals("国有相对控股") || ownership.equals("国有控股")) {
            return "国有";
        }
        else if (ownership.equals("集体控股") || ownership.equals("集体相对控股") || ownership.equals("集体绝对控股")) {
            return "集体";
        }
        else if (ownership.equals("私人相对控股") || ownership.equals("私人绝对控股") || ownership.equals("私人控股")) {
            return "民营";
        }
        else {
            return "未知";
        }
    }

    public static int getScaleIndex(String scale) {
        if (scale.equals("小型") || scale.equals("微型")) {
            return 1;
        }
        else if (scale.equals("中型")) {
            return 2;
        }
        else if (scale.equals("大型")) {
            return 3;
        }
        else {
            return 0;
        }
    }

    public static int getOwnershipIndex(String ownership) {
        if (ownership.equals("国有")) {
            return 1;
        }
        else if (ownership.equals("集体")) {
            return 2;
        }
        else if (ownership.equals("民营")) {
            return 3;
        }
        else if (ownership.equals("外资")) {
            return 4;
        }
        else {
            return 0;
        }
    }

    public static Date getJudgeDate() {
        String dateString = new String("2018-09-30");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(2018 - 1900, 8, 30);
        }
    }

    public static boolean isCreditOutOfDate(Date endDate) {
        Date judgeDate = getJudgeDate();
        if (endDate != null && endDate.before(judgeDate)) {
            return true;
        }
        else {
            return false;
        }
    }

    public static int adaptToCreditIndex(String credit) {
        int creditIndex = 0;
        if (credit.equals("AAA")) {
            creditIndex = 1;
        }
        else if (credit.equals("AA+")) {
            creditIndex = 2;
        }
        else if (credit.equals("AA")) {
            creditIndex = 3;
        }
        else if (credit.equals("AA-")) {
            creditIndex = 4;
        }
        else if (credit.equals("A+")) {
            creditIndex = 5;
        }
        else if (credit.equals("A")) {
            creditIndex = 6;
        }else if (credit.equals("A-")) {
            creditIndex = 7;
        }else if (credit.equals("BBB")) {
            creditIndex = 8;
        }
        else if (credit.equals("BB")) {
            creditIndex = 9;
        }
        else if (credit.equals("B")) {
            creditIndex = 10;
        }
        else if (credit.equals("CCC")) {
            creditIndex = 11;
        }
        else if (credit.equals("CC")) {
            creditIndex = 12;
        }
        else if (credit.equals("C")) {
            creditIndex = 13;
        }
        else if (credit.equals("D")) {
            creditIndex = 14;
        }
        else if (credit.equals("不予评级")) {
            creditIndex = 15;
        }
        else if (credit.equals("过期")) {
            creditIndex = 16;
        }
        return creditIndex;
    }

    public static boolean isEnterpriseCustomer(String customerName, String branch) {
        if (branch.contains("主权")) {
            return false;
        }
        else if (CustomerUtil.isBankCustomer(customerName)) {
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean isEnterpriseCustomerInside(String customerName, String branch, String province) {
        if (isEnterpriseCustomer(customerName, branch)) {
            if (province == null || province.equals("")) {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
}
