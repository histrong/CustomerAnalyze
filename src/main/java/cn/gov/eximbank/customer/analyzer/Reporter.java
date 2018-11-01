package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
