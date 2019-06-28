package nzero.samplifier.gui;

import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.Register;

import javax.swing.table.AbstractTableModel;

/**
 * "Basic" mode
 */
public class ReadRegisterTableModel extends AbstractRegisterTableModel {
    public static boolean DEBUG = true;

    public ReadRegisterTableModel(Register register) {
        super(register);
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // read only
    }

}
