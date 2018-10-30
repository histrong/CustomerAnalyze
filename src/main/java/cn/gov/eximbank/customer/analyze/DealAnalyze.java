package cn.gov.eximbank.customer.analyze;

import cn.gov.eximbank.customer.model.Deal;
import cn.gov.eximbank.customer.util.CellContentException;
import cn.gov.eximbank.customer.util.CellContentUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DealAnalyze {

    public List<Deal> getDeals(Sheet sheet) {
        List<Deal> deals = new ArrayList<Deal>();
        double total = 0.0;
        for (int i = 5; i != sheet.getLastRowNum() + 1; ++i) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(7);
            try {
                String dealAccount = CellContentUtil.getStringContent(row.getCell(7));
                double remaining = CellContentUtil.getNumericContent(row.getCell(10));
                total += remaining;
                String customerAccount = CellContentUtil.getStringContent(row.getCell(49));
                System.out.println(dealAccount + " : " + remaining + " : " + customerAccount);
            } catch (CellContentException e) {

            }
        }
        System.out.println("Total is : " + total);
        return deals;
    }
}
