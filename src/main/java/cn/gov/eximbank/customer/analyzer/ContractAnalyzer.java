package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.Contract;
import cn.gov.eximbank.customer.model.ContractRepository;
import cn.gov.eximbank.customer.util.CellContentException;
import cn.gov.eximbank.customer.util.CellContentUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContractAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(ContractAnalyzer.class);

    private static String dataPathStr = "data";

    private static String[] contractFileNames = new String[] {"201703.xlsx"};

    private static String[] remainingColumnNames = new String[] {"remaining1701"};

    private ContractRepository contractRepository;

    public ContractAnalyzer(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public void readContractFiles() {
        for (int i = 0; i != contractFileNames.length; ++i) {
            String fileName = contractFileNames[i];
            String remainingColumnName = remainingColumnNames[i];
            File contractFile = new File(dataPathStr + File.separator + fileName);
            if (contractFile.exists()) {
                readContracts(contractFile, remainingColumnName);
            }
            else {
                logger.error(contractFile.getAbsolutePath() + " not exist");
            }
        }
    }

    private void readContracts(File dealFile, String remainingColumnName) {
        try {
            Workbook wb = WorkbookFactory.create(dealFile);
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 5; i != sheet.getLastRowNum() + 1; ++i) {
                Row row = sheet.getRow(i);
                try {
                    String contractId = CellContentUtil.getStringContent(row.getCell(7));
                    double remaining = CellContentUtil.getNumericContent(row.getCell(10));
                    String customerId = CellContentUtil.getStringContent(row.getCell(49));
                    Contract contract = contractRepository.findContractById(contractId);
                    if (contract == null) {
                        createContract(contractId, remaining, customerId, remainingColumnName);
                    }
                    else {
                        updateContract(contractId, remaining, customerId, remainingColumnName);
                    }
                } catch (CellContentException e) {
                    logger.error("Read error : row : " + e.getRowIndex() + ", column : " + e.getColumnIndex());
                }
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createContract(String contractId, double remaining, String customerId, String remainingColumnName) {
        //TODO
    }

    private void updateContract(String contractId, double remaining, String customerId, String remainingColumnName) {
        //TODO
    }

}
