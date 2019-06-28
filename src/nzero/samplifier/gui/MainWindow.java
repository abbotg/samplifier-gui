package nzero.samplifier.gui;

import nzero.samplifier.model.Register;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainWindow {
    private List<Register> registers;
    private JFrame frame;
    private JPanel rootPanel; // root below the JFrame
    private final JTabbedPane writeRegTabbedPane, readRegTabbedPane;
    private JPanel writeRegToolbar, readRegToolbar;
    private JComboBox<String> comboBox;

    public MainWindow(List<Register> registers) {
        this.registers = registers;

        /*
        Create read and write panel
         */
//        writeRegTabbedPane = new JPanel(new CardLayout());
        writeRegTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        readRegTabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        writeRegTabbedPane.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        readRegTabbedPane.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));


        // Create the "cards" in the card layout, which are RegisterPanels
        // and add them to the writeRegTabbedPane. The cards (registers) are identified
        // by their name (a string)
        for (Register register : registers) {
            if (register.isWritable()) {
                writeRegTabbedPane.add(new RegisterPanel(register), register.getName()); // this is a deep call
            } else {
                readRegTabbedPane.add(new RegisterPanel(register), register.getName());
            }
        }

        rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

        writeRegToolbar = new JPanel(new BorderLayout());
        writeRegToolbar.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        writeRegToolbar.add(new JLabel("Writable Registers"), BorderLayout.LINE_START);
        writeRegToolbar.add(new JButton("Write"), BorderLayout.LINE_END);

        rootPanel.add(writeRegToolbar);
        rootPanel.add(writeRegTabbedPane);

        rootPanel.add(new JSeparator(JSeparator.HORIZONTAL));

        readRegToolbar = new JPanel(new BorderLayout());
        readRegToolbar.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        readRegToolbar.add(new JLabel("Readable Registers"), BorderLayout.LINE_START);
        readRegToolbar.add(new JButton("Read"), BorderLayout.LINE_END);

        rootPanel.add(readRegToolbar);
        rootPanel.add(readRegTabbedPane);





        frame = new JFrame("Samplifier GUI");

        frame.add(rootPanel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

    }



}
