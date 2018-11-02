package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Reporter {

    private CustomerRepository customerRepository;

    private GroupCustomerRepository groupCustomerRepository;

    private ContractRepository contractRepository;

    private ContractStateRepository contractStateRepository;

    public Reporter(CustomerRepository customerRepository, GroupCustomerRepository groupCustomerRepository,
                    ContractRepository contractRepository, ContractStateRepository contractStateRepository) {
        this.customerRepository = customerRepository;
        this.groupCustomerRepository = groupCustomerRepository;
        this.contractRepository = contractRepository;
        this.contractStateRepository = contractStateRepository;
    }

    public void reportMainInfo() {
        int validCustomerCount = 0;
        for (Customer customer : customerRepository.findAll()) {
            if (customer.getFirstDealDate() != null || !customer.isInSystem()) {
                ++validCustomerCount;
            }
        }
        System.out.println("历史有效客户数 : " + validCustomerCount);

        String period = "201809";
        List<ContractState> contractStates = contractStateRepository.findByPeriod(period);
        Set<String> remainingCusterIds = new HashSet<String>();

        double totalRemaing = 0.0;
        for (ContractState contractState : contractStateRepository.findByPeriod("201809")) {
            totalRemaing += contractState.getRemaining();
        }
        System.out.println("余额折人民币合计 : " + totalRemaing);

        for (ContractState contractState : contractStates) {
            if (contractState.getRemaining() > 0.01) {
                Contract contract = contractRepository.findContractById(contractState.getContractId());
                remainingCusterIds.add(contract.getCustomerId());
            }
            else {
                System.out.println("余额为0 : " + contractState.getRemaining());
            }
        }
        System.out.println("截止9月末仍有余额客户数 : " + remainingCusterIds.size());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse("2018-09-30");
            List<Customer> lastCreditValidCustomers = customerRepository.findAllByLastCreditDateAfter(date);
            int count = 0;
            for (Customer customer : lastCreditValidCustomers) {
                if (!remainingCusterIds.contains(customer.getId())) {
                    ++count;
                }
            }
            System.out.println("余额为0但授信在有效期的客户数为 : " + count);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Date date = format.parse("2017-12-31");
            List<Customer> firstDealInYearCustomers = customerRepository.findAllByFirstDealDateAfter(date);
            System.out.println("今年首次发生业务客户 : " + firstDealInYearCustomers.size());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<ContractState> contractStateList = contractStateRepository.findByPeriodAndQualityLevelGreaterThan(period, 2);
        double notgoodRemaining = 0.0;
        for (ContractState contractState : contractStateList) {
            notgoodRemaining += contractState.getRemaining();
        }
        System.out.println("不良贷款余额 : " + notgoodRemaining);

        List<ContractState> lastYearContractStates = contractStateRepository.findByPeriod("201712");
        double lastYearRemaing = 0.0;
        for (ContractState contractState : lastYearContractStates) {
            lastYearRemaing += contractState.getRemaining();
        }
        System.out.println("去年年末余额 : " + lastYearRemaing + ", 本年度新增 : " + (totalRemaing - lastYearRemaing));
    }

    public void reportGovermentCustomerInfo() {
        String period = "201809";
        List<Customer> govermentCustomers = customerRepository.findAllByBranch("主权客户部");
        System.out.println("主权客户部客户数 ： " + govermentCustomers.size());
        double totalReming = 0.0;
        List<Contract> contracts = getContractByCustomers(govermentCustomers);
        for (Contract contract : contracts) {
            ContractState contractState = contractStateRepository.findByPeriodAndContractId(period, contract.getId());
            if (contractState != null) {
                totalReming += contractState.getRemaining();
            }
        }
        System.out.println("主权客户部客户余额 ： " + totalReming);
    }

    private List<Contract> getContractByCustomer(Customer customer) {
        return contractRepository.findByCustomerId(customer.getId());
    }

    private List<Contract> getContractByCustomers(List<Customer> customers) {
        List<Contract> contracts = new ArrayList<Contract>();
        for (Customer customer : customers) {
            List<Contract> currerntCustomerContracts = getContractByCustomer(customer);
            contracts.addAll(currerntCustomerContracts);
        }
        return contracts;
    }

    public void reportCustomerScale() {
        String period1 = "201712";
        String period2 = "201809";

        List<ContractState> lastYearContractStates = contractStateRepository.findByPeriod(period1);
        List<ContractState> currentContractStates = contractStateRepository.findByPeriod(period2);
        analyzeCustomerScale(lastYearContractStates, period1);
        analyzeCustomerScale(currentContractStates, period2);
    }

    private void analyzeCustomerScale(List<ContractState> contractStates, String tag) {
        Map<String, CustomerAnalyzeInfo> customerAnalyzeInfos = new HashMap<String, CustomerAnalyzeInfo>();
        for (ContractState contractState : contractStates) {
            String customerId = contractState.getCustomerId();
            if (contractState.getProvince() == null || contractState.getProvince().equals("")) {
                continue;
            }
            if (customerAnalyzeInfos.containsKey(customerId)) {
                customerAnalyzeInfos.get(customerId).remaining += contractState.getRemaining();
            }
            else {
                CustomerAnalyzeInfo info = new CustomerAnalyzeInfo();
                info.id = contractState.getCustomerId();
                info.remaining = contractState.getRemaining();
                info.scale = contractState.getScale();
                customerAnalyzeInfos.put(customerId, info);
            }
        }

        Map<String, Integer> scaleCount = new HashMap<String, Integer>();
        Map<String, Double> scaleRemaning = new HashMap<String, Double>();
        for (String customerId : customerAnalyzeInfos.keySet()) {
            CustomerAnalyzeInfo info = customerAnalyzeInfos.get(customerId);
            if (scaleCount.containsKey(info.scale)) {
                Integer last = scaleCount.get(info.scale);
                scaleCount.put(info.scale, last + 1);
                Double lastRemaing = scaleRemaning.get(info.scale);
                scaleRemaning.put(info.scale, lastRemaing + info.remaining);
            }
            else {
                scaleCount.put(info.scale, 1);
                scaleRemaning.put(info.scale, info.remaining);
            }
        }

        for (String scale : scaleCount.keySet()) {
            System.out.println(scale + " 数量 : " + scaleCount.get(scale));
            System.out.println(scale + " 余额 : " + scaleRemaning.get(scale));
        }
    }

    public void reportOwnership() {
        String period1 = "201712";
        String period2 = "201809";

        List<ContractState> lastYearContractStates = contractStateRepository.findByPeriod(period1);
        List<ContractState> currentContractStates = contractStateRepository.findByPeriod(period2);
        analyzeCustomerOwnerShip(lastYearContractStates, period1);
        analyzeCustomerOwnerShip(currentContractStates, period2);
    }

    private void analyzeCustomerOwnerShip(List<ContractState> contractStates, String tag) {
        System.out.println(tag);
        Map<String, CustomerAnalyzeInfo> customerAnalyzeInfos = new HashMap<String, CustomerAnalyzeInfo>();
        for (ContractState contractState : contractStates) {
            String customerId = contractState.getCustomerId();
            if (contractState.getProvince() == null || contractState.getProvince().equals("")) {
                continue;
            }
            if (customerAnalyzeInfos.containsKey(customerId)) {
                customerAnalyzeInfos.get(customerId).remaining += contractState.getRemaining();
            }
            else {
                CustomerAnalyzeInfo info = new CustomerAnalyzeInfo();
                info.id = contractState.getCustomerId();
                info.remaining = contractState.getRemaining();
                info.scale = contractState.getScale();
                info.onwership = contractState.getOwnership();
                customerAnalyzeInfos.put(customerId, info);
            }
        }

        Map<String, Integer> scaleCount = new HashMap<String, Integer>();
        Map<String, Double> scaleRemaning = new HashMap<String, Double>();
        for (String customerId : customerAnalyzeInfos.keySet()) {
            CustomerAnalyzeInfo info = customerAnalyzeInfos.get(customerId);
            if (scaleCount.containsKey(info.onwership)) {
                Integer last = scaleCount.get(info.onwership);
                scaleCount.put(info.onwership, last + 1);
                Double lastRemaing = scaleRemaning.get(info.onwership);
                scaleRemaning.put(info.onwership, lastRemaing + info.remaining);
            }
            else {
                scaleCount.put(info.onwership, 1);
                scaleRemaning.put(info.onwership, info.remaining);
            }
        }

        for (String scale : scaleCount.keySet()) {
            System.out.println(scale + " 数量 : " + scaleCount.get(scale));
            System.out.println(scale + " 余额 : " + scaleRemaning.get(scale));
        }
    }
}
