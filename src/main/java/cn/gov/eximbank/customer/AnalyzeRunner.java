package cn.gov.eximbank.customer;

import cn.gov.eximbank.customer.analyzer.*;
import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.reporter.CustomerReporter;
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

    private void report() {
        Reporter reporter = new Reporter(customerRepository, groupCustomerRepository,
                contractRepository, contractStateRepository);
        //reporter.reportMainInfo();
        //reporter.reportGovermentCustomerInfo();
//        reporter.reportCustomerScale();
//        reporter.reportOwnership();

        CustomerReporter customerReporter = new CustomerReporter(customerRepository,
                groupCustomerRepository, validCustomerStateRepository,
                contractRepository, contractStateRepository);
//        customerReporter.reportCustomers();
        customerReporter.reportGroupCustomers();
//        customerReporter.reportBranchs();

        QualityLevelReporter qualityLevelReporter = new QualityLevelReporter(customerRepository, groupCustomerRepository,
                contractRepository, contractStateRepository);
//        qualityLevelReporter.reportQualityLevel();
    }

}
