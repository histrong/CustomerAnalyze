package cn.gov.eximbank.customer.reporter;

public class ReporterUtil {

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

}
