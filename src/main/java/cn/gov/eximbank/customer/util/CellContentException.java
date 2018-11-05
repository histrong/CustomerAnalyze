package cn.gov.eximbank.customer.util;

@Deprecated
public class CellContentException extends Exception {

    private int row;

    private int column;

    public CellContentException(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRowIndex() {
        return row;
    }

    public int getColumnIndex() {
        return column;
    }
}
