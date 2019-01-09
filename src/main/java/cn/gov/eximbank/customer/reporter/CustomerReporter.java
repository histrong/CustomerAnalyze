package cn.gov.eximbank.customer.reporter;

import cn.gov.eximbank.customer.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerReporter {

    private static Logger logger = LoggerFactory.getLogger(CustomerReporter.class);

    private CustomerRepository customerRepository;

    private GroupCustomerRepository groupCustomerRepository;

    private ValidCustomerStateRepository validCustomerStateRepository;

    private ContractRepository contractRepository;

    private ContractStateRepository contractStateRepository;

    public CustomerReporter(CustomerRepository customerRepository,
                            GroupCustomerRepository groupCustomerRepository,
                            ValidCustomerStateRepository validCustomerStateRepository,
                            ContractRepository contractRepository,
                            ContractStateRepository contractStateRepository) {
        this.customerRepository = customerRepository;
        this.groupCustomerRepository = groupCustomerRepository;
        this.validCustomerStateRepository = validCustomerStateRepository;
        this.contractRepository = contractRepository;
        this.contractStateRepository = contractStateRepository;
    }

    public void reportCustomers() {
        Set<String> validCustomerIds = getValidCustomerIds(validCustomerStateRepository);
        Set<String> creditCustomerIds = getCreditCustomerIds(customerRepository);
        Set<String> totalCustomerIds = new HashSet<String>();
        totalCustomerIds.addAll(validCustomerIds);
        totalCustomerIds.addAll(creditCustomerIds);
        System.out.println("2018年9月仍有余额客户数 : " + validCustomerStateRepository.findByPeriod("201809").size());
        System.out.println("2017年3月至今曾经有余额客户数为 :" + validCustomerIds.size());
        System.out.println("2017年3月至今包含评级未过期的有效客户数为 : " + totalCustomerIds.size());
    }

    public void reportCustomerDetails(String period) {
//        List<ValidCustomerState> validCustomerStates = validCustomerStateRepository.findByPeriod(period);
//        int bankCustomer = 0;
//        int governmentCustomer = 0;
//        int foreignCustomer = 0;
//        int[] scale
    }

    public void reportGroupCustomers() {
        // 201703至今所有有余额的客户
        Set<String> validCustomerIds = getValidCustomerIds(validCustomerStateRepository);

        // 截止201809授信未过期客户
        Set<String> creditCustomerIds = getCreditCustomerIds(customerRepository);

        // 上述两客户集合的并集
        Set<String> totalCustomerIds = new HashSet<String>();
        totalCustomerIds.addAll(validCustomerIds);
        totalCustomerIds.addAll(creditCustomerIds);


        // 201809仍有余额的客户
        String latestPeriod = ReporterUtil.getPeriods()[0];
        Set<String> lastedValidCustomerIds = getValidCustomerIdsWithPeriod(validCustomerStateRepository, latestPeriod);

        System.out.println("2018年9月末仍有余额集团数量 : " +
                getGroupIdsForCustomers(customerRepository, lastedValidCustomerIds).size());
        System.out.println("2017年3月至今所有有效集团数量 : " +
                getGroupIdsForCustomers(customerRepository, totalCustomerIds).size());


        // 201809仍有余额的集团客户
        Set<String> latestValidGroupIds = getGroupIdsForCustomers(customerRepository, lastedValidCustomerIds);

        // 201703至今所有有效的集团客户
        Set<String> totalValidGroupIds = getGroupIdsForCustomers(customerRepository, totalCustomerIds);
        System.out.println("2018年9月末仍有余额跨经营单位集团数量 : " + getGroupInBranches(latestValidGroupIds).size());
        System.out.println("201703至今有效的跨经营单位集团数量 : " + getGroupInBranches(totalValidGroupIds).size());

        reportGroupDistributions(latestValidGroupIds);
    }

    private void reportGroupDistributions(Set<String> latestValidGroupIds) {
        int[] groupDistributions = new int[40];
        for (String groupId : latestValidGroupIds) {
            int branchCount = getBranchCount(groupId);
            ++groupDistributions[branchCount];
        }
        for (int i = 0; i != groupDistributions.length; ++i) {
            System.out.println(i + ";" + groupDistributions[i]);
        }
    }

    private int getBranchCount(String groupId) {
        Set<String> branches = new HashSet<String>();
        List<Customer> customers = customerRepository.findByGroupId(groupId);
        for (Customer customer : customers) {
            branches.add(customer.getBranch());
        }
        if (branches.size() >= 15) {
            Optional<GroupCustomer> groupCustomerInDB = groupCustomerRepository.findById(groupId);
            if (groupCustomerInDB.isPresent()) {
                String groupName = groupCustomerInDB.get().getName();
                System.out.println(groupName + ";" + branches.size());
            }
        }
        return branches.size();
    }

    public Set<String> getGroupInBranches(Set<String> groupIds) {
        Set<String> groupIdsInBranchesIds = new HashSet<String>();
        for (String validGroupId : groupIds) {
            if (isGroupInBranches(validGroupId)) {
                groupIdsInBranchesIds.add(validGroupId);
            }
        }
        return groupIdsInBranchesIds;
    }



//    public void reportBranchs() {
//        String[] periods = new String[] {"201703", "201706", "201709", "201712", "201803", "201806", "201809"};
//        for (String period : periods) {
//            Map<String, CustomerAnalyzeInfo> analyzeInfos = getCustomerAnalyzeInfos(period);
//            reportBranchInfos(analyzeInfos, period);
//        }
//    }
//
//    class BranchInfo {
//        public int customerCount;
//        public double remaining;
//        public Map<String, Integer> scaleCustomerCount;
//        public Map<String, Double> scaleCustomerRemaining;
//        public int groupCount;
//        public double groupRemaining;
//    }
//
//    private void reportBranchInfos(Map<String, CustomerAnalyzeInfo> customerAnalyzeInfos, String period) {
//        System.out.println(period);
//        Map<String, BranchInfo> branchs = new HashMap<String, BranchInfo>();
//        for (String customerId : customerAnalyzeInfos.keySet()) {
//            Optional<Customer> customerInDB = customerRepository.findById(customerId);
//            if (customerInDB.isPresent()) {
//                String customerName = customerInDB.get().getName();
//                if (customerName.indexOf("银行") != -1
//                        || customerName.indexOf("bank") != -1) {
//                    continue;
//                }
//                else {
//                    CustomerAnalyzeInfo customerAnalyzeInfo = customerAnalyzeInfos.get(customerId);
//                    handleSingleCustomerAnalyzeInfo(customerAnalyzeInfo, branchs);
//                }
//            }
//        }
//
//        for (String branch : branchs.keySet()) {
////            BranchInfo branchInfo = branchs.get(branch);
////            System.out.println(branch);
////            System.out.println("客户总数 : " + branchInfo.customerCount);
////            System.out.println("余额 : " + branchInfo.remaining);
////            for (String scale : branchInfo.scaleCustomerCount.keySet()) {
////                System.out.println(scale + " 数量 : " + branchInfo.scaleCustomerCount.get(scale) + ", 余额 : " + branchInfo.scaleCustomerRemaining.get(scale));
////            }
////            System.out.println("集团成员数量 : " + branchInfo.groupCount);
////            System.out.println("集团信贷余额 : " + branchInfo.groupRemaining);
//            BranchInfo branchInfo = branchs.get(branch);
//            System.out.print(branch + ";");
//            System.out.print(branchInfo.customerCount + ";");
//            System.out.print(branchInfo.remaining + ";");
//            System.out.print(branchInfo.scaleCustomerCount.get("微型") + ";");
//            System.out.print(branchInfo.scaleCustomerCount.get("小型") + ";");
//            System.out.print(branchInfo.scaleCustomerCount.get("中型") + ";");
//            System.out.print(branchInfo.scaleCustomerCount.get("大型") + ";");
//            System.out.print(branchInfo.scaleCustomerRemaining.get("微型") + ";");
//            System.out.print(branchInfo.scaleCustomerRemaining.get("小型") + ";");
//            System.out.print(branchInfo.scaleCustomerRemaining.get("中型") + ";");
//            System.out.print(branchInfo.scaleCustomerRemaining.get("大型") + ";");
//            System.out.print(branchInfo.groupCount + ";");
//            System.out.print(branchInfo.groupRemaining + ";");
//            System.out.println();
//        }
//    }
//
//    private void handleSingleCustomerAnalyzeInfo(CustomerAnalyzeInfo customerAnalyzeInfo, Map<String, BranchInfo> branchs) {
//        if (customerAnalyzeInfo.branch.equals("巴黎分行")) {
//            int i = 0;
//        }
//        if (branchs.containsKey(customerAnalyzeInfo.branch)) {
//            BranchInfo branchInfo = branchs.get(customerAnalyzeInfo.branch);
//            ++branchInfo.customerCount;
//            branchInfo.remaining += customerAnalyzeInfo.remaining;
//            handleSingleCustomerInfoInScale(customerAnalyzeInfo, branchInfo);
////            if (isInGroup(customerAnalyzeInfo.id)) {
////                ++branchInfo.groupCount;
////                branchInfo.groupRemaining += customerAnalyzeInfo.remaining;
////            }
//        }
//        else {
//            BranchInfo branchInfo = new BranchInfo();
//            branchInfo.customerCount = 1;
//            branchInfo.remaining = customerAnalyzeInfo.remaining;
//            branchInfo.scaleCustomerCount = new HashMap<String, Integer>();
//            branchInfo.scaleCustomerRemaining = new HashMap<String, Double>();
//            branchInfo.scaleCustomerCount.put(customerAnalyzeInfo.scale, 1);
//            branchInfo.scaleCustomerRemaining.put(customerAnalyzeInfo.scale, customerAnalyzeInfo.remaining);
////            if (isInGroup(customerAnalyzeInfo.id)) {
////                branchInfo.groupCount = 1;
////                branchInfo.groupRemaining = customerAnalyzeInfo.remaining;
////            }
//            branchs.put(customerAnalyzeInfo.branch, branchInfo);
//        }
//    }
//
//    private void handleSingleCustomerInfoInScale(CustomerAnalyzeInfo customerAnalyzeInfo, BranchInfo branchInfo) {
//        if (branchInfo.scaleCustomerCount.containsKey(customerAnalyzeInfo.scale)) {
//            int newScaleCustomerCount = branchInfo.scaleCustomerCount.get(customerAnalyzeInfo.scale) + 1;
//            branchInfo.scaleCustomerCount.put(customerAnalyzeInfo.scale, newScaleCustomerCount);
//            double newScaleCustomerRemaining = branchInfo.scaleCustomerRemaining.get(customerAnalyzeInfo.scale) + customerAnalyzeInfo.remaining;
//            branchInfo.scaleCustomerRemaining.put(customerAnalyzeInfo.scale, newScaleCustomerRemaining);
//        }
//        else {
//            branchInfo.scaleCustomerCount.put(customerAnalyzeInfo.scale, 1);
//            branchInfo.scaleCustomerRemaining.put(customerAnalyzeInfo.scale, customerAnalyzeInfo.remaining);
//        }
//    }
//

//
//    public Map<String, CustomerAnalyzeInfo> getCustomerAnalyzeInfos(String period) {
//        Map<String, CustomerAnalyzeInfo> customerAnalyzeInfos = new HashMap<String, CustomerAnalyzeInfo>();
//        List<ContractState> contractStates = contractStateRepository.findByPeriod(period);
//        for (ContractState contractState : contractStates) {
//            String customerId = contractState.getCustomerId();
////            if (contractState.getProvince() == null || contractState.getProvince().equals("")) {
////                continue;
////            }
//            if (customerAnalyzeInfos.containsKey(customerId)) {
//                customerAnalyzeInfos.get(customerId).remaining += contractState.getRemaining();
//            }
//            else {
//                CustomerAnalyzeInfo info = new CustomerAnalyzeInfo();
//                info.id = contractState.getCustomerId();
//                info.remaining = contractState.getRemaining();
////                info.scale = contractState.getScale();
////                info.industry = contractState.getIndustry();
//                String branch = "未知";
//                Optional<Customer> customerInDB = customerRepository.findById(contractState.getCustomerId());
//                if (customerInDB.isPresent()) {
//                    branch = customerInDB.get().getBranch();
//                }
//                info.branch = branch;
//                customerAnalyzeInfos.put(customerId, info);
//            }
//        }
//        return customerAnalyzeInfos;
//    }
//
//    private Set<String> getValidCustomers() {
//        String period = "201809";
//        List<ContractState> contractStates = contractStateRepository.findByPeriod(period);
//        Set<String> validCustomers = new HashSet<String>();
//        for (ContractState contractState : contractStates) {
//            //Contract contract = contractRepository.findContractById(contractState.getContractId());
//            validCustomers.add(contractState.getCustomerId());
//        }
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date date = format.parse("2018-09-30");
//            List<Customer> lastCreditValidCustomers = customerRepository.findAllByLastCreditDateAfter(date);
//            for (Customer customer : lastCreditValidCustomers) {
//                validCustomers.add(customer.getId());
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return validCustomers;
//    }

    public static Set<String> getValidCustomerIdsWithPeriod(ValidCustomerStateRepository validCustomerStateRepository,
                                                            String period) {
        Set<String> latestValidCustomerIds = new HashSet<String>();
        List<ValidCustomerState> validCustomerStates
                = validCustomerStateRepository.findByPeriod(period);
        for (ValidCustomerState validCustomerState : validCustomerStates) {
            latestValidCustomerIds.add(validCustomerState.getCustomerId());
        }
        return latestValidCustomerIds;
    }

    public static Set<String> getValidCustomerIds(ValidCustomerStateRepository validCustomerStateRepository) {
        Set<String> validCustomerIds = new HashSet<String>();
        for (String period : ReporterUtil.getPeriods()) {
            List<ValidCustomerState> validCustomerStates
                    = validCustomerStateRepository.findByPeriod(period);
            for (ValidCustomerState validCustomerState : validCustomerStates) {
                validCustomerIds.add(validCustomerState.getCustomerId());
            }
        }
        return validCustomerIds;
    }

    public static Set<String> getCreditCustomerIds(CustomerRepository customerRepository) {
        Set<String> creditCustomerIds = new HashSet<String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse("2018-09-30");
            List<Customer> lastCreditValidCustomers = customerRepository.findAllByLastCreditDateAfter(date);
            for (Customer customer : lastCreditValidCustomers) {
                creditCustomerIds.add(customer.getId());
            }
            return creditCustomerIds;
        } catch (ParseException e) {
            e.printStackTrace();
            return new HashSet<String>();
        }
    }

    public static Set<String> getGroupIdsForCustomers(CustomerRepository customerRepository, Set<String> customerIds) {
        Set<String> groupIds = new HashSet<String>();
        for (String customerId : customerIds) {
            Optional<Customer> customerInDB = customerRepository.findById(customerId);
            if (customerInDB.isPresent() && isInGroup(customerInDB.get())) {
                groupIds.add(customerInDB.get().getGroupId());
            }
        }
        return groupIds;
    }

    private boolean isGroupInBranches(String groupId) {
        List<Customer> customers = customerRepository.findByGroupId(groupId);
        if (customers == null || customers.size() == 0) {
            logger.error("Group not contain customers : " + groupId);
            return false;
        }
        else if (customers.size() == 1) {
            return false;
        }
        else {
            String firstCustomerBranch = customers.get(0).getBranch();
            for (int i = 1; i != customers.size(); ++i) {
                if (!customers.get(i).getBranch().equals(firstCustomerBranch)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isInGroup(Customer customer) {
        String groupId = customer.getGroupId();
        if (groupId == null || groupId.equals("")) {
            return false;
        }
        else {
            return true;
        }

    }
}