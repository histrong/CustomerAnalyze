package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.CustomerCredit;
import cn.gov.eximbank.customer.model.CustomerCreditRepository;
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
import java.util.Date;
import java.util.Optional;

public class CreditAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(ContractAnalyzer.class);

    private static String dataPathStr = "data";

    private static String creditFileName = "credit.xlsx";

    private CustomerCreditRepository customerCreditRepository;

    public CreditAnalyzer(CustomerCreditRepository customerCreditRepository) {
        this.customerCreditRepository = customerCreditRepository;
    }

    public void readCreditFiles() {
        File customerFile = new File(dataPathStr + File.separator + creditFileName);
        if (customerFile.exists()) {
            readCreditFile(customerFile);
        } else {
            logger.error(customerFile.getAbsolutePath() + " not exist");
        }
    }

    private void readCreditFile(File customerFile) {
        try {
            Workbook wb = WorkbookFactory.create(customerFile);
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i != sheet.getLastRowNum() + 1; ++i) {
                Row row = sheet.getRow(i);
                readCustomerCreditRow(row);
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readCustomerCreditRow(Row row) {
        String customerId = CellContentUtil.getStringContent(row.getCell(1));
        String originCredit = CellContentUtil.getStringContent(row.getCell(3));
        String credit = adaptOriginCredit(originCredit);
        Date startDate = CellContentUtil.getDateContent(row.getCell(4));
        Optional<CustomerCredit> customerCreditInDB = customerCreditRepository.findById(customerId);
        if (customerCreditInDB.isPresent()) {
            if (customerCreditInDB.get().getStartDate() == null) {
                customerCreditRepository.save(new CustomerCredit(customerId, credit, startDate));
            }
            else if (startDate != null && customerCreditInDB.get().getStartDate().before(startDate)) {
                customerCreditRepository.save(new CustomerCredit(customerId, credit, startDate));
            }
        }
        else {
            customerCreditRepository.save(new CustomerCredit(customerId, credit, startDate));
        }
    }

    private String adaptOriginCredit(String originCredit) {
        if (originCredit.equals("不予评级")) {
            return originCredit;
        }

        int leftBraceIndex = originCredit.indexOf('(');
        int rightBraceIndex = originCredit.indexOf(')');
        String originCreditResult = originCredit.substring(0, leftBraceIndex);
        String creditType = originCredit.substring(leftBraceIndex + 1, rightBraceIndex);
        if (creditType.equals("9级")) {
            return mappingCreditResult(originCreditResult);
        }
        else {
            return originCreditResult;
        }
    }

    private String mappingCreditResult(String originCreditResult) {
        if (originCreditResult.equals("AAA")) {
            return "AAA";
        }
        else if (originCreditResult.equals("AA")) {
            return "AA+";
        }
        else if (originCreditResult.equals("A+")) {
            return "A";
        }
        else if (originCreditResult.equals("A")) {
            return "A-";
        }
        else if (originCreditResult.equals("A-")) {
            return "BBB";
        }
        else if (originCreditResult.equals("BBB")) {
            return "BB";
        }
        else if (originCreditResult.equals("BB")) {
            return "B";
        }
        else if (originCreditResult.equals("B")) {
            return "CCC";
        }
        else if (originCreditResult.equals("F")) {
            return "D";
        }
        else {
            return "未知";
        }
    }
}
