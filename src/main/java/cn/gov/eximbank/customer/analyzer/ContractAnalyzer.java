package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.util.CellContentUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ContractAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(ContractAnalyzer.class);

    private static String dataPathStr = "data";

    private static String[] contractFileNames = new String[] {
            "201809.xlsx", "201806.xlsx", "201803.xlsx",
            "201712.xlsx", "201709.xlsx", "201706.xlsx", "201703.xlsx"};

    private static String[] periods = new String[] {"201809", "201806", "201803",
            "201712", "201709", "201706", "201703"};

    private ContractRepository contractRepository;

    private ContractStateRepository contractStateRepository;

    private CustomerRepository customerRepository;

    private ValidCustomerStateRepository validCustomerStateRepository;

    public ContractAnalyzer(ContractRepository contractRepository,
                            ContractStateRepository contractStateRepository,
                            CustomerRepository customerRepository,
                            ValidCustomerStateRepository validCustomerStateRepository) {
        this.contractRepository = contractRepository;
        this.contractStateRepository = contractStateRepository;
        this.customerRepository = customerRepository;
        this.validCustomerStateRepository = validCustomerStateRepository;
    }

    public void readContractFiles() {
//        for (int i = 0; i != contractFileNames.length; ++i)
        {
            int i = 0;
            String fileName = contractFileNames[i];
            String period = periods[i];
            File contractFile = new File(dataPathStr + File.separator + fileName);
            if (contractFile.exists()) {
                readContracts(contractFile, period);
            }
            else {
                logger.error(contractFile.getAbsolutePath() + " not exist");
            }
        }
    }

    private void readContracts(File contractFile, String period) {
        try {
            Workbook wb = WorkbookFactory.create(contractFile);
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 5; i != sheet.getLastRowNum() + 1; ++i) {
                Row row = sheet.getRow(i);
                ContractState contractState = readContractState(row, period);
                ValidCustomerState validCustomerState = readValidCustomerState(row, period);
                updateContractState(contractState);
                updateValidCustomerState(validCustomerState);
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ContractState readContractState(Row row, String period) {
        String contractId = getContractId(row);
        double remaining = CellContentUtil.getNumericContent(row.getCell(10));
        String qualityLevelStr = "";
        if (period.startsWith("2018")) {
            qualityLevelStr = CellContentUtil.getStringContent(row.getCell(109));
        }
        else {
            qualityLevelStr = CellContentUtil.getStringContent(row.getCell(104));
        }
        String customerId = CellContentUtil.getStringContent(row.getCell(49));
        String branch = CellContentUtil.getStringContent(row.getCell(8));
        String investDirection = CellContentUtil.getStringContent(row.getCell(31));
        return new ContractState(contractId, period, remaining, qualityLevelStr, customerId,
                branch, investDirection);
    }

    private ValidCustomerState readValidCustomerState(Row row, String period) {
        String customerId = CellContentUtil.getStringContent(row.getCell(49));
        String customerName = CellContentUtil.getStringContent(row.getCell(50));
        double remaining = CellContentUtil.getNumericContent(row.getCell(10));
        String scale = CellContentUtil.getStringContent(row.getCell(57));
        String ownership = ignoreUsedContent(CellContentUtil.getStringContent(row.getCell(60)));
        String industry = ignoreUsedContent(CellContentUtil.getStringContent(row.getCell(55)));
        String branch = "";
        Optional<Customer> customerInDB = customerRepository.findById(customerId);
        if (customerInDB.isPresent()) {
            branch = customerInDB.get().getBranch();
        }
        else {
            branch = CellContentUtil.getStringContent(row.getCell(8));
        }
        String province = ignoreUsedContent(CellContentUtil.getStringContent(row.getCell(68)));
        return new ValidCustomerState(customerId, customerName, period, remaining, scale, ownership,
                industry, branch, province);
    }

    private String getContractId(Row row) {
        String contractId = CellContentUtil.getStringContent(row.getCell(7));
        if (contractId == null || contractId.equals("")) {
            contractId = CellContentUtil.getStringContent(row.getCell(2));
        }
        return contractId.trim();
    }

    private void updateContractState(ContractState contractState) {
        Optional<Contract> contractInDB = contractRepository.findById(contractState.getContractId());
        if (!contractInDB.isPresent()) {
            contractRepository.save(new Contract(contractState.getContractId(),
                    contractState.getCustomerId()));
        }

        ContractState contractStateInDB = contractStateRepository.findByPeriodAndContractId(contractState.getPeriod(), contractState.getContractId());
        if (contractStateInDB == null) {
            contractStateRepository.save(contractState);
        }
        else {
            contractStateInDB.setRemaining(contractStateInDB.getRemaining() + contractState.getRemaining());
            contractStateRepository.save(contractStateInDB);
        }
    }

    private void updateValidCustomerState(ValidCustomerState validCustomerState) {
        ValidCustomerState validCustomerStateInDB = validCustomerStateRepository
                .findByPeriodAndCustomerId(validCustomerState.getPeriod(), validCustomerState.getCustomerId());
        if (validCustomerStateInDB == null) {
            validCustomerStateRepository.save(validCustomerState);
        }
        else {
            validCustomerStateInDB.setRemaining(validCustomerStateInDB.getRemaining()
                    + validCustomerState.getRemaining());
            validCustomerStateRepository.save(validCustomerStateInDB);
        }
    }

    private String ignoreUsedContent(String content) {
        if (content == null || content.equals("") || content.startsWith(" ") || content.contains("-")) {
            return "";
        }
        else {
            return content;
        }
    }

}
