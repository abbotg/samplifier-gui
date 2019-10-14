package nzero.samplifier.gui.basic;

import nzero.samplifier.gui.GUIInputHandler;
import nzero.samplifier.gui.GUIUtils;
import nzero.samplifier.gui.SamplifierMainWindow;
import nzero.samplifier.model.Register;
import nzero.samplifier.model.RegisterType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static nzero.samplifier.gui.GUIUtils.createButton;

/**
 * Holds the RegisterPanel containing the table, as well as the toolbar (with write/writeall/read etc buttons) and
 * manages the card/tabbed pane layout
 * <p>
 * There are 3 of these per application instance. They are assigned to either Read registers, Write registers, or
 * Read/Write registers.
 * <p>
 * No RegisterPanel or Register is a member of multiple RegisterPanelContainers.
 */
public class RegisterPanelContainer extends JPanel implements ItemListener {

    private static final int MAX_NUM_TABS = 6;

    private Container panelContainer; // Either a JTabbedPane or JPanel with card layout
    private List<RegisterPanel> registerPanels;
    private List<Register> registers;
    private Register currentRegister;
    private JToolBar toolBar;
    private RegisterType registerType;
    private final boolean usesTabs;
    private GUIInputHandler inputHandler;
    private JComboBox<Object> comboBox;



    public RegisterPanelContainer(List<Register> registers,
                                  RegisterType registerType,
                                  GUIInputHandler inputHandler) {
        this.registerType = registerType;
        this.registers = registers;
        this.inputHandler = inputHandler;

        assert !registers.isEmpty();

        this.registerPanels = registers.stream().map(register -> new RegisterPanel(register, inputHandler.mainWindow())).collect(Collectors.toList());
        this.usesTabs = registers.size() <= MAX_NUM_TABS;

        toolBar = new JToolBar(); // TODO: need some common interface for write/read button action listeners
//        toolBar.setLayout(new BorderLayout());
        toolBar.setFloatable(false);
        JPanel group = new JPanel(new FlowLayout());
        group.setOpaque(false);
        if (registerType == RegisterType.READ) {
//            toolBar.add(new JLabel(" Read-only"), BorderLayout.LINE_START);
//            group.add(createButton("Read", inputHandler::read));
//            group.add(createButton("Read All", inputHandler::readAll));
//            toolBar.add(group, BorderLayout.LINE_END);
            toolBar.add(new JLabel(" Read-only"));
            toolBar.addSeparator();
            toolBar.add(createReadButton());
            toolBar.add(createReadAllButton());
        } else if (registerType == RegisterType.WRITE) {
//            toolBar.add(new JLabel(" Write-only"), BorderLayout.LINE_START);
//            group.add(createButton("Write", inputHandler::write));
//            group.add(createButton("Write All", inputHandler::writeAll));
//            toolBar.add(group, BorderLayout.LINE_END);
            toolBar.add(new JLabel(" Write-only"));
            toolBar.addSeparator();
            toolBar.add(createWriteButton());
            toolBar.add(createWriteAllButton());
        } else { // Read/Write
//            toolBar.add(new JLabel(" Read/Write"), BorderLayout.LINE_START);
//            group.add(createButton("Read", inputHandler::read));
//            group.add(createButton("Read All", inputHandler::readAll));
//            group.add(createButton("Write", inputHandler::write));
//            group.add(createButton("Write All", inputHandler::writeAll)); // TODO: write all write all registers that are writable
//            toolBar.add(group, BorderLayout.LINE_END);
            toolBar.add(new JLabel(" Read/Write"));
            toolBar.addSeparator();
            toolBar.add(createReadButton());
            toolBar.add(createReadAllButton());
            toolBar.add(createWriteButton());
            toolBar.add(createWriteAllButton());
        }

        toolBar.add(createButton("Pop out", this::popOutButton));

        if (usesTabs) {
            JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
            tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            for (RegisterPanel registerPanel : registerPanels) {
                tabbedPane.add(registerPanel, registerPanel.getRegister().getName());
            }
            panelContainer = tabbedPane;
        } else {
            JPanel panel = new JPanel(new CardLayout());
//            panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            JComboBox<Object> comboBox = new JComboBox<>(registers.toArray());
            comboBox.setEditable(false);
            comboBox.addItemListener(this);
            toolBar.add(comboBox);

            for (RegisterPanel registerPanel : registerPanels) {
                panel.add(registerPanel, registerPanel.getRegister().getName());
            }

            panelContainer = panel;
        }

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.PAGE_START);
        add(panelContainer, BorderLayout.CENTER);

