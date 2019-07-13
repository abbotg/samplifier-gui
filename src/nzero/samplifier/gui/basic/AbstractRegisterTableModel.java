package nzero.samplifier.gui.basic;

import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.Register;

import javax.swing.table.AbstractTableModel;

public abstract class AbstractRegisterTableModel extends AbstractTableModel {

    private Register register;
    private static String[] columnNames = {
            "Bits (Length)",
            "Description",
            "Data"
    };


    AbstractRegisterTableModel(Register register) {
        this.register = register;
    }

    /**
     * Required to implement
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // BitMaps correspond to rows in the table
        BitMap bitMap = getBitMap(rowIndex);
        switch (columnIndex) {
            case 0:
                return bitMap.getBitRange() + " (" + bitMap.getLength() + ")";
            case 1:
                return bitMap.getName();
            case 2:
                return getFormattedData(bitMap); // TODO: Extra stuff added here to display proper thing
            default:
                return null;
        }

    }

    private Object getFormattedData(BitMap bitMap) {
        int data = register.getData(bitMap);
        switch (bitMap.getDataType()) {
            case BOOL:
                return data == 1 ? Boolean.TRUE : Boolean.FALSE;
            case BIN:
                return String.format("%" + bitMap.getLength() + "s", Integer.toBinaryString(data)).replace(' ', '0');
            case DEC:
                return Integer.toString(data);
            case HEX:
                return Integer.toHexString(data);
            default:
                return null; // TODO: ?
        }
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

    void printDebugData() {
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
        System.out.print("Register data: ");
        System.out.println(Integer.toBinaryString(register.getData()));
        System.out.println("--------------------------");
    }

    public BitMap getBitMap(int rowIndex) {
        return register.getBitMaps().get(rowIndex);
    }


    @Override
    public abstract boolean isCellEditable(int row, int col);

    @Override
    public abstract void setValueAt(Object aValue, int rowIndex, int columnIndex);

    //    @Deprecated
//    public static Object[][] generateRegisterTable(Register register) {
//        Object[][] data = new Object[register.getNumMappings()][columnNames.length];
//        for (BitMap bitMap : register.getBitMaps())
//        for (int i = 0; i < data.length; i++) {
//            data[i] = new Object[] {
//                    bitMap.getBitRange() + " (" + bitMap.getLength() + ')',
//                    bitMap.getName(),
//                    bitMap.getFormattedData()
//            };
//        }
//        return data;
//    }


    Register getRegister() {
        return register;
    }
}
