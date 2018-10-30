package cn.gov.eximbank.customer.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellContentUtil {

    private static Logger logger = LoggerFactory.getLogger(CellContentUtil.class);

    public static String getStringContent(Cell cell) throws CellContentException {
        if (cell.getCellTypeEnum().equals(CellType.STRING)) {
            return cell.getStringCellValue();
        }
        else if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            return Double.toString(cell.getNumericCellValue());
        }
        else {
            throw new CellContentException(cell.getRowIndex(), cell.getColumnIndex());
        }
    }
}
