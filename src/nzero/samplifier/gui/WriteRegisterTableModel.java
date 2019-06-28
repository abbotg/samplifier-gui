package nzero.samplifier.gui;

import nzero.samplifier.model.Register;

/**
 * "Basic" mode
 */
public class WriteRegisterTableModel extends AbstractRegisterTableModel {
    public static boolean DEBUG = true;


    public WriteRegisterTableModel(Register register) {
        super(register);
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (getRegister().isWritable()) {
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
        getRegister().getBitMaps().get(rowIndex).setData(aValue);
        fireTableCellUpdated(rowIndex, columnIndex);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }

    }

}
