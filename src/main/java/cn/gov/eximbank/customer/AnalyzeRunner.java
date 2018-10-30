package cn.gov.eximbank.customer;

import cn.gov.eximbank.customer.analyzer.ContractAnalyzer;
import cn.gov.eximbank.customer.model.Contract;
import cn.gov.eximbank.customer.model.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnalyzeRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzeRunner.class);

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public void run(String... args) throws Exception {
        if (args.length != 2) {
            logger.error("Arguments is not enough");
        }
        String argument = args[1];
        if (argument.equals("contracts")) {
            readContracts();
        }
        else if (argument.equals("customers")) {

        }
//        ContractAnalyzer analyzer = new ContractAnalyzer(contractRepository);
        Contract contract = new Contract("1234", 1000.0, "2341241");
        contractRepository.save(contract);
        for (Contract newCon : contractRepository.findAllByCustomerId("2341241")) {
            logger.info(newCon.getId());
        }
    }

    private void readContracts() {
        ContractAnalyzer contractAnalyzer = new ContractAnalyzer(contractRepository);
        contractAnalyzer.readContractFiles();
    }
}
