package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.util.CellContentUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ContributionAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(ContributionAnalyzer.class);

    private static String dataPathStr = "data";

    private static String contributionFileName = "contribution.xlsx";

    private static String period = "201712";

    private static String nowPeriod = "201809";

    private IntermediateContributionRepository intermediateContributionRepository;

    private DemandDepositContributionRepository demandDepositContributionRepository;

    private TimeDepositContributionRepository timeDepositContributionRepository;

    private LoanContributionRepository loanContributionRepository;

    private CustomerContributionRepository customerContributionRepository;

    private ValidCustomerStateRepository validCustomerStateRepository;

    public ContributionAnalyzer(IntermediateContributionRepository intermediateContributionRepository,
                                DemandDepositContributionRepository demandDepositContributionRepository,
                                TimeDepositContributionRepository timeDepositContributionRepository,
                                LoanContributionRepository loanContributionRepository,
                                CustomerContributionRepository customerContributionRepository,
                                ValidCustomerStateRepository validCustomerStateRepository) {
        this.intermediateContributionRepository = intermediateContributionRepository;
        this.demandDepositContributionRepository = demandDepositContributionRepository;
        this.timeDepositContributionRepository = timeDepositContributionRepository;
        this.loanContributionRepository = loanContributionRepository;
        this.customerContributionRepository = customerContributionRepository;
        this.validCustomerStateRepository = validCustomerStateRepository;
    }

    public void readContributionFiles() {
        File contributionFile = new File(dataPathStr + File.separator + contributionFileName);
        if (contributionFile.exists()) {
            readContributionFile(contributionFile);
        }
        else {
            logger.error("Contribution file does not exists");
        }
    }

    private void readContributionFile(File contributionFile) {
        try {
            Workbook wb = WorkbookFactory.create(contributionFile);
            Sheet intermediateSheet = wb.getSheetAt(1);
            Sheet demandDepositSheet = wb.getSheetAt(2);
//            Sheet timeDepositSheet = wb.getSheetAt(3);
            Sheet loanSheet = wb.getSheetAt(0);
//            readIntermediateSheet(intermediateSheet);
            readDemandDepositSheet(demandDepositSheet);
//            readTimeDepositSheet(timeDepositSheet);
//            readLoanSheet(loanSheet);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    private void readIntermediateSheet(Sheet intermediateSheet) {
        for (int i = 1; i != intermediateSheet.getLastRowNum() + 1; ++i) {
            Row row = intermediateSheet.getRow(i);
            readIntermediateContribution(row);
        }
    }

    private void readIntermediateContribution(Row row) {
        String contractId = CellContentUtil.getStringContent(row.getCell(7));
        String customerId = CellContentUtil.getStringContent(row.getCell(0));
        String customerName = CellContentUtil.getStringContent(row.getCell(1));
        String industry = CellContentUtil.getStringContent(row.getCell(4));
        String branch = getBranch(CellContentUtil.getStringContent(row.getCell(6)));
        double contribution = CellContentUtil.getNumericContent(row.getCell(12));
        String scale = CellContentUtil.getStringContent(row.getCell(2));
        String ownership = CellContentUtil.getStringContent(row.getCell(3));

//        ValidCustomerState validCustomerState = validCustomerStateRepository
//                .findByPeriodAndCustomerId(period, customerId);
//        if (validCustomerState == null) {
//            logger.error("Customer cannot find in 201712 " + customerId);
//        }
//        else {
        IntermediateContribution intermediateContribution
                = new IntermediateContribution(contractId, customerId, contribution, branch);
        Optional<IntermediateContribution> intermediateContributionInDB = intermediateContributionRepository.findById(contractId);
        if (intermediateContributionInDB.isPresent()) {
            logger.error("Loan is exist : " + contractId);
            intermediateContributionRepository.save(new IntermediateContribution(contractId, customerId, contribution + intermediateContributionInDB.get().getContribution(), branch));
        }
        else {
            intermediateContributionRepository.save(intermediateContribution);
        }
        updateCustomerContribution(customerId, customerName, scale, ownership, industry, branch, contribution, CustomerContribution.EContributionType.Intermediate);
//        }
    }

    private void readDemandDepositSheet(Sheet demandDepositSheet) {
        for (int i = 1; i != demandDepositSheet.getLastRowNum() + 1; ++i) {
            readDemandDepositContribution(demandDepositSheet.getRow(i));
        }
    }

    private void readDemandDepositContribution(Row row) {
        String contractId = CellContentUtil.getStringContent(row.getCell(0));
        String customerId = CellContentUtil.getStringContent(row.getCell(1));
        String customerName = CellContentUtil.getStringContent(row.getCell(2));
        String scale = CellContentUtil.getStringContent(row.getCell(3));
        String ownership = CellContentUtil.getStringContent(row.getCell(4));
        String industry = CellContentUtil.getStringContent(row.getCell(5));
        String branch = getBranch(CellContentUtil.getStringContent(row.getCell(7)));
        double contribution = CellContentUtil.getNumericContent(row.getCell(13));
//        ValidCustomerState validCustomerState = validCustomerStateRepository.findByPeriodAndCustomerId(period, customerId);
//        if (validCustomerState == null) {
//            logger.error("Customer cannot find in 201712 " + customerId);
//        }
//        else {
        DemandDepositContribution demandDepositContribution =
                new DemandDepositContribution(contractId, customerId, contribution, branch);
        Optional<DemandDepositContribution> demandDepositContributionInDB = demandDepositContributionRepository.findById(contractId);
        if (demandDepositContributionInDB.isPresent()) {
            demandDepositContributionRepository.save(new DemandDepositContribution(contractId, customerId,
                    demandDepositContributionInDB.get().getContribution() + contribution, branch));
        }
        else {
            demandDepositContributionRepository.save(demandDepositContribution);
        }
        updateCustomerContribution(customerId, customerName, scale, ownership, industry, branch, contribution, CustomerContribution.EContributionType.DemandDeposit);
//        }
    }

    private void readTimeDepositSheet(Sheet timeDepositSheet) {
        for (int i = 1; i != timeDepositSheet.getLastRowNum() + 1; ++i) {
            readTimeDepositContribution(timeDepositSheet.getRow(i));
        }
    }

    private void readTimeDepositContribution(Row row) {

    }

    private void readLoanSheet(Sheet loanSheet) {
        for (int i = 1; i != loanSheet.getLastRowNum() + 1; ++i) {
            Row row = loanSheet.getRow(i);
            readLoanContribution(row);
        }
    }

    private void readLoanContribution(Row row) {
        String contractId = CellContentUtil.getStringContent(row.getCell(0));
        String customerId = CellContentUtil.getStringContent(row.getCell(2));
        String customerName = CellContentUtil.getStringContent(row.getCell(3));
        String industry = CellContentUtil.getStringContent(row.getCell(4));
        String branch = getBranch(CellContentUtil.getStringContent(row.getCell(8)));
        double contribution = CellContentUtil.getNumericContent(row.getCell(12));
        String scale = CellContentUtil.getStringContent(row.getCell(5));
        String ownership = CellContentUtil.getStringContent(row.getCell(6));

        LoanContribution loanContribution = new LoanContribution(contractId, customerId, contribution, branch);
        Optional<LoanContribution> loanContributionInDB = loanContributionRepository.findById(contractId);
        if (loanContributionInDB.isPresent()) {
            logger.error("Loan is exist : " + contractId);
            loanContributionRepository.save(new LoanContribution(contractId, customerId, contribution + loanContributionInDB.get().getContribution(), branch));
        }
        else {
            loanContributionRepository.save(loanContribution);
        }
        updateCustomerContribution(customerId, customerName, scale, ownership, industry, branch, contribution, CustomerContribution.EContributionType.Loan);
    }

    private void updateCustomerContribution(String customerId, String customerName, String scale,
                                            String ownership, String industry, String branch,
                                            double contribution, CustomerContribution.EContributionType type) {
        CustomerContribution customerContribution;
        Optional<CustomerContribution> customerContributionInDB = customerContributionRepository.findById(customerId);
        if (customerContributionInDB.isPresent()) {
            customerContribution = customerContributionInDB.get();
        }
        else {
            customerContribution = new CustomerContribution(customerId, customerName, scale, ownership, industry, branch, "");
        }
        customerContribution.addContribution(type, contribution);
        customerContributionRepository.save(customerContribution);
    }


    private void updateCustomerContribution(ValidCustomerState validCustomerState,
                                            double contribution,
                                            CustomerContribution.EContributionType type) {
        CustomerContribution customerContribution;
        Optional<CustomerContribution> customerContributionInDB = customerContributionRepository
                .findById(validCustomerState.getCustomerId());
        if (customerContributionInDB.isPresent()) {
            customerContribution = customerContributionInDB.get();
        }
        else {
            customerContribution = new CustomerContribution(validCustomerState);
        }
        customerContribution.addContribution(type, contribution);
        customerContributionRepository.save(customerContribution);
    }

    private String getBranch(String content) {
        if (content.contains("分行")) {
            int index = content.indexOf("行");
            return content.substring(0, index + 1);
        }
        else if (content.contains("部")) {
            int index = content.indexOf("部");
            return content.substring(0, index + 1);
        }
        else {
            logger.error("Can not read : " + content);
            return content;
        }
    }
}
