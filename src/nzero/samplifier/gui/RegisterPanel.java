package nzero.samplifier.gui;

import nzero.samplifier.model.Register;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private Register register;

    public RegisterPanel(Register register) {
        super(new GridLayout(1, 0));
        this.register = register;

        AbstractTableModel tableModel = register.isWritable()
                ? new WriteRegisterTableModel(register) : new ReadRegisterTableModel(register);

        JTable table = new JTable(tableModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 2) {
                    Object value = getValueAt(row, column);
                    if (value != null) {
                        System.out.println(value.getClass());
                        return getDefaultEditor(value.getClass());
                    }
                }
                return super.getCellEditor(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 2) {
                    Object value = getValueAt(row, column);
                    return getDefaultRenderer(value.getClass());
                }
                return super.getCellRenderer(row, column);
            }
        };

        table.setPreferredScrollableViewportSize(new Dimension(500, 80));
        table.setFillsViewportHeight(true);

        // Wrap the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the scroll pane to this panel
        add(scrollPane);
    }

}
