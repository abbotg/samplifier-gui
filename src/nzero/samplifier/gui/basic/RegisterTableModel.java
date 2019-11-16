package nzero.samplifier.gui.basic;

import nzero.samplifier.gui.InvalidInputLengthException;
import nzero.samplifier.gui.SamplifierMainWindow;
import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.DataType;
import nzero.samplifier.model.Register;
import nzero.samplifier.model.RegisterType;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class RegisterTableModel extends AbstractTableModel {

    private Register register;
    private SamplifierMainWindow parent;
    private static String[] columnNames = {
            "Data Type (Range)",
            "Description",
            "Data"
    };

    private static boolean DEBUG = true; // TODO: remove


    RegisterTableModel(Register register, SamplifierMainWindow parent) {
        this.register = register;
        this.parent = parent;
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
                return getFormattedRange(bitMap);
            case 1:
                return bitMap.getName();
            case 2:
                return getFormattedData(bitMap); // TODO: Extra stuff added here to display proper thing
            default:
                return null;
        }

    }

    private Object getFormattedRange(BitMap bitMap) {
        switch (bitMap.getDataType()) {
            case BOOL:
                return "Boolean";
            case BIN:
                return String.format("Binary (%s)", bitMap.getFormattedRange());
            case DEC:
                return String.format("Decimal (%s)", bitMap.getFormattedRange());
            case HEX:
                return String.format("Hexadecimal (%s)", bitMap.getFormattedRange());
            default:
                return "";
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
                return "";
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
//    @Override
//    public Class getColumnClass(int c) {
//        return getValueAt(0, c).getClass();
//        return Boolean.class;
//    }

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
        System.out.println(register.getBinaryString());
        System.out.println("--------------------------");
    }

    public BitMap getBitMap(int rowIndex) {
        return register.getBitMaps().get(rowIndex);
    }


    @Override
    public boolean isCellEditable(int row, int col) {
//        if (register.getRegisterType() == RegisterType.READ) {
//            return false;
//        } else { // WRITE, READ/WRITE
//            return col == 2; // TODO: globalize this
//        }
        return col == 2 && register.isWritable(); // TODO: BinaryInputPopup and decimal, hex, support
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (register.getRegisterType() != RegisterType.READ) { // its a WRITE, READ/WRITE
            if (DEBUG) {
                System.out.printf("Setting value at %d, %d to %s (an instance of %s)%n",
                        rowIndex, columnIndex, aValue, aValue.getClass());
            }

            assert columnIndex == 2; // only editable column
            assert aValue instanceof String || aValue instanceof Boolean;

            BitMap bitMap = getBitMap(rowIndex);
            try {
                flattenAndSetData(bitMap, aValue);
            } catch (InvalidInputLengthException e) {
                JOptionPane.showMessageDialog(null, String.format("Input %s out of range, must be %s", e.getInput(), bitMap.getFormattedRange()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Input data is not binary");
            }

            fireTableCellUpdated(rowIndex, columnIndex);
            parent.fireWriteRegistersDataChange();

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
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
                throw new InvalidInputLengthException(data);
            }
        }
        getRegister().setData(bitMap, val);
    }

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
