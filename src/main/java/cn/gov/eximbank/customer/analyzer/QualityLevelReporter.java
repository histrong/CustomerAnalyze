package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QualityLevelReporter {

    private CustomerRepository customerRepository;

    private GroupCustomerRepository groupCustomerRepository;

    private ContractRepository contractRepository;

    private ContractStateRepository contractStateRepository;

    public QualityLevelReporter(CustomerRepository customerRepository, GroupCustomerRepository groupCustomerRepository,
                                ContractRepository contractRepository, ContractStateRepository contractStateRepository) {
        this.customerRepository = customerRepository;
        this.groupCustomerRepository = groupCustomerRepository;
        this.contractRepository = contractRepository;
        this.contractStateRepository = contractStateRepository;
    }

    public void reportQualityLevel() {
        String[] periods = new String[] {"201703", "201706", "201709", "201712", "201803", "201806", "201809"};
        for (String period : periods) {
            List<ContractState> contractStates = contractStateRepository.findByPeriod(period);
            reportQualityLevelInfo(contractStates, period);
        }
    }

    private void reportQualityLevelInfo(List<ContractState> contractStates, String period) {
        Map<String, QualityInfo> industryQualities = new HashMap<String, QualityInfo>();
        Map<String, QualityInfo> scaleQualities = new HashMap<String, QualityInfo>();
        Map<String, QualityInfo> branchQualities = new HashMap<String, QualityInfo>();
        Map<String, QualityInfo> ownershipQualities = new HashMap<String, QualityInfo>();
        Map<String, QualityInfo> groupQualites = new HashMap<String, QualityInfo>();

        for (ContractState contractState : contractStates) {
            handleSingleContractState(contractState, industryQualities, scaleQualities, branchQualities, ownershipQualities, groupQualites);
        }

        System.out.println(period);
        System.out.println();

        for (String industry : industryQualities.keySet()) {
            displayQualityInfo(industry, industryQualities.get(industry));
        }

        System.out.println();

        for (String scale : scaleQualities.keySet()) {
            displayQualityInfo(scale, scaleQualities.get(scale));
        }

        System.out.println();

        for (String branch : branchQualities.keySet()) {
            displayQualityInfo(branch, branchQualities.get(branch));
        }

        System.out.println();

        for (String ownership : ownershipQualities.keySet()) {
            displayQualityInfo(ownership, ownershipQualities.get(ownership));
        }

        System.out.println();

        for (String group : groupQualites.keySet()) {
            displayQualityInfo(group, groupQualites.get(group));
        }

        System.out.println();
    }

    private void displayQualityInfo(String tag, QualityInfo info) {
        System.out.println(tag + ";" + info.total + ";" + info.nonPerform);
    }

    private void handleSingleContractState(ContractState contractState,
                                           Map<String, QualityInfo> industryQualities,
                                           Map<String, QualityInfo> scaleQualities,
                                           Map<String, QualityInfo> branchQualities,
                                           Map<String, QualityInfo> ownershipQualities,
                                           Map<String, QualityInfo> groupQualites) {
        Optional<Customer> customerInDB = customerRepository.findById(contractState.getCustomerId());
        if (!customerInDB.isPresent()) {
            return;
        }
        
        if (!isValidCustomer(customerInDB.get())) {
            return;
        }
        
        handleSingleContractStateWithTag(contractState.getIndustry(), contractState, industryQualities);
        handleSingleContractStateWithTag(contractState.getScale(), contractState, scaleQualities);
        handleSingleContractStateWithTag(customerInDB.get().getBranch(), contractState, branchQualities);
        handleSingleContractStateWithTag(contractState.getOwnership(), contractState, ownershipQualities);

        String groupTag = "单一";
        if (customerInDB.get().getGroupId()!= null && !customerInDB.get().getGroupId().equals("")) {
            groupTag = "集团";
        }
        handleSingleContractStateWithTag(groupTag, contractState, groupQualites);
    }

    private void handleSingleContractStateWithTag(String tag, ContractState contractState, Map<String, QualityInfo> qualities) {
        if (qualities.containsKey(tag)) {
            QualityInfo qualityInfo = qualities.get(tag);
            qualityInfo.total += contractState.getRemaining();
            if (contractState.getQualityLevel() > 2) {
                qualityInfo.nonPerform += contractState.getRemaining();
            }
            qualities.put(tag, qualityInfo);
        }
        else {
            QualityInfo qualityInfo = new QualityInfo();
            qualityInfo.total += contractState.getRemaining();
            if (contractState.getQualityLevel() > 2) {
                qualityInfo.nonPerform += contractState.getRemaining();
            }
            qualities.put(tag, qualityInfo);
        }
    }


    private boolean isValidCustomer(Customer customer) {
        if (customer.getBranch().equals("主权客户部")) {
            return false;
        }
        else if (customer.getName().indexOf("银行") != -1 || customer.getName().indexOf("bank") != -1) {
            return false;
        }
        else {
            return true;
        }
    }
}
