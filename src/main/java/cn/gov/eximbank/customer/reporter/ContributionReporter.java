package cn.gov.eximbank.customer.reporter;

import cn.gov.eximbank.customer.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ContributionReporter {

    private static Logger logger = LoggerFactory.getLogger(ContributionReporter.class);

    private static String[] scales = new String[] {"未知", "小微", "中型", "大型"};

    private static String[] ownerships = new String[] {"未知", "国有", "集体", "民营", "外资"};

    private CustomerContributionRepository customerContributionRepository;

    private LoanContributionRepository loanContributionRepository;

    private IntermediateContributionRepository intermediateContributionRepository;

    private DemandDepositContributionRepository demandDepositContributionRepository;

    private CustomerCreditRepository customerCreditRepository;

    private ValidCustomerStateRepository validCustomerStateRepository;

    public ContributionReporter(CustomerContributionRepository customerContributionRepository,
                                LoanContributionRepository loanContributionRepository,
                                IntermediateContributionRepository intermediateContributionRepository,
                                DemandDepositContributionRepository demandDepositContributionRepository,
                                CustomerCreditRepository customerCreditRepository,
                                ValidCustomerStateRepository validCustomerStateRepository) {
        this.customerContributionRepository = customerContributionRepository;
        this.loanContributionRepository = loanContributionRepository;
        this.intermediateContributionRepository = intermediateContributionRepository;
        this.demandDepositContributionRepository = demandDepositContributionRepository;
        this.customerCreditRepository = customerCreditRepository;
        this.validCustomerStateRepository = validCustomerStateRepository;
    }

    public void reportContributionInBranch() {
        Map<String, Double> branchContributions = new HashMap<>();
        Map<String, Set<String>> branchCustomers = new HashMap<>();
        for (LoanContribution loanContribution : loanContributionRepository.findAll()) {
            String branch = loanContribution.getBranch();
            if (branchContributions.containsKey(branch)) {
                Double contribution = branchContributions.get(branch);
                branchContributions.put(branch, contribution.doubleValue() + loanContribution.getContribution());
                branchCustomers.get(branch).add(loanContribution.getCustomerId());
            }
            else {
                branchContributions.put(branch, loanContribution.getContribution());
                Set<String> customers = new HashSet<>();
                customers.add(loanContribution.getCustomerId());
                branchCustomers.put(branch, customers);
            }
        }

        for (DemandDepositContribution demandDepositContribution : demandDepositContributionRepository.findAll()) {
            String branch = demandDepositContribution.getBranch();
            if (branchContributions.containsKey(branch)) {
                Double contribution = branchContributions.get(branch);
                branchContributions.put(branch, contribution.doubleValue() + demandDepositContribution.getContribution());
                branchCustomers.get(branch).add(demandDepositContribution.getCustomerId());
            }
            else {
                branchContributions.put(branch, demandDepositContribution.getContribution());
                Set<String> customers = new HashSet<>();
                customers.add(demandDepositContribution.getCustomerId());
                branchCustomers.put(branch, customers);
            }
        }

        for (IntermediateContribution intermediateContribution : intermediateContributionRepository.findAll()) {
            String branch = intermediateContribution.getBranch();
            if (branchContributions.containsKey(branch)) {
                Double contribution = branchContributions.get(branch);
                branchContributions.put(branch, contribution.doubleValue() + intermediateContribution.getContribution());
                branchCustomers.get(branch).add(intermediateContribution.getCustomerId());
            }
            else {
                branchContributions.put(branch, intermediateContribution.getContribution());
                Set<String> customers = new HashSet<>();
                customers.add(intermediateContribution.getCustomerId());
                branchCustomers.put(branch, customers);
            }
        }

        System.out.println("经营单位;贡献度;客户数量;平均贡献度");
        for (String branch : branchContributions.keySet()) {
            System.out.println(branch
                    + ";" + branchContributions.get(branch)
                    + ";" + branchCustomers.get(branch).size()
                    + ";" + branchContributions.get(branch) / branchCustomers.get(branch).size());
        }
    }

    public void reportLoanContributionInBranch() {
        Map<String, Double> branchContributions = new HashMap<>();
        Map<String, Set<String>> branchCustomers = new HashMap<>();
        for (LoanContribution loanContribution : loanContributionRepository.findAll()) {
            String branch = loanContribution.getBranch();
            if (branchContributions.containsKey(branch)) {
                Double contribution = branchContributions.get(branch);
                branchContributions.put(branch, contribution.doubleValue() + loanContribution.getContribution());
                branchCustomers.get(branch).add(loanContribution.getCustomerId());
            }
            else {
                branchContributions.put(branch, loanContribution.getContribution());
                Set<String> customers = new HashSet<>();
                customers.add(loanContribution.getCustomerId());
                branchCustomers.put(branch, customers);
            }
        }
        System.out.println("经营单位;贡献度;客户数量;平均贡献度");
        for (String branch : branchContributions.keySet()) {
            System.out.println(branch
                    + ";" + branchContributions.get(branch)
                    + ";" + branchCustomers.get(branch).size()
                    + ";" + branchContributions.get(branch) / branchCustomers.get(branch).size());
        }
    }

    public void reportDepositContributionInBranch() {
        Map<String, Double> branchContributions = new HashMap<>();
        Map<String, Set<String>> branchCustomers = new HashMap<>();
        for (DemandDepositContribution demandDepositContribution : demandDepositContributionRepository.findAll()) {
            String branch = demandDepositContribution.getBranch();
            if (branchContributions.containsKey(branch)) {
                Double contribution = branchContributions.get(branch);
                branchContributions.put(branch, contribution.doubleValue() + demandDepositContribution.getContribution());
                branchCustomers.get(branch).add(demandDepositContribution.getCustomerId());
            }
            else {
                branchContributions.put(branch, demandDepositContribution.getContribution());
                Set<String> customers = new HashSet<>();
                customers.add(demandDepositContribution.getCustomerId());
                branchCustomers.put(branch, customers);
            }
        }
        System.out.println("经营单位;贡献度;客户数量;平均贡献度");
        for (String branch : branchContributions.keySet()) {
            System.out.println(branch
                    + ";" + branchContributions.get(branch)
                    + ";" + branchCustomers.get(branch).size()
                    + ";" + branchContributions.get(branch) / branchCustomers.get(branch).size());
        }
    }

    public void reportIntermediateContributionInBranch() {
        Map<String, Double> branchContributions = new HashMap<>();
        Map<String, Set<String>> branchCustomers = new HashMap<>();
        for (IntermediateContribution intermediateContribution : intermediateContributionRepository.findAll()) {
            String branch = intermediateContribution.getBranch();
            if (branchContributions.containsKey(branch)) {
                Double contribution = branchContributions.get(branch);
                branchContributions.put(branch, contribution.doubleValue() + intermediateContribution.getContribution());
                branchCustomers.get(branch).add(intermediateContribution.getCustomerId());
            }
            else {
                branchContributions.put(branch, intermediateContribution.getContribution());
                Set<String> customers = new HashSet<>();
                customers.add(intermediateContribution.getCustomerId());
                branchCustomers.put(branch, customers);
            }
        }
        System.out.println("经营单位;贡献度;客户数量;平均贡献度");
        for (String branch : branchContributions.keySet()) {
            System.out.println(branch
                    + ";" + branchContributions.get(branch)
                    + ";" + branchCustomers.get(branch).size()
                    + ";" + branchContributions.get(branch) / branchCustomers.get(branch).size());
        }
    }

    public void reportContributionDetails() {
        Map<String, Double> scaleContributions = new HashMap<>();
        Map<String, Integer> scaleCounts = new HashMap<>();
        Map<String, Double> ownershipContributions = new HashMap<>();
        Map<String, Integer> ownershipCounts = new HashMap<>();

        double[] ownershipContributionArray = new double[ownerships.length];
        int[] ownershipCountArray = new int[ownerships.length];
        String period = "201809";

        for (CustomerContribution customerContribution : customerContributionRepository.findAll()) {
//            if (ReporterUtil.isEnterpriseCustomerInside(customerContribution.getCustomerName(),
//                    customerContribution.getBranch(), customerContribution.getProvince())) {
            String scale = customerContribution.getScale();
            String ownership = customerContribution.getOwnership();
            double contribution = customerContribution.getLoanContribution()
                    + customerContribution.getDemandDepositContribution() + customerContribution.getIntermediateContribution();


            if (scaleContributions.containsKey(scale)) {
                scaleContributions.put(scale, scaleContributions.get(scale) + contribution);
                scaleCounts.put(scale, scaleCounts.get(scale) + 1);
            } else {
                scaleContributions.put(scale, contribution);
                scaleCounts.put(scale, 1);
            }

            if (ownershipContributions.containsKey(ownership)) {
                ownershipContributions.put(ownership, ownershipContributions.get(ownership) + contribution);
                ownershipCounts.put(ownership, ownershipCounts.get(ownership) + 1);
            } else {
                ownershipContributions.put(ownership, contribution);
                ownershipCounts.put(ownership, 1);
            }

            ownership = "未知";
            ValidCustomerState validCustomerState = validCustomerStateRepository.findByPeriodAndCustomerId(period, customerContribution.getCustomerId());
            if (validCustomerState != null) {
                if (validCustomerState.getOwnership() != null || validCustomerState.getOwnership() != "") {
                    ownership = validCustomerState.getOwnership();
                }
            }
            int ownershipIndex = ReporterUtil.getOwnershipIndex(ReporterUtil.ownershipMapping(ownership));
            ownershipContributionArray[ownershipIndex] += contribution;
            ++ownershipCountArray[ownershipIndex];
//            }
        }

        System.out.println("规模;贡献度;数量;平均");
        for (String scale : scaleContributions.keySet()) {
            System.out.println(scale + ";" + scaleContributions.get(scale) + ";"
                    + scaleCounts.get(scale) + ";"
                    + scaleContributions.get(scale) / scaleCounts.get(scale));
        }

        System.out.println();

        System.out.println("所有制;贡献度;数量;平均");
//        for (String ownership : ownershipContributions.keySet()) {
//            System.out.println(ownership + ";" + ownershipContributions.get(ownership) + ";"
//                    + ownershipCounts.get(ownership) + ";"
//                    + ownershipContributions.get(ownership) / ownershipCounts.get(ownership));
//        }
        for (int i = 0; i != ownerships.length; ++i) {
            System.out.println(ownerships[i] + ";" + ownershipContributionArray[i] + ";"
                    + ownershipCountArray[i] + ";" + ownershipContributionArray[i] / ownershipCountArray[i]);
        }
    }

    public void reportContributionWithCredit() {
        double[] creditContributions = new double[ReporterUtil.credits.length];
        int[] creditCustomerCount = new int[ReporterUtil.credits.length];
        for (CustomerContribution customerContribution : customerContributionRepository.findAll()) {
//            if (ReporterUtil.isEnterpriseCustomerInside(customerContribution.getCustomerName(),
//                    customerContribution.getBranch(), customerContribution.getProvince())) {
            Optional<CustomerCredit> creditInDB = customerCreditRepository.findById(customerContribution.getCustomerId());
            String credit = "未知";
            if (creditInDB.isPresent()) {
                credit = CreditReporter.getValidCredit(creditInDB.get());
            }
            int creditIndex = ReporterUtil.adaptToCreditIndex(credit);
            double contribution = customerContribution.getLoanContribution()
                    + customerContribution.getDemandDepositContribution()
                    + customerContribution.getIntermediateContribution();

            creditContributions[creditIndex] += contribution;
            ++creditCustomerCount[creditIndex];
//            }
        }

        System.out.println("评级;贡献度;数量;平均");
        for (int i = 0; i != ReporterUtil.credits.length; ++i) {
            System.out.println(ReporterUtil.credits[i] + ";" + creditContributions[i] + ";" + creditCustomerCount[i] + ";" + creditContributions[i] / creditCustomerCount[i]);
        }
    }
}
