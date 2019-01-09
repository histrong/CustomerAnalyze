package cn.gov.eximbank.customer;

import cn.gov.eximbank.customer.analyzer.*;
import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.reporter.*;
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

    @Autowired
    private IntermediateContributionRepository intermediateContributionRepository;

    @Autowired
    private DemandDepositContributionRepository demandDepositContributionRepository;

    @Autowired
    private TimeDepositContributionRepository timeDepositContributionRepository;

    @Autowired
    private LoanContributionRepository loanContributionRepository;

    @Autowired
    private CustomerContributionRepository customerContributionRepository;

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
            } else if (argument.equals("contribution")) {
                readContributions();
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

    private void readContributions() {
        ContributionAnalyzer contributionAnalyzer =
                new ContributionAnalyzer(intermediateContributionRepository, demandDepositContributionRepository,
                        timeDepositContributionRepository, loanContributionRepository,
                        customerContributionRepository, validCustomerStateRepository);
        contributionAnalyzer.readContributionFiles();
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
//        customerDetailReporter.reportCustomerDetailsWithPeriodInBranch("201809", "厦门分行");

        RemainingReporter remainingReporter
                = new RemainingReporter(customerRepository, validCustomerStateRepository);
//        remainingReporter.reportRemainingDetails();

        QualityLevelReporter qualityLevelReporter = new QualityLevelReporter(customerRepository, groupCustomerRepository,
                contractRepository, contractStateRepository);
//        qualityLevelReporter.reportQualityLevel();

        CreditReporter creditReporter = new CreditReporter(validCustomerStateRepository, customerCreditRepository, contractStateRepository);
//        creditReporter.reportCustomerCredits();
//        creditReporter.reportCreditQuality();
//        creditReporter.reportCustomerCreditsInScalesAndOwnerShipWithPeriod("201809");
//        creditReporter.reportCreditDetailsWithBranch("201809");

        ContributionReporter contributionReporter = new ContributionReporter(customerContributionRepository,
                loanContributionRepository, intermediateContributionRepository,
                demandDepositContributionRepository, customerCreditRepository, validCustomerStateRepository);
//        contributionReporter.reportLoanContributionInBranch();
//        contributionReporter.reportIntermediateContributionInBranch();
//        contributionReporter.reportDepositContributionInBranch();;
//        contributionReporter.reportContributionInBranch();
        contributionReporter.reportContributionDetails();
//        contributionReporter.reportContributionWithCredit();
    }

}
