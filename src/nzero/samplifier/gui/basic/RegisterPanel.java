package nzero.samplifier.gui.basic;

import nzero.samplifier.gui.SamplifierMainWindow;
import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.DataType;
import nzero.samplifier.model.Register;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An individual view of a register, containing a single JTable
 */
public class RegisterPanel extends JPanel {
    private @NotNull Register register;
    private JTable table;
    private AbstractTableModel tableModel;
    private SamplifierMainWindow parent;

    public RegisterPanel(@NotNull Register register, SamplifierMainWindow parent) {
        super(new GridLayout(1, 0));
        this.register = register;
        this.parent = parent;

        tableModel = new RegisterTableModel(register, parent);

        table = new JTable(tableModel) {
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

        table.setPreferredScrollableViewportSize(new Dimension(500, 120));
        table.setFillsViewportHeight(true);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                int col = table.columnAtPoint(point);
                if (table.getSelectedRow() != -1 && mouseEvent.getClickCount() == 2) {
                    BitMap bitMap = register.getBitMaps().get(row);
                    if (bitMap.getDataType() == DataType.BIN) {
                        BinaryInputPopup.display(register, bitMap, parent);
                    }
                }
            }
        });

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();
                if (table.getSelectedRow() != -1 && e.getKeyChar() == KeyEvent.VK_SPACE) {
                    BitMap bitMap = register.getBitMaps().get(row);
                    if (bitMap.getDataType() == DataType.BIN) {
                        BinaryInputPopup.display(register, bitMap, parent);
                    }
                    e.consume();
                }
            }
        });

        // Wrap the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the scroll pane to this panel
        add(scrollPane);
    }

    public void updateRegisterData() {
        tableModel.fireTableDataChanged();
        table.repaint();
    }

    @NotNull
    public Register getRegister() {
        return register;
    }

}
