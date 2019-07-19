package nzero.samplifier.gui.basic;

import nzero.samplifier.gui.GUICommon;
import nzero.samplifier.gui.SamplifierMainWindow;
import nzero.samplifier.model.Register;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class BasicMainWindow extends JFrame implements SamplifierMainWindow {
    private JPanel rootPanel; // root below the JFrame
    private final JPanel writeRegTabbedPane;
    //    private final JTabbedPane writeRegTabbedPane;
    private final JTabbedPane readRegTabbedPane;
    private JToolBar writeRegToolbar, readRegToolbar;
    private JComboBox<String> comboBox;

    private JButton writeButton, writeAllButton, readButton, readAllButton, continuousReadButton;

    private List<RegisterPanel> writeRegisterPanels, readRegisterPanels; // For updating the table data on profile load
    private GUICommon common;


    public BasicMainWindow(GUICommon common) {
        super(GUICommon.WINDOW_NAME);
        this.common = common;

        //////////////////////
        /// Create buttons ///
        //////////////////////
        writeButton = new JButton("Write");
        writeAllButton = new JButton("Write All");
        readButton = new JButton("Read");
        readAllButton = new JButton("Read All");
        continuousReadButton = new JButton("Continuous Read");


        /////////////////////
        /// Create panels ///
        /////////////////////

//        writeRegTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        writeRegTabbedPane = new JPanel(new CardLayout());
        readRegTabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        writeRegTabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        readRegTabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Create the "cards" in the card layout, which are RegisterPanels
        // and add them to the writeRegTabbedPane. The cards (registers) are identified
        // by their name (a string)
        List<String> writeRegisterNames = new ArrayList<>();
        writeRegisterPanels = new ArrayList<>(); // TODO: can this and above be consolidated
        readRegisterPanels = new ArrayList<>();

        for (Register register : common.registers()) {
            RegisterPanel registerPanel = new RegisterPanel(register);
            if (register.isWritable()) {
                writeRegisterPanels.add(registerPanel);
                writeRegTabbedPane.add(registerPanel, register.getName()); // this is a deep call
                writeRegisterNames.add(register.getName()); // TODO: still need to handle dup names
            } else {
                readRegTabbedPane.add(registerPanel, register.getName());
                readRegisterPanels.add(registerPanel);
            }
        }

        rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

//        writeRegTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        comboBox = new JComboBox<>(writeRegisterNames.toArray(new String[0]));
        comboBox.setEditable(false);
        comboBox.addItemListener(e -> {
            CardLayout layout = (CardLayout) writeRegTabbedPane.getLayout();
            layout.show(writeRegTabbedPane, (String) e.getItem());
        });


        // Write register view
        writeRegToolbar = new JToolBar();
        writeRegToolbar.add(new JLabel("Writable Registers"), BorderLayout.LINE_START);
        writeRegToolbar.addSeparator();
        writeRegToolbar.add(comboBox);
        writeRegToolbar.add(writeButton);
        writeRegToolbar.add(writeAllButton);
        writeRegToolbar.setFloatable(false);

        JPanel writeContainer = new JPanel(new BorderLayout());
        writeContainer.add(writeRegToolbar, BorderLayout.PAGE_START);
        writeContainer.add(writeRegTabbedPane, BorderLayout.CENTER);
        rootPanel.add(writeContainer);

        rootPanel.add(new JSeparator(JSeparator.HORIZONTAL));

        // Read register view
        readRegToolbar = new JToolBar();
        readRegToolbar.add(new JLabel("Readable Registers"), BorderLayout.LINE_START);
        readRegToolbar.addSeparator();
        readRegToolbar.add(readButton);
        readRegToolbar.add(readAllButton);
        readRegToolbar.add(continuousReadButton);
        readRegToolbar.setFloatable(false);

        JPanel readContainer = new JPanel(new BorderLayout());
        readContainer.add(readRegToolbar, BorderLayout.PAGE_START);
        readContainer.add(readRegTabbedPane, BorderLayout.CENTER);
        rootPanel.add(readContainer);

        // Configure the JFrame
        setJMenuBar(common.createMenuBar());
        add(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        configureActionListeners();
        common.updateProfilesMenu();


        // Called last
        pack();
        setLocationRelativeTo(null);
        setVisible(true);



    }

    private void configureActionListeners() {
        writeButton.addActionListener(this::writeButton);
        writeAllButton.addActionListener(this::writeAllButton);
    }





    public Register getCurrentWriteRegister() {
        String name = String.valueOf(comboBox.getSelectedItem());
        return common.registers().stream().filter(register -> register.getName().equals(name)).findFirst().get();
    }

    public RegisterPanel getCurrentWriteRegisterPanel() {
        String name = String.valueOf(comboBox.getSelectedItem());
        return writeRegisterPanels.stream().filter(panel -> panel.getRegister().getName().equals(name)).findFirst().orElse(null);
    }

    private void writeButton(ActionEvent e) {
        Register current = getCurrentWriteRegister();
        common.write(current);
    }

    private void writeAllButton(ActionEvent e) {
        common.writeAll();
    }


    @Override
    public JFrame getFrame() {
        return this;
    }

    @Override
    public void fireWriteRegistersDataChange() {
        getCurrentWriteRegisterPanel().updateRegisterData();
    }
}
