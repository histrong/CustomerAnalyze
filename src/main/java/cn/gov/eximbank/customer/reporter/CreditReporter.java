package cn.gov.eximbank.customer.reporter;

import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.util.CustomerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CreditReporter {

    private static Logger logger = LoggerFactory.getLogger(CreditReporter.class);

    private ValidCustomerStateRepository validCustomerStateRepository;

    private CustomerCreditRepository customerCreditRepository;

    private ContractStateRepository contractStateRepository;

    private static String[] credits = new String[] {"未知", "AAA", "AA+", "AA", "AA-", "A+", "A", "A-", "BBB", "BB",
            "B", "CCC", "CC", "C", "D", "不予评级"};
    private static String[] qualityLevels = new String[] {"正常", "关注", "次级", "可疑", "损失"};

    public CreditReporter(ValidCustomerStateRepository validCustomerStateRepository,
                          CustomerCreditRepository customerCreditRepository,
                          ContractStateRepository contractStateRepository) {
        this.validCustomerStateRepository = validCustomerStateRepository;
        this.customerCreditRepository = customerCreditRepository;
        this.contractStateRepository = contractStateRepository;
    }

    public void reportCustomerCredits() {
        String latestPeriod = ReporterUtil.getPeriods()[0];
        reportCustomerCreditsWithPeriod(latestPeriod);
    }

    public void reportCreditQuality() {
        String latestPeriod = ReporterUtil.getPeriods()[0];
        reportCreditQualityWithPeriod(latestPeriod);
    }

    private void reportCustomerCreditsWithPeriod(String latestPeriod) {
        CustomerCreditDetail[] totalCustomerCreditDetails = new CustomerCreditDetail[16];
        CustomerCreditDetail[] insideCustomerCreditDetails = new CustomerCreditDetail[16];
        CustomerCreditDetail[] outsideCustomerCreditDetails = new CustomerCreditDetail[16];
        for (int i = 0; i != credits.length; ++i) {
            totalCustomerCreditDetails[i] = new CustomerCreditDetail();
            insideCustomerCreditDetails[i] = new CustomerCreditDetail();
            outsideCustomerCreditDetails[i] = new CustomerCreditDetail();
        }

        List<ValidCustomerState> validCustomerStates = validCustomerStateRepository.findByPeriod(latestPeriod);
        for (ValidCustomerState validCustomerState :validCustomerStates) {
            if (isValidCustomer(validCustomerState.getBranch(), validCustomerState.getCustomerName())) {
                String credit = "未知";
                Optional<CustomerCredit> customerCreditInDB
                        = customerCreditRepository.findById(validCustomerState.getCustomerId());
                if (customerCreditInDB.isPresent()) {
                    credit = customerCreditInDB.get().getCredit();
                }
                handleValidCustomerCredit(credit, totalCustomerCreditDetails, validCustomerState.getRemaining());
                if (validCustomerState.getProvince() == null || validCustomerState.getProvince().equals("")) {
                    handleValidCustomerCredit(credit, outsideCustomerCreditDetails, validCustomerState.getRemaining());
                } else {
                    handleValidCustomerCredit(credit, insideCustomerCreditDetails, validCustomerState.getRemaining());
                }
            }
        }

        displayCustomerCreditDetailWithPeriod("201809", "境内客户", insideCustomerCreditDetails);
        displayCustomerCreditDetailWithPeriod("201809", "境外客户", outsideCustomerCreditDetails);
        displayCustomerCreditDetailWithPeriod("201809", "总计", totalCustomerCreditDetails);
    }

    private void displayCustomerCreditDetailWithPeriod(String period, String tag, CustomerCreditDetail[] customerCreditDetails) {
        System.out.println();
        System.out.println(period + ";" + tag);
        System.out.println("评级;数量;余额");
        for (int i = 0; i != credits.length; ++i) {
            System.out.println(credits[i] + ";" + customerCreditDetails[i].getCount() + ";" + customerCreditDetails[i].getRemaining());
        }
    }

    private void handleValidCustomerCredit(String credit, CustomerCreditDetail[] customerCreditDetails, double remaining) {
        int creditIndex = adaptCreditToIndex(credit);
        customerCreditDetails[creditIndex].increaseCount();
        customerCreditDetails[creditIndex].addRemaining(remaining);
    }

    private static int adaptCreditToIndex(String credit) {
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
        return creditIndex;
    }

    private void reportCreditQualityWithPeriod(String period) {
        double[][] creditQualityRemainings = new double[5][16];
        for (int i = 0; i != 5; ++i) {
            creditQualityRemainings[i] = new double[16];
        }
        List<ContractState> contractStates = contractStateRepository.findByPeriod(period);
        for (ContractState contractState : contractStates) {
            ValidCustomerState validCustomerState = validCustomerStateRepository
                    .findByPeriodAndCustomerId(period, contractState.getCustomerId());
            if (validCustomerState != null && isValidCustomer(validCustomerState.getBranch(), validCustomerState.getCustomerName())) {
                int qualityLevelIndex = contractState.getQualityLevel() - 1;
                String credit = "未知";
                Optional<CustomerCredit> customerCreditInDB = customerCreditRepository.findById(contractState.getCustomerId());
                if (customerCreditInDB.isPresent()) {
                    credit = customerCreditInDB.get().getCredit();
                }
                int creditIndex = adaptCreditToIndex(credit);
                creditQualityRemainings[qualityLevelIndex][creditIndex] += contractState.getRemaining();
            }
        }

        System.out.println(period);
        System.out.println("分类;正常;关注;次级;可疑;损失");
        for (int i = 0; i != credits.length; ++i) {
            System.out.print(credits[i] + ";"
                    + creditQualityRemainings[0][i] + ";"
                    + creditQualityRemainings[1][i] + ";"
                    + creditQualityRemainings[2][i] + ";"
                    + creditQualityRemainings[3][i] + ";"
                    + creditQualityRemainings[4][i] + ";");
            System.out.print("\n");
        }
    }

    private boolean isValidCustomer(String branch, String customerName) {
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
}
