package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerReporter {

    private CustomerRepository customerRepository;

    private GroupCustomerRepository groupCustomerRepository;

    private ContractRepository contractRepository;

    private ContractStateRepository contractStateRepository;

    public CustomerReporter(CustomerRepository customerRepository, GroupCustomerRepository groupCustomerRepository,
                            ContractRepository contractRepository, ContractStateRepository contractStateRepository) {
        this.customerRepository = customerRepository;
        this.groupCustomerRepository = groupCustomerRepository;
        this.contractRepository = contractRepository;
        this.contractStateRepository = contractStateRepository;
    }

    public void reportGroupCustomers() {
        Set<String> validCustomers = getValidCustomers();
        Set<String> groupIds = new HashSet<String>();
        for (String id : validCustomers) {
            Optional<Customer> customerInDB = customerRepository.findById(id);
            if (customerInDB.isPresent()) {
                String groupId = customerInDB.get().getGroupId();
                if (groupId != null && !groupId.equals("")) {
                    groupIds.add(groupId);
                }
            }
        }

        for (String groupId : groupIds) {
            System.out.println(groupId);
        }
        System.out.println("集团总数是 : " + groupIds.size());
    }

    public void reportBranchs() {
        String[] periods = new String[] {"201703", "201706", "201709", "201712", "201803", "201806", "201809"};
        for (String period : periods) {
            Map<String, CustomerAnalyzeInfo> analyzeInfos = getCustomerAnalyzeInfos(period);
            reportBranchInfos(analyzeInfos, period);
        }
    }

    class BranchInfo {
        public int customerCount;
        public double remaining;
        public Map<String, Integer> scaleCustomerCount;
        public Map<String, Double> scaleCustomerRemaining;
        public int groupCount;
        public double groupRemaining;
    }

    private void reportBranchInfos(Map<String, CustomerAnalyzeInfo> customerAnalyzeInfos, String period) {
        System.out.println(period);
        Map<String, BranchInfo> branchs = new HashMap<String, BranchInfo>();
        for (String customerId : customerAnalyzeInfos.keySet()) {
            Optional<Customer> customerInDB = customerRepository.findById(customerId);
            if (customerInDB.isPresent()) {
                String customerName = customerInDB.get().getName();
                if (customerName.indexOf("银行") != -1
                        || customerName.indexOf("bank") != -1) {
                    continue;
                }
                else {
                    CustomerAnalyzeInfo customerAnalyzeInfo = customerAnalyzeInfos.get(customerId);
                    handleSingleCustomerAnalyzeInfo(customerAnalyzeInfo, branchs);
                }
            }
        }

        for (String branch : branchs.keySet()) {
//            BranchInfo branchInfo = branchs.get(branch);
//            System.out.println(branch);
//            System.out.println("客户总数 : " + branchInfo.customerCount);
//            System.out.println("余额 : " + branchInfo.remaining);
//            for (String scale : branchInfo.scaleCustomerCount.keySet()) {
//                System.out.println(scale + " 数量 : " + branchInfo.scaleCustomerCount.get(scale) + ", 余额 : " + branchInfo.scaleCustomerRemaining.get(scale));
//            }
//            System.out.println("集团成员数量 : " + branchInfo.groupCount);
//            System.out.println("集团信贷余额 : " + branchInfo.groupRemaining);
            BranchInfo branchInfo = branchs.get(branch);
            System.out.print(branch + ";");
            System.out.print(branchInfo.customerCount + ";");
            System.out.print(branchInfo.remaining + ";");
            System.out.print(branchInfo.scaleCustomerCount.get("微型") + ";");
            System.out.print(branchInfo.scaleCustomerCount.get("小型") + ";");
            System.out.print(branchInfo.scaleCustomerCount.get("中型") + ";");
            System.out.print(branchInfo.scaleCustomerCount.get("大型") + ";");
            System.out.print(branchInfo.scaleCustomerRemaining.get("微型") + ";");
            System.out.print(branchInfo.scaleCustomerRemaining.get("小型") + ";");
            System.out.print(branchInfo.scaleCustomerRemaining.get("中型") + ";");
            System.out.print(branchInfo.scaleCustomerRemaining.get("大型") + ";");
            System.out.print(branchInfo.groupCount + ";");
            System.out.print(branchInfo.groupRemaining + ";");
            System.out.println();
        }
    }

    private void handleSingleCustomerAnalyzeInfo(CustomerAnalyzeInfo customerAnalyzeInfo, Map<String, BranchInfo> branchs) {
        if (customerAnalyzeInfo.branch.equals("巴黎分行")) {
            int i = 0;
        }
        if (branchs.containsKey(customerAnalyzeInfo.branch)) {
            BranchInfo branchInfo = branchs.get(customerAnalyzeInfo.branch);
            ++branchInfo.customerCount;
            branchInfo.remaining += customerAnalyzeInfo.remaining;
            handleSingleCustomerInfoInScale(customerAnalyzeInfo, branchInfo);
            if (isInGroup(customerAnalyzeInfo.id)) {
                ++branchInfo.groupCount;
                branchInfo.groupRemaining += customerAnalyzeInfo.remaining;
            }
        }
        else {
            BranchInfo branchInfo = new BranchInfo();
            branchInfo.customerCount = 1;
            branchInfo.remaining = customerAnalyzeInfo.remaining;
            branchInfo.scaleCustomerCount = new HashMap<String, Integer>();
            branchInfo.scaleCustomerRemaining = new HashMap<String, Double>();
            branchInfo.scaleCustomerCount.put(customerAnalyzeInfo.scale, 1);
            branchInfo.scaleCustomerRemaining.put(customerAnalyzeInfo.scale, customerAnalyzeInfo.remaining);
            if (isInGroup(customerAnalyzeInfo.id)) {
                branchInfo.groupCount = 1;
                branchInfo.groupRemaining = customerAnalyzeInfo.remaining;
            }
            branchs.put(customerAnalyzeInfo.branch, branchInfo);
        }
    }

    private void handleSingleCustomerInfoInScale(CustomerAnalyzeInfo customerAnalyzeInfo, BranchInfo branchInfo) {
        if (branchInfo.scaleCustomerCount.containsKey(customerAnalyzeInfo.scale)) {
            int newScaleCustomerCount = branchInfo.scaleCustomerCount.get(customerAnalyzeInfo.scale) + 1;
            branchInfo.scaleCustomerCount.put(customerAnalyzeInfo.scale, newScaleCustomerCount);
            double newScaleCustomerRemaining = branchInfo.scaleCustomerRemaining.get(customerAnalyzeInfo.scale) + customerAnalyzeInfo.remaining;
            branchInfo.scaleCustomerRemaining.put(customerAnalyzeInfo.scale, newScaleCustomerRemaining);
        }
        else {
            branchInfo.scaleCustomerCount.put(customerAnalyzeInfo.scale, 1);
            branchInfo.scaleCustomerRemaining.put(customerAnalyzeInfo.scale, customerAnalyzeInfo.remaining);
        }
    }

    private boolean isInGroup(String customerId) {
        Optional<Customer> customerInDB = customerRepository.findById(customerId);
        if (customerInDB.isPresent()) {
            String groupId = customerInDB.get().getGroupId();
            if (groupId != null && !groupId.equals("")) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public Map<String, CustomerAnalyzeInfo> getCustomerAnalyzeInfos(String period) {
        Map<String, CustomerAnalyzeInfo> customerAnalyzeInfos = new HashMap<String, CustomerAnalyzeInfo>();
        List<ContractState> contractStates = contractStateRepository.findByPeriod(period);
        for (ContractState contractState : contractStates) {
            String customerId = contractState.getCustomerId();
//            if (contractState.getProvince() == null || contractState.getProvince().equals("")) {
//                continue;
//            }
            if (customerAnalyzeInfos.containsKey(customerId)) {
                customerAnalyzeInfos.get(customerId).remaining += contractState.getRemaining();
            }
            else {
                CustomerAnalyzeInfo info = new CustomerAnalyzeInfo();
                info.id = contractState.getCustomerId();
                info.remaining = contractState.getRemaining();
                info.scale = contractState.getScale();
                info.industry = contractState.getIndustry();
                String branch = "未知";
                Optional<Customer> customerInDB = customerRepository.findById(contractState.getCustomerId());
                if (customerInDB.isPresent()) {
                    branch = customerInDB.get().getBranch();
                }
                info.branch = branch;
                customerAnalyzeInfos.put(customerId, info);
            }
        }
        return customerAnalyzeInfos;
    }

    private Set<String> getValidCustomers() {
        String period = "201809";
        List<ContractState> contractStates = contractStateRepository.findByPeriod(period);
        Set<String> validCustomers = new HashSet<String>();
        for (ContractState contractState : contractStates) {
            //Contract contract = contractRepository.findContractById(contractState.getContractId());
            validCustomers.add(contractState.getCustomerId());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse("2018-09-30");
            List<Customer> lastCreditValidCustomers = customerRepository.findAllByLastCreditDateAfter(date);
            for (Customer customer : lastCreditValidCustomers) {
                validCustomers.add(customer.getId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return validCustomers;
    }
}