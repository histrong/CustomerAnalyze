package cn.gov.eximbank.customer;

import cn.gov.eximbank.customer.analyzer.ContractAnalyzer;
import cn.gov.eximbank.customer.analyzer.CustomerAnalyzer;
import cn.gov.eximbank.customer.model.ContractRepository;
import cn.gov.eximbank.customer.model.CustomerRepository;
import cn.gov.eximbank.customer.model.GroupCustomer;
import cn.gov.eximbank.customer.model.GroupCustomerRepository;
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
    private CustomerRepository customerRepository;

    @Autowired
    private GroupCustomerRepository groupCustomerRepository;

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

            }
//        ContractAnalyzer analyzer = new ContractAnalyzer(contractRepository);
//        Contract contract = new Contract("1234", 1000.0, "2341241");
//        contractRepository.save(contract);
//        for (Contract newCon : contractRepository.findAllByCustomerId("2341241")) {
//            logger.info(newCon.getId());
//        }
        }
    }

    private void readContracts() {
        ContractAnalyzer contractAnalyzer = new ContractAnalyzer(contractRepository);
        contractAnalyzer.readContractFiles();
    }

    private void readCustomers() {
        CustomerAnalyzer customerAnalyzer = new CustomerAnalyzer(customerRepository, groupCustomerRepository);
        customerAnalyzer.readCustomerFiles();
    }


}
