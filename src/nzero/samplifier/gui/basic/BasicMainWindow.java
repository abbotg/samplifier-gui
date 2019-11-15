package nzero.samplifier.gui.basic;

import nzero.samplifier.gui.GUIInputHandler;
import nzero.samplifier.gui.GUICommon;
import nzero.samplifier.gui.SamplifierMainWindow;
import nzero.samplifier.gui.basic.hint.MouseOverHintManager2;
import nzero.samplifier.model.Register;
import nzero.samplifier.model.RegisterType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BasicMainWindow extends JFrame implements SamplifierMainWindow, GUIInputHandler {
    private JPanel rootPanel; // root below the JFrame
    private JPanel writeRegTabbedPane;
    //    private final JTabbedPane writeRegTabbedPane;
    private JTabbedPane readRegTabbedPane;
    private JToolBar writeRegToolbar, readRegToolbar;
    private JComboBox<String> comboBox;

    private JButton writeButton, writeAllButton, readButton, readAllButton, continuousReadButton, writePopOutButton;
    private List<RegisterPopOutWindow> popOutWindows;

    private List<RegisterPanel> writeRegisterPanels, readRegisterPanels; // For updating the table data on profile load
    private GUICommon common;

    private RegisterPanelContainer writeContainer, readContainer, readWriteContainer;
    private JLabel hintLabel;
    private MouseOverHintManager2 hintManager;


    public BasicMainWindow(GUICommon common) {
        super(GUICommon.WINDOW_NAME);
        this.common = common;
        this.popOutWindows = new ArrayList<>();
        this.hintLabel = new JLabel();
        this.hintManager = new MouseOverHintManager2(hintLabel);

        List<Register> read, write, readWrite;

        read = common.registers().stream()
                .filter(register -> register.getRegisterType() == RegisterType.READ)
                .collect(Collectors.toList());

        write = common.registers().stream()
                .filter(register -> register.getRegisterType() == RegisterType.WRITE)
                .collect(Collectors.toList());

        readWrite = common.registers().stream()
                .filter(register -> register.getRegisterType() == RegisterType.READ_WRITE)
                .collect(Collectors.toList());

        rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        if (!read.isEmpty()) {
            this.readContainer = new RegisterPanelContainer(read, RegisterType.READ, this);
            rootPanel.add(readContainer);
        }

        if (!write.isEmpty()) {
            this.writeContainer = new RegisterPanelContainer(write, RegisterType.WRITE, this);
            rootPanel.add(writeContainer);
        }

        if (!readWrite.isEmpty()) {
            this.readWriteContainer = new RegisterPanelContainer(readWrite, RegisterType.READ_WRITE, this);
            rootPanel.add(readWriteContainer);
        }


//        rootPanel.add(hintLabel);

        hintLabel.setBorder(BorderFactory.createLoweredBevelBorder());
//        hintLabel.setBorder(BorderFactory.createLoweredSoftBevelBorder());

        // Configure the JFrame
        setJMenuBar(common.createMenuBar());
        add(rootPanel);
        add(hintLabel, BorderLayout.PAGE_END);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        configureActionListeners();
        common.updateProfilesMenu();

        hintManager.enableHints(this);

        // Called last
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

//    public BasicMainWindow(GUICommon common) {
//        super(GUICommon.WINDOW_NAME);
//        this.common = common;
//        this.popOutWindows = new ArrayList<>();
//
//        //////////////////////
//        /// Create buttons ///
//        //////////////////////
//        writeButton = new JButton("Write");
//        writeAllButton = new JButton("Write All");
//        readButton = new JButton("Read");
//        readAllButton = new JButton("Read All");
//        continuousReadButton = new JButton("Continuous Read");
//        writePopOutButton = new JButton("Pop out");
//
//
//        /////////////////////
//        /// Create panels ///
//        /////////////////////
//
////        writeRegTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
//        writeRegTabbedPane = new JPanel(new CardLayout());
//        readRegTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
//
//        writeRegTabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//        readRegTabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//
//        // Create the "cards" in the card layout, which are RegisterPanels
//        // and add them to the writeRegTabbedPane. The cards (registers) are identified
//        // by their name (a string)
//        List<String> writeRegisterNames = new ArrayList<>();
//        writeRegisterPanels = new ArrayList<>(); // TODO: can this and above be consolidated
//        readRegisterPanels = new ArrayList<>();
//
//        for (Register register : common.registers()) {
//            RegisterPanel registerPanel = new RegisterPanel(register);
//            if (register.isWritable()) {
//                writeRegisterPanels.add(registerPanel);
//                writeRegTabbedPane.add(registerPanel, register.getName()); // this is a deep call
//                writeRegisterNames.add(register.getName()); // TODO: still need to handle dup names
//            } else {
//                readRegTabbedPane.add(registerPanel, register.getName());
//                readRegisterPanels.add(registerPanel);
//            }
//        }
//
//        rootPanel = new JPanel();
//        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));
//
////        writeRegTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
//
//        comboBox = new JComboBox<>(writeRegisterNames.toArray(new String[0]));
//        comboBox.setEditable(false);
//        comboBox.addItemListener(e -> {
//            CardLayout layout = (CardLayout) writeRegTabbedPane.getLayout();
//            layout.show(writeRegTabbedPane, (String) e.getItem());
//        });
//
//
//        // Write register view
//        writeRegToolbar = new JToolBar();
//        writeRegToolbar.add(new JLabel("Writable Registers"), BorderLayout.LINE_START);
//        writeRegToolbar.addSeparator();
//        writeRegToolbar.add(comboBox);
//        writeRegToolbar.add(writeButton);
//        writeRegToolbar.add(writeAllButton);
//        writeRegToolbar.add(writePopOutButton);
//        writeRegToolbar.setFloatable(false);
//
//        JPanel writeContainer = new JPanel(new BorderLayout());
//        writeContainer.add(writeRegToolbar, BorderLayout.PAGE_START);
//        writeContainer.add(writeRegTabbedPane, BorderLayout.CENTER);
//        rootPanel.add(writeContainer);
//
//        rootPanel.add(new JSeparator(JSeparator.HORIZONTAL));
//
//        // Read register view
//        readRegToolbar = new JToolBar();
//        readRegToolbar.add(new JLabel("Readable Registers"), BorderLayout.LINE_START);
//        readRegToolbar.addSeparator();
//        readRegToolbar.add(readButton);
//        readRegToolbar.add(readAllButton);
//        readRegToolbar.add(continuousReadButton);
//        readRegToolbar.setFloatable(false);
//
//        JPanel readContainer = new JPanel(new BorderLayout());
//        readContainer.add(readRegToolbar, BorderLayout.PAGE_START);
//        readContainer.add(readRegTabbedPane, BorderLayout.CENTER);
//        rootPanel.add(readContainer);
//
//        // Configure the JFrame
//        setJMenuBar(common.createMenuBar());
//        add(rootPanel);
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//
//
//        configureActionListeners();
//        common.updateProfilesMenu();
//
//
//        // Called last
//        pack();
//        setLocationRelativeTo(null);
//        setVisible(true);
//
//
//
//    }
//
//    private void configureActionListeners() {
//        writeButton.addActionListener(this::writeButton);
//        writeAllButton.addActionListener(this::writeAllButton);
//        writePopOutButton.addActionListener(this::popOutButton);
//    }





//    public Register getCurrentWriteRegister() {
//        String name = String.valueOf(comboBox.getSelectedItem());
//        return common.registers().stream().filter(register -> register.getName().equals(name)).findFirst().get();
//    }

    public Register getCurrentWriteRegister() {
        return writeContainer.currentRegister();
    }

//    public RegisterPanel getCurrentWriteRegisterPanel() {
//        String name = String.valueOf(comboBox.getSelectedItem());
//        return writeRegisterPanels.stream().filter(panel -> panel.getRegister().getName().equals(name)).findFirst().orElse(null);
//    }

    public RegisterPanel getCurrentWriteRegisterPanel() {
        return writeContainer.currentRegisterPanel();
    }

    public RegisterPanel getCurrentReadRegisterPanel() {
        return readContainer.currentRegisterPanel();
    }

    private void writeButton(ActionEvent e) {
        Register current = getCurrentWriteRegister();
        common.confirmAndWrite(current);
    }

    private void writeAllButton(ActionEvent e) {
        common.confirmAndWriteAll();
    }

//    @Deprecated
//    private void popOutButton(ActionEvent e) {
//        RegisterPanel panel = new RegisterPanel(getCurrentWriteRegister(), this);
//        JToolBar toolBar = new JToolBar();
//        toolBar.setLayout(new BorderLayout());
//        toolBar.setFloatable(false);
//
//        if (panel.getRegister().getRegisterType() == RegisterType.READ) {
//            toolBar.add(new JLabel(" Read-only"), BorderLayout.LINE_START);
//            toolBar.addSeparator();
//            JButton _readButton = new JButton("Read");
//            _readButton.addActionListener(this::writeButton);
//            _readButton.setActionCommand(panel.getRegister().getName()); // TODO: Registers are identified by name, must be unique
//            toolBar.add(_readButton, BorderLayout.LINE_END);
//        } else if (panel.getRegister().getRegisterType() == RegisterType.WRITE) {
//            toolBar.add(new JLabel(" Write-only"), BorderLayout.LINE_START);
//            toolBar.addSeparator();
//            JButton _writeButton = new JButton("Write");
//            _writeButton.addActionListener(this::writeButton);
//            _writeButton.setActionCommand(panel.getRegister().getName()); // TODO: Registers are identified by name, must be unique
//            toolBar.add(_writeButton, BorderLayout.LINE_END);
//        } else { // READ/WRITE
//            toolBar.add(new JLabel(" Read/Write"), BorderLayout.LINE_START);
//            toolBar.addSeparator();
//            JButton _readButton = new JButton("Read");
//            _readButton.addActionListener(this::writeButton);
//            _readButton.setActionCommand(panel.getRegister().getName()); // TODO: Registers are identified by name, must be unique
//            toolBar.add(_readButton, BorderLayout.LINE_END);
//            JButton _writeButton = new JButton("Write");
//            _writeButton.addActionListener(this::writeButton);
//            _writeButton.setActionCommand(panel.getRegister().getName()); // TODO: Registers are identified by name, must be unique
//            toolBar.add(_writeButton, BorderLayout.LINE_END);
//        }
//
//        JDialog dialog = new JDialog(getFrame(), panel.getRegister().getName(), false);
//        dialog.setLayout(new BorderLayout());
//        dialog.add(panel, BorderLayout.CENTER);
//        dialog.add(toolBar, BorderLayout.PAGE_END);
//
////        popOutWindows.add(dialog);
//        dialog.pack();
//        dialog.setVisible(true);
//    }


    @Override
    public JFrame getFrame() {
        return this;
    }

    @Override
    public void addPopOutWindow(RegisterPopOutWindow window) {
        popOutWindows.add(window);
    }

    @Override
    public void fireWriteRegistersDataChange() {
        getCurrentWriteRegisterPanel().updateRegisterData();
        cleanPopOutWindows();

        for (RegisterPopOutWindow window : popOutWindows) {
            if (window.getRegister().isWritable()) {
                window.fireDataChange();
            }
        }
    }

    @Override
    public void fireReadRegistersDataChange() {
        getCurrentReadRegisterPanel().updateRegisterData();
        cleanPopOutWindows();

        for (RegisterPopOutWindow window : popOutWindows) {
            if (window.getRegister().isReadable()) {
                window.fireDataChange();
            }
        }
    }

    @Override
    public void addHintFor(Component component, Supplier<String> runnable) {
        hintManager.addHintFor(component, runnable);
    }

    private void cleanPopOutWindows() {
        popOutWindows.removeIf(window -> !window.isVisible());
    }

    @Override
    public void write(ActionEvent e) {
        String command = e.getActionCommand();
        if (command != null && !command.isEmpty()) {
            // action command (from pop out window)
            Register register = common.getRegister(command);
            if (register == null) {
                System.err.println("ActionCommand invalid register");
                return;
            }
            common.confirmAndWrite(register);
        } else {
            // get active register (from main window)
            common.confirmAndWrite(writeContainer.currentRegister());
        }
    }

    @Override
    public void read(ActionEvent e) {
        String command = e.getActionCommand();
        if (command != null && !command.isEmpty()) {
            // action command (from pop out window)
            common.read(common.getRegister(command));
        } else {
            // get active register (from main window)
            common.read(readContainer.currentRegister());
        }
    }

    @Override
    public void writeAll(ActionEvent e) {
        common.confirmAndWriteAll();
    }

    @Override
    public void readAll(ActionEvent e) {
        common.readAll(common.registers().stream().filter(Register::isReadable).collect(Collectors.toList()));
    }

    @Override
    public SamplifierMainWindow mainWindow() {
        return this;
    }
}
