package cn.gov.eximbank.customer.analyzer;

import cn.gov.eximbank.customer.model.Customer;
import cn.gov.eximbank.customer.model.CustomerRepository;
import cn.gov.eximbank.customer.model.GroupCustomer;
import cn.gov.eximbank.customer.model.GroupCustomerRepository;
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
import java.util.Date;
import java.util.Optional;

public class CustomerAnalyzer {
    private static Logger logger = LoggerFactory.getLogger(CustomerAnalyzer.class);

    private static String dataPathStr = "data";

    private static String customerFileName = "customers.xlsx";

    private CustomerRepository customerRepository;

    private GroupCustomerRepository groupCustomerRepository;

    public CustomerAnalyzer(CustomerRepository customerRepository, GroupCustomerRepository groupCustomerRepository) {
        this.customerRepository = customerRepository;
        this.groupCustomerRepository = groupCustomerRepository;
    }


    public void readCustomerFiles() {
        File customerFile = new File(dataPathStr + File.separator + customerFileName);
        if (customerFile.exists()) {
            readCustomers(customerFile);
        } else {
            logger.error(customerFile.getAbsolutePath() + " not exist");
        }
    }

    private void readCustomers(File customerFile) {
        try {
            Workbook wb = WorkbookFactory.create(customerFile);
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i != sheet.getLastRowNum() + 1; ++i) {
                Row row = sheet.getRow(i);
                GroupCustomer groupCustomer = readGroup(row);
                Customer customer = readCustomer(row, groupCustomer);
                System.out.println(i + " : " + customer.getId());
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GroupCustomer readGroup(Row row) {
        try {
            String groupId = CellContentUtil.getStringContent(row.getCell(4));
            String groupName = CellContentUtil.getStringContent(row.getCell(5));
            if (groupId != null && groupName != null && !groupId.equals("")) {
                Optional<GroupCustomer> groupCustomerInDB = groupCustomerRepository.findById(groupId);
                if (!groupCustomerInDB.isPresent()) {
                    GroupCustomer groupCustomer = new GroupCustomer(groupId, groupName);
                    groupCustomerRepository.save(groupCustomer);
                    return groupCustomer;
                }
                else {
                    groupCustomerInDB.get();
                }
            } else {
                return null;
            }
        } catch (CellContentException e) {
            logger.error("Read error : row : " + e.getRowIndex() + ", column : " + e.getColumnIndex());
            e.printStackTrace();
        }
        return null;
    }

    private Customer readCustomer(Row row, GroupCustomer groupCustomer) {
        try {
            String customerId = CellContentUtil.getStringContent(row.getCell(1));
            String customerName = CellContentUtil.getStringContent(row.getCell(2));
            Date relationshipDate = CellContentUtil.getDateContent(row.getCell(6));
            Date firstDealDate = CellContentUtil.getDateContent(row.getCell(7));
            Date lastCreditDate = CellContentUtil.getDateContent(row.getCell(8));
            Date lastDealClearDate = CellContentUtil.getDateContent(row.getCell(9));
            String branch = getBranch(CellContentUtil.getStringContent(row.getCell(10)));
            String manager = CellContentUtil.getStringContent(row.getCell(11));
            Customer customer = null;
            if (groupCustomer != null) {
                customer = new Customer(customerId, customerName, groupCustomer.getId(), "", branch, relationshipDate,
                        firstDealDate, lastCreditDate, lastDealClearDate, manager);
            }
            else {
                customer = new Customer(customerId, customerName, "", "", branch, relationshipDate,
                        firstDealDate, lastCreditDate, lastDealClearDate, manager);
            }
            customerRepository.save(customer);
            return customer;
        } catch (CellContentException e) {
            logger.error("Read error : row : " + e.getRowIndex() + ", column : " + e.getColumnIndex());
            e.printStackTrace();
        }
        return null;
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
