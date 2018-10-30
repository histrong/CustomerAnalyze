package cn.gov.eximbank.customer.analyze;

import cn.gov.eximbank.customer.model.Deal;
import cn.gov.eximbank.customer.util.CellContentException;
import cn.gov.eximbank.customer.util.CellContentUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

public class DealAnalyze {

    public List<Deal> getDeals(Sheet sheet) {
        List<Deal> deals = new ArrayList<Deal>();
        for (int i = 5; i != sheet.getLastRowNum() + 1; ++i) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(7);
            try {
                System.out.println(CellContentUtil.getStringContent(cell));
            } catch (CellContentException e) {

            }
        }
        return deals;
    }
}
