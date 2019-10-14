package nzero.samplifier.gui.advanced;

import nzero.samplifier.model.Register;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class BulkRegisterTableModel extends AbstractTableModel {
    private List<Register> registers;
    private static String[] columnNames = {
            "Register",
            "Type",
            "Width",
            "Data"
    };

    public BulkRegisterTableModel(List<Register> registers) {
        this.registers = registers;
    }

    @Override
    public int getRowCount() {
        return registers.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Register register = registers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return register.getName();
            case 1:
                switch (register.getRegisterType()) {
                    case READ: return "Read";
                    case WRITE: return "Write";
                    case READ_WRITE: return "Read/Write";
                }
            case 2:
                return Integer.toString(register.getBitWidth());
            case 3:
                return register.getBinaryString();
            default:
                return null; // TODO
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 3 && registers.get(rowIndex).isWritable();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        assert columnIndex == 3;
        assert aValue instanceof String;
        String string = (String) aValue;
        Register register = registers.get(rowIndex);
        int data;
        try {
            data = Integer.parseUnsignedInt((String) aValue, 2);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Input out of range", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (string.length() != register.getBitWidth()) {
            JOptionPane.showMessageDialog(null, "Input data is not binary", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        register.setData(data);
    }
}
