package nzero.samplifier.gui;

import nzero.samplifier.gui.basic.RegisterPanel;
import nzero.samplifier.model.Register;
import nzero.samplifier.profile.ProfileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {
    private List<Register> registers;
    private JFrame frame;
    private JPanel rootPanel; // root below the JFrame
    private final JPanel writeRegTabbedPane;
    //    private final JTabbedPane writeRegTabbedPane;
    private final JTabbedPane readRegTabbedPane;
    private JToolBar writeRegToolbar, readRegToolbar;
    private JComboBox<String> comboBox;

    private JButton writeButton, writeAllButton, readButton, readAllButton, continuousReadButton;

    private JMenu loadProfilesMenu;
    private ProfileManager profileManager;


    public MainWindow(List<Register> registers) {
        this.registers = registers;

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

        for (Register register : registers) {
            if (register.isWritable()) {
                writeRegTabbedPane.add(new RegisterPanel(register), register.getName()); // this is a deep call
                writeRegisterNames.add(register.getName()); // TODO: still need to handle dup names
            } else {
                readRegTabbedPane.add(new RegisterPanel(register), register.getName());
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
        frame = new JFrame("Samplifier GUI");
        frame.setJMenuBar(createMenuBar());
        frame.add(rootPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);


        configureActionListeners();
        updateProfilesMenu();


        // Called last
        frame.pack();
        frame.setVisible(true);

    }

    private void configureActionListeners() {
        writeButton.addActionListener(e -> {

        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu connectionMenu, viewMenu, profileMenu, helpMenu;
        JRadioButtonMenuItem radioButtonMenuItem;
        JMenuItem menuItem;

        // Create menu bar
        menuBar = new JMenuBar();

        /// View Menu ///
        viewMenu = new JMenu("View");
//        menuBar.add(viewMenu);

        ButtonGroup buttonGroup = new ButtonGroup();

        radioButtonMenuItem = new JRadioButtonMenuItem("Basic");
        radioButtonMenuItem.setSelected(true);
        buttonGroup.add(radioButtonMenuItem);
        viewMenu.add(radioButtonMenuItem);

        radioButtonMenuItem = new JRadioButtonMenuItem("Advanced");
        radioButtonMenuItem.setSelected(false);
        buttonGroup.add(radioButtonMenuItem);
        viewMenu.add(radioButtonMenuItem);

        /// Profile Menu ///
        profileMenu = new JMenu("Profile");

        menuItem = new JMenuItem("Save as...");
        menuItem.addActionListener(this::profileSaveAsButton);
        profileMenu.add(menuItem);

        loadProfilesMenu = new JMenu("Load");
        profileMenu.add(loadProfilesMenu);

        menuItem = new JMenuItem("Load default");
        menuItem.addActionListener(this::profileLoadDefaultButton);
        profileMenu.add(menuItem);

        menuItem = new JMenuItem("Set default...");
        menuItem.addActionListener(this::profileSetDefaultButton);
        profileMenu.add(menuItem);


        // placeholder
        connectionMenu = new JMenu("Connection");

        helpMenu = new JMenu("Help");

        menuBar.add(connectionMenu);
        menuBar.add(viewMenu);
        menuBar.add(profileMenu);
        menuBar.add(helpMenu);


        return menuBar;
    }

    private void profileSaveAsButton(ActionEvent e) {
        profileManager.createProfile("" + Math.random(), registers); // todo
        updateProfilesMenu();

    }

    private void profileLoadDefaultButton(ActionEvent e) {
        profileManager.removeProfile();
    }

    private void profileSetDefaultButton(ActionEvent e) {

    }

    public void updateProfilesMenu() {
        if (profileManager == null) {
            profileManager = new ProfileManager();
        }
        List<String> profiles = profileManager.getProfiles();
        profiles.stream().map(JMenu::new).map(menu -> loadProfilesMenu.add(menu));
    }


}