        // Set selected register
        currentRegister = registers.get(0);

    }

    Register currentRegister() {
        if (panelContainer instanceof JPanel) {
            return currentRegister;
        } else if (panelContainer instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) panelContainer;
            return registers.get(tabbedPane.getSelectedIndex());
        } else {
            return null;
        }
    }

    RegisterPanel currentRegisterPanel() {
        for (RegisterPanel panel : registerPanels) {
            if (panel.getRegister().equals(this.currentRegister)) {
                return panel;
            }
        }
        throw new NoSuchElementException("Couldnt find current register panel" );
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        CardLayout layout = (CardLayout) panelContainer.getLayout();
        this.currentRegister = (Register) e.getItem();
        layout.show(panelContainer, currentRegister.getName());

    }

    // TODO: move this
    private void popOutButton(ActionEvent e) {
        RegisterPanel panel = new RegisterPanel(currentRegister(), inputHandler.mainWindow());
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new BorderLayout());
        toolBar.setFloatable(false);

        if (panel.getRegister().getRegisterType() == RegisterType.READ) {
            toolBar.add(new JLabel(" Read-only"), BorderLayout.LINE_START);
            toolBar.addSeparator();
            JButton _readButton = new JButton("Read");
            _readButton.addActionListener(inputHandler::read);
            _readButton.setActionCommand(panel.getRegister().getName()); // TODO: Registers are identified by name, must be unique
            toolBar.add(_readButton, BorderLayout.LINE_END);
        } else if (panel.getRegister().getRegisterType() == RegisterType.WRITE) {
            toolBar.add(new JLabel(" Write-only"), BorderLayout.LINE_START);
            toolBar.addSeparator();
            JButton _writeButton = new JButton("Write");
            _writeButton.addActionListener(inputHandler::write);
            _writeButton.setActionCommand(panel.getRegister().getName()); // TODO: Registers are identified by name, must be unique
            toolBar.add(_writeButton, BorderLayout.LINE_END);
        } else { // READ/WRITE
            toolBar.add(new JLabel(" Read/Write"), BorderLayout.LINE_START);
            toolBar.addSeparator();
            JButton _readButton = new JButton("Read");
            _readButton.addActionListener(inputHandler::read);
            _readButton.setActionCommand(panel.getRegister().getName()); // TODO: Registers are identified by name, must be unique
            toolBar.add(_readButton, BorderLayout.LINE_END);
            JButton _writeButton = new JButton("Write");
            _writeButton.addActionListener(inputHandler::write);
            _writeButton.setActionCommand(panel.getRegister().getName()); // TODO: Registers are identified by name, must be unique
            toolBar.add(_writeButton, BorderLayout.LINE_END);
        }

        JDialog dialog = new JDialog(inputHandler.mainWindow().getFrame(), panel.getRegister().getName(), false);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(toolBar, BorderLayout.PAGE_END);

//        popOutWindows.add(dialog);
        dialog.pack();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(inputHandler.mainWindow().getFrame());
        dialog.setVisible(true);

        inputHandler.mainWindow().addPopOutWindow(new RegisterPopOutWindow() {
            @Override
            public void fireDataChange() {
                panel.updateRegisterData();
            }

            @Override
            public boolean isVisible() {
                return dialog.isVisible();
            }

            @Override
            public Register getRegister() {
                return panel.getRegister();
            }
        });
    }

    private JButton createReadButton() {
        JButton button = createButton("Read", inputHandler::read);
        inputHandler.mainWindow().addHintFor(button, () -> "Read from register " + currentRegister().getName());
        return button;
    }

    private JButton createReadAllButton() {
        JButton button = createButton("Read All", inputHandler::readAll);
        inputHandler.mainWindow().addHintFor(button, () ->"Read all registers");
        return button;
    }

    private JButton createWriteButton() {
        JButton button = createButton("Write", inputHandler::write);
        inputHandler.mainWindow().addHintFor(button, () -> "Write to register " + currentRegister().getName());
        return button;
    }

    private JButton createWriteAllButton() {
        JButton button = createButton("Write All", inputHandler::writeAll);
        inputHandler.mainWindow().addHintFor(button, () -> "Write to all registers");
        return button;
    }
}
