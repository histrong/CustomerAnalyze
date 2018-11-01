package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.*;
import cn.gov.eximbank.customer.util.CellContentException;
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

    public ContractAnalyzer(ContractRepository contractRepository,
                            ContractStateRepository contractStateRepository,
                            CustomerRepository customerRepository) {
        this.contractRepository = contractRepository;
        this.contractStateRepository = contractStateRepository;
        this.customerRepository = customerRepository;
    }

    public void readContractFiles() {
        //for (int i = 0; i != contractFileNames.length; ++i)
        {
            int i = 6;
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
                try {
                    String contractId = CellContentUtil.getStringContent(row.getCell(7));
                    if (contractId == null || contractId.equals("")) {
                        contractId = CellContentUtil.getStringContent(row.getCell(2));
                    }
                    contractId = contractId.trim();
                    double remaining = CellContentUtil.getNumericContent(row.getCell(10));
                    String customerId = CellContentUtil.getStringContent(row.getCell(49));
                    String scale = CellContentUtil.getStringContent(row.getCell(57));
                    updateCustomerInfo(row, customerId, scale, contractFile);
                    String qualityLevelStr = "";
                    if (period.startsWith("2018")) {
                        qualityLevelStr = CellContentUtil.getStringContent(row.getCell(109));
                    }
                    else {
                        qualityLevelStr = CellContentUtil.getStringContent(row.getCell(104));
                    }
                    Contract contract = contractRepository.findContractById(contractId);
                    if (contract == null) {
                        createContract(contractId, customerId);
                    }
                    ContractState contractState = new ContractState(contractId, period, remaining, qualityLevelStr);
                    updateContractState(contractState);
                } catch (CellContentException e) {
                    logger.error("Read error : row : " + e.getRowIndex() + ", column : " + e.getColumnIndex() + " of " + contractFile.getAbsolutePath());
                }
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateContractState(ContractState contractState) {
        ContractState contractStateInDB = contractStateRepository.findByPeriodAndContractId(contractState.getPeriod(), contractState.getContractId());
        if (contractStateInDB == null) {
            contractStateRepository.save(contractState);
        }
    }

    private void updateCustomerInfo(Row row, String customerId, String scale, File contractFile) {
        Optional<Customer> customerIdDB = customerRepository.findById(customerId);
        if (customerIdDB.isPresent()) {
            Customer customer = customerIdDB.get();
            if (customer.getScale() == null || customer.getScale().equals("")) {
                customer.setScale(scale);
                customerRepository.save(customer);
            }
        }
        else {
            try {
                String custmerName = CellContentUtil.getStringContent(row.getCell(44));
                Customer newCustomer = new Customer(customerId, custmerName, "", "", "",
                        null, null, null, null, "",0);
                customerRepository.save(newCustomer);
            } catch (CellContentException e) {
                e.printStackTrace();
            }
        }
    }

    private void createContract(String contractId, String customerId) {
        contractRepository.save(new Contract(contractId, customerId));
    }

}
