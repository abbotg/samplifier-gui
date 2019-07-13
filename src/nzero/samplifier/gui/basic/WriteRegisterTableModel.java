package nzero.samplifier.gui.basic;

import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.DataType;
import nzero.samplifier.model.Register;

import javax.swing.*;

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
        assert aValue instanceof String || aValue instanceof Boolean;

        try {
            flattenAndSetData(getBitMap(rowIndex), aValue);
        } catch (InvalidInputLengthException e) {
            JOptionPane.showMessageDialog(null, "Input out of range");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Input data is not binary");
        }

        fireTableCellUpdated(rowIndex, columnIndex);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }

    }

    private void flattenAndSetData(BitMap bitMap, Object data) throws NumberFormatException, InvalidInputLengthException {
        int val = 0;
        if (bitMap.getDataType() == DataType.BOOL) {
            assert data instanceof Boolean; // this should be handled elsewhere
            val = (Boolean) data ? 1 : 0;
        } else {
            assert data instanceof String;
            switch (bitMap.getDataType()) {
                case BIN:
                    val = Integer.parseUnsignedInt((String) data, 2);
                    break;
                case DEC:
                    val = Integer.parseUnsignedInt((String) data, 10);
                    break;
                case HEX:
                    val = Integer.parseUnsignedInt((String) data, 16);
                    break;
            }
            if (val > bitMap.getDataMaxVal() || val < bitMap.getDataMinVal()) {
                throw new InvalidInputLengthException();
            }
        }
        getRegister().setData(bitMap, val);
    }

}
