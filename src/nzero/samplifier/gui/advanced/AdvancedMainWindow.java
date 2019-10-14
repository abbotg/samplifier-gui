package nzero.samplifier.gui.advanced;

import nzero.samplifier.gui.GUICommon;
import nzero.samplifier.gui.SamplifierMainWindow;
import nzero.samplifier.gui.basic.RegisterPopOutWindow;
import nzero.samplifier.model.Register;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AdvancedMainWindow extends JFrame implements SamplifierMainWindow {
    private GUICommon common;
//    private JPanel rootPanel;
    private AbstractTableModel tableModel;

    public AdvancedMainWindow(GUICommon common) {
        super(GUICommon.WINDOW_NAME);
        this.common = common;

//        rootPanel = new JPanel();
//        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        Object[] writeRegisterNames = common.registers().stream().filter(Register::isWritable).map(Register::getName).toArray();

        JToolBar toolbar = new JToolBar();
        toolbar.add(new JLabel(" All Registers"));
        toolbar.addSeparator();
        toolbar.add(new JComboBox<>(writeRegisterNames));
        toolbar.add(new JButton("Write Selected"));
        toolbar.add(new JButton("Write All"));
        toolbar.add(new JButton("Read All"));
        toolbar.setFloatable(false);
        toolbar.setBorderPainted(true);
//        toolbar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(toolbar, BorderLayout.PAGE_START);

        tableModel = new BulkRegisterTableModel(common.registers());
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        setSize(500, 700);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(50);
        columnModel.getColumn(2).setPreferredWidth(50);
        columnModel.getColumn(3).setPreferredWidth(300);


        setJMenuBar(common.createMenuBar());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public JFrame getFrame() {
        return this;
    }

    @Override
    public void addPopOutWindow(RegisterPopOutWindow window) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fireWriteRegistersDataChange() {

    }

    @Override
    public void addHintFor(Component component, Supplier<String> hint) {
        throw new UnsupportedOperationException();
    }
}
