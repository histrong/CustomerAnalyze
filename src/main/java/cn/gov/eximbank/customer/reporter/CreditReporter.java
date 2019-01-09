package cn.gov.eximbank.customer.reporter;

import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.util.CustomerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CreditReporter {

    private static Logger logger = LoggerFactory.getLogger(CreditReporter.class);

    private ValidCustomerStateRepository validCustomerStateRepository;

    private CustomerCreditRepository customerCreditRepository;

    private ContractStateRepository contractStateRepository;

    private static String[] qualityLevels = new String[] {"正常", "关注", "次级", "可疑", "损失"};
    private static String[] scales = new String[] {"未知", "小微", "中型", "大型"};
    private static String[] ownerships = new String[] {"未知", "国有", "集体", "民营", "外资"};

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

    public void reportCustomerCreditsInScalesAndOwnerShipWithPeriod(String latestPeriod) {
        int[][] scaleCreditDetails = new int[scales.length][ReporterUtil.credits.length];
        for (int i = 0; i != scales.length; ++i) {
            scaleCreditDetails[i] = new int[ReporterUtil.credits.length];
        }
        int[][] ownershipCreditDetails = new int[ownerships.length][ReporterUtil.credits.length];
        for (int i = 0; i != ownerships.length; ++i) {
            ownershipCreditDetails[i] = new int[ReporterUtil.credits.length];
        }

        List<ValidCustomerState> validCustomerStates = validCustomerStateRepository.findByPeriod(latestPeriod);
        for (ValidCustomerState validCustomerState : validCustomerStates) {
            if (ReporterUtil.isEnterpriseCustomerInside(validCustomerState.getCustomerName(),
                    validCustomerState.getBranch(), validCustomerState.getProvince())) {
                continue;
            }
            else {
                handleValidCustomerCredit(validCustomerState, scaleCreditDetails, ownershipCreditDetails);
            }
        }

        System.out.println("规模;未知;AAA;AA+;AA;AA-;A+;A;A-;BBB;BB;B;CCC;CC;C;D;不予评级");
        for (int i = 0; i != scales.length; ++i) {
            System.out.print(scales[i]);
            for (int j = 0; j != ReporterUtil.credits.length; ++j) {
                System.out.print(";" + scaleCreditDetails[i][j]);
            }
            System.out.print("\n");
        }

        System.out.println();

        System.out.println("所有制;未知;AAA;AA+;AA;AA-;A+;A;A-;BBB;BB;B;CCC;CC;C;D;不予评级");
        for (int i = 0; i != ownerships.length; ++i) {
            System.out.print(ownerships[i]);
            for (int j = 0; j != ReporterUtil.credits.length; ++j) {
                System.out.print(";" + ownershipCreditDetails[i][j]);
            }
            System.out.print("\n");
        }
    }

    public void reportCreditDetailsWithBranch(String period) {
        Map<String, CreditDistribution> creditDistributions = new HashMap<String, CreditDistribution>();
        List<ValidCustomerState> validCustomerStates = validCustomerStateRepository.findByPeriod(period);
        for (ValidCustomerState validCustomerState : validCustomerStates) {
//            if (ReporterUtil.isEnterpriseCustomerInside(validCustomerState.getCustomerName(),
//                    validCustomerState.getBranch(), validCustomerState.getProvince())) {
            if (ReporterUtil.isEnterpriseCustomer(validCustomerState.getCustomerName(), validCustomerState.getBranch())) {
                handleValidCustomerCredit(validCustomerState, creditDistributions);
            }
        }
        System.out.println("经营单位;未知;AAA;AA+;AA;AA-;A+;A;A-;BBB;BB;B;CCC;CC;C;D;不予评级;过期");
        for (String branch : creditDistributions.keySet()) {
            System.out.print(branch);
            for (int i = 0; i != ReporterUtil.credits.length; ++i) {
                System.out.print(";" + creditDistributions.get(branch).getCreditCount(ReporterUtil.credits[i]));
            }
            System.out.println();
        }
    }

    private void handleValidCustomerCredit(ValidCustomerState validCustomerState, Map<String, CreditDistribution> creditDistributions) {
        String branch = validCustomerState.getBranch();
        if (!creditDistributions.containsKey(branch)) {
            creditDistributions.put(branch, new CreditDistribution());
        }
        Optional<CustomerCredit> customerCreditInDB = customerCreditRepository.findById(validCustomerState.getCustomerId());
        Date judgeDate = ReporterUtil.getJudgeDate();
        if (customerCreditInDB.isPresent()) {
            CustomerCredit customerCredit = customerCreditInDB.get();
            String credit = getValidCredit(customerCredit);
            creditDistributions.get(branch).addCreditCount(credit);
        }
    }

    private void handleValidCustomerCredit(ValidCustomerState validCustomerState, int[][] scaleCreditDetails, int[][] ownershipCreditDetails) {
        String credit = "未知";
        Optional<CustomerCredit> customerCreditInDB
                = customerCreditRepository.findById(validCustomerState.getCustomerId());
        if (customerCreditInDB.isPresent()) {
            credit = customerCreditInDB.get().getCredit();
        }
        int creditIndex = adaptCreditToIndex(credit);
        int scaleIndex = ReporterUtil.getScaleIndex(validCustomerState.getScale());
        int ownershipIndex = ReporterUtil.getOwnershipIndex(ReporterUtil.ownershipMapping(validCustomerState.getOwnership()));
        ++scaleCreditDetails[scaleIndex][creditIndex];
        ++ownershipCreditDetails[ownershipIndex][creditIndex];
    }

    private void reportCustomerCreditsWithPeriod(String latestPeriod) {
        CustomerCreditDetail[] totalCustomerCreditDetails = new CustomerCreditDetail[ReporterUtil.credits.length];
        CustomerCreditDetail[] insideCustomerCreditDetails = new CustomerCreditDetail[ReporterUtil.credits.length];
        CustomerCreditDetail[] outsideCustomerCreditDetails = new CustomerCreditDetail[ReporterUtil.credits.length];
        for (int i = 0; i != ReporterUtil.credits.length; ++i) {
            totalCustomerCreditDetails[i] = new CustomerCreditDetail();
            insideCustomerCreditDetails[i] = new CustomerCreditDetail();
            outsideCustomerCreditDetails[i] = new CustomerCreditDetail();
        }

        List<ValidCustomerState> validCustomerStates = validCustomerStateRepository.findByPeriod(latestPeriod);
        for (ValidCustomerState validCustomerState :validCustomerStates) {
            if (ReporterUtil.isEnterpriseCustomer(validCustomerState.getCustomerName(), validCustomerState.getBranch())) {
                String credit = "未知";
                Optional<CustomerCredit> customerCreditInDB
                        = customerCreditRepository.findById(validCustomerState.getCustomerId());
                if (customerCreditInDB.isPresent()) {
                    credit = getValidCredit(customerCreditInDB.get());
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
        for (int i = 0; i != ReporterUtil.credits.length; ++i) {
            System.out.println(ReporterUtil.credits[i] + ";" + customerCreditDetails[i].getCount() + ";" + customerCreditDetails[i].getRemaining());
        }
    }

    private void handleValidCustomerCredit(String credit, CustomerCreditDetail[] customerCreditDetails, double remaining) {
        int creditIndex = adaptCreditToIndex(credit);
        customerCreditDetails[creditIndex].increaseCount();
        customerCreditDetails[creditIndex].addRemaining(remaining);
    }

    private static int adaptCreditToIndex(String credit) {
        return ReporterUtil.adaptToCreditIndex(credit);
    }

    private void reportCreditQualityWithPeriod(String period) {
        double[][] creditQualityRemainings = new double[5][ReporterUtil.credits.length];
        for (int i = 0; i != 5; ++i) {
            creditQualityRemainings[i] = new double[ReporterUtil.credits.length];
        }
        List<ContractState> contractStates = contractStateRepository.findByPeriod(period);
        for (ContractState contractState : contractStates) {
            ValidCustomerState validCustomerState = validCustomerStateRepository
                    .findByPeriodAndCustomerId(period, contractState.getCustomerId());
            if (validCustomerState != null
                    && ReporterUtil.isEnterpriseCustomerInside(validCustomerState.getCustomerName(),
                    validCustomerState.getBranch(), validCustomerState.getProvince())) {
                int qualityLevelIndex = contractState.getQualityLevel() - 1;
                String credit = "未知";
                Optional<CustomerCredit> customerCreditInDB = customerCreditRepository.findById(contractState.getCustomerId());
                if (customerCreditInDB.isPresent()) {
                    credit = getValidCredit(customerCreditInDB.get());
                }
                int creditIndex = adaptCreditToIndex(credit);
                creditQualityRemainings[qualityLevelIndex][creditIndex] += contractState.getRemaining();
//                if (credit.equals("AA+") && qualityLevelIndex > 1) {
//                    System.out.println(validCustomerState.getCustomerId());
//                }
            }
        }

        System.out.println(period);
        System.out.println("分类;正常;关注;次级;可疑;损失");
        for (int i = 0; i != ReporterUtil.credits.length; ++i) {
            System.out.print(ReporterUtil.credits[i] + ";"
                    + creditQualityRemainings[0][i] + ";"
                    + creditQualityRemainings[1][i] + ";"
                    + creditQualityRemainings[2][i] + ";"
                    + creditQualityRemainings[3][i] + ";"
                    + creditQualityRemainings[4][i] + ";");
            System.out.print("\n");
        }
    }

    public static String getValidCredit(CustomerCredit customerCredit) {
        String credit = customerCredit.getCredit();
        if (credit.equals("未知") || credit.equals("不予评级")) {
            return credit;
        }
        else {
            if (ReporterUtil.isCreditOutOfDate(customerCredit.getEndDate())) {
                credit = "过期";
            }
            return credit;
        }
    }
}
