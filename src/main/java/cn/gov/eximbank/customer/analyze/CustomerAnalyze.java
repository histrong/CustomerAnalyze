package cn.gov.eximbank.customer.analyze;

import cn.gov.eximbank.customer.model.Deal;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class CustomerAnalyze {

    private static Logger logger = LoggerFactory.getLogger(CustomerAnalyze.class);

    private static String dataPathStr = "data";

    private static String[] dealNames = new String[] {"201703.xlsx"};

    private DealAnalyze dealAnalyze;

    public static void main(String[] args) {
        CustomerAnalyze customerAnalyze = new CustomerAnalyze();
        for (String fileName : dealNames) {
            File dealFile = new File(dataPathStr + File.separator + fileName);
            if (dealFile.exists()) {
                customerAnalyze.readDealFile(dealFile);
            }
            else {
                logger.error(dealFile.getAbsolutePath() + " not exist");
            }
        }
    }

    public CustomerAnalyze() {
        dealAnalyze = new DealAnalyze();
    }

    private boolean readDealFile(File dealFile) {
        try {
            Workbook wb = WorkbookFactory.create(dealFile);
            Sheet sheet = wb.getSheetAt(0);
            List<Deal> deals = dealAnalyze.getDeals(sheet);
            logger.info(dealFile.getName() + " : " +deals.size());
            return true;
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
