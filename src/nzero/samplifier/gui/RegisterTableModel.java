package nzero.samplifier.gui;

import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.Register;

import javax.swing.table.AbstractTableModel;

/**
 * "Basic" mode
 */
public class RegisterTableModel extends AbstractTableModel {
    public static boolean DEBUG = true;
    private Register register;
    private static String[] columnNames = {
            "Bits (Length)",
            "Description",
            "Data"
    };

    public RegisterTableModel(Register register) {
        this.register = register;
    }

    /**
     * Required to implement
     */
    @Override
    public int getRowCount() {
        return register.getNumMappings();
    }

    /**
     * Required to implement
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Required to implement
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // BitMaps correspond to rows in the table
        BitMap bitMap = register.getBitMaps().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return bitMap.getBitRange() + " (" + bitMap.getLength() + ")";
            case 1:
                return bitMap.getName();
            case 2:
                return bitMap.getData();
            default:
                return null;
        }

    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
//        return Boolean.class;
    }



    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (register.isWritable()) {
            return col == 2; // allow column 2 (data) to be edited
        } else { // it's a read register
            return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (DEBUG) {
            System.out.println("Setting value at " + rowIndex + "," + columnIndex
                    + " to " + aValue
                    + " (an instance of "
                    + aValue.getClass() + ")");
        }

        assert columnIndex == 2; // only editable column
        register.getBitMaps().get(rowIndex).setData(aValue);
        fireTableCellUpdated(rowIndex, columnIndex);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }

    }

//    @Deprecated
//    public static Object[][] generateRegisterTable(Register register) {
//        Object[][] data = new Object[register.getNumMappings()][columnNames.length];
//        for (BitMap bitMap : register.getBitMaps())
//        for (int i = 0; i < data.length; i++) {
//            data[i] = new Object[] {
//                    bitMap.getBitRange() + " (" + bitMap.getLength() + ')',
//                    bitMap.getName(),
//                    bitMap.getData()
//            };
//        }
//        return data;
//    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

}
