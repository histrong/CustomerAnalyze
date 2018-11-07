package cn.gov.eximbank.customer;

import cn.gov.eximbank.customer.analyzer.*;
import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.reporter.CreditReporter;
import cn.gov.eximbank.customer.reporter.CustomerDetailReporter;
import cn.gov.eximbank.customer.reporter.CustomerReporter;
import cn.gov.eximbank.customer.reporter.RemainingReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AnalyzeRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzeRunner.class);

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractStateRepository contractStateRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GroupCustomerRepository groupCustomerRepository;

    @Autowired
    private ValidCustomerStateRepository validCustomerStateRepository;

    @Autowired
    private CustomerCreditRepository customerCreditRepository;

    @Override
    public void run(String... args) throws Exception {
        if (args.length != 1) {
            logger.error("Arguments is not enough");
        }
        else {
            String argument = args[0];
            if (argument.equals("contracts")) {
                readContracts();
            } else if (argument.equals("customers")) {
                readCustomers();
            } else if (argument.equals("credit")) {
                readCredits();
            } else if (argument.equals("analyze")) {
                report();
            }
        }
    }

    private void readContracts() {
        ContractAnalyzer contractAnalyzer = new ContractAnalyzer(contractRepository, contractStateRepository,
                customerRepository, validCustomerStateRepository);
        contractAnalyzer.readContractFiles();
    }

    private void readCustomers() {
        CustomerAnalyzer customerAnalyzer = new CustomerAnalyzer(customerRepository, groupCustomerRepository);
        customerAnalyzer.readCustomerFiles();
    }

    private void readCredits() {
        CreditAnalyzer creditAnalyzer = new CreditAnalyzer(customerCreditRepository);
        creditAnalyzer.readCreditFiles();
    }

    private void report() {
        CustomerReporter customerReporter = new CustomerReporter(customerRepository,
                groupCustomerRepository, validCustomerStateRepository,
                contractRepository, contractStateRepository);
//        customerReporter.reportCustomers();
//        customerReporter.reportGroupCustomers();
//        customerReporter.reportBranchs();

        CustomerDetailReporter customerDetailReporter
                = new CustomerDetailReporter(customerRepository, groupCustomerRepository, validCustomerStateRepository);
//        customerDetailReporter.reportCustomerDetails();
        customerDetailReporter.reportValidCustomer("201809");

        RemainingReporter remainingReporter
                = new RemainingReporter(customerRepository, validCustomerStateRepository);
//        remainingReporter.reportRemainingDetails();

        QualityLevelReporter qualityLevelReporter = new QualityLevelReporter(customerRepository, groupCustomerRepository,
                contractRepository, contractStateRepository);
//        qualityLevelReporter.reportQualityLevel();

        CreditReporter creditReporter = new CreditReporter(validCustomerStateRepository, customerCreditRepository, contractStateRepository);
//        creditReporter.reportCustomerCredits();
//        creditReporter.reportCreditQuality();
    }

}
