package nzero.samplifier.gui;

import nzero.samplifier.gui.advanced.AdvancedMainWindow;
import nzero.samplifier.gui.basic.BasicMainWindow;
import nzero.samplifier.model.Register;
import nzero.samplifier.profile.ProfileManager;
import nzero.samplifier.profile.ProfileMismatchException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GUICommon {
    private List<Register> registers;
    private ProfileManager profileManager;
    private JMenu loadProfilesMenu;
    private JRadioButtonMenuItem basicModeButton, advancedModeButton;
    private GUIMode mode;
    private SamplifierMainWindow activeWindow;

    public static final String WINDOW_NAME = "Samplifier GUI";


    public GUICommon(List<Register> registers, ProfileManager profileManager) {
        this.registers = registers;
        this.profileManager = profileManager;
    }

    /**
     * Must be called from the EDT
     * @param mode
     */
    public void setWindow(GUIMode mode) {
        if (this.mode != null && this.mode == mode) {
            return; // Mode is already this mode
        }
        this.mode = mode;
        if (activeWindow != null) {
            JFrame frame = activeWindow.getFrame();
            frame.setVisible(false);
            frame.dispose();
        }
        switch (mode) {
            case BASIC:
                this.activeWindow = new BasicMainWindow(this);
                basicModeButton.setSelected(true);
                break;
            case ADVANCED:
                this.activeWindow = new AdvancedMainWindow(this);
                advancedModeButton.setSelected(true);
                break;
        }
    }

    public JMenuBar createMenuBar() {
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

        basicModeButton = new JRadioButtonMenuItem("Basic");
        basicModeButton.addActionListener(e -> setWindow(GUIMode.BASIC));
        basicModeButton.setSelected(true);
        buttonGroup.add(basicModeButton);
        viewMenu.add(basicModeButton);

        advancedModeButton = new JRadioButtonMenuItem("Advanced");
        advancedModeButton.setSelected(false);
        advancedModeButton.addActionListener(e -> setWindow(GUIMode.ADVANCED));
        buttonGroup.add(advancedModeButton);
        viewMenu.add(advancedModeButton);

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
        String input;
        boolean valid;
        do {
            input = JOptionPane.showInputDialog(
                    getActiveFrame(),
                    "Enter profile name",
                    "Save as",
                    JOptionPane.PLAIN_MESSAGE);

            if (input == null) {
                return;
            } else if (input.isEmpty()) {
                JOptionPane.showMessageDialog(getActiveFrame(),
                        "Profile name cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                valid = false;
            } else {
                if (profileManager.existsProfile(input)) {
                    int option = JOptionPane.showConfirmDialog(getActiveFrame(),
                            "Profile " + input + " already exists, replace it?",
                            "Warning",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    valid = (option == JOptionPane.OK_OPTION);
                } else {
                    valid = true;
                }
            }
        } while (!valid);

        profileManager.createProfile(input, registers);
        updateProfilesMenu();
    }

    private void profileLoadDefaultButton(ActionEvent e) {
        Optional<String> defaultProfile = profileManager.getDefaultProfile();
        if (defaultProfile.isPresent()) {
            loadProfile(defaultProfile.get());
        } else {
            JOptionPane.showMessageDialog(getActiveFrame(), "No default profile set.");
        }
    }

    private void profileSetDefaultButton(ActionEvent e) {
        Object[] possibilities = profileManager.getProfiles().toArray();
        String s = (String) JOptionPane.showInputDialog(
                getActiveFrame(),
                "Select the new default profile:",
                "Set Default Profile",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                null);

        profileManager.setDefaultProfile(s);
    }

    private void loadProfile(String profile) {
        try {
            profileManager.loadProfile(profile, registers);
        } catch (ProfileMismatchException e) {
            JOptionPane.showMessageDialog(getActiveFrame(),
                    "Profile not compatible with the current register mapping",
                    "Profile Mismatch",
                    JOptionPane.ERROR_MESSAGE);
        }
        updateProfilesMenu();
        activeWindow.fireWriteRegistersDataChange();
    }

    /**
     * Updates profiles menu and repaints the table with new data
     * TODO: this doesn't remove the old menu(s) it replaces
     */
    public void updateProfilesMenu() {
        if (profileManager == null) {
            profileManager = new ProfileManager();
        }
        loadProfilesMenu.removeAll();
        List<String> profiles = profileManager.getProfiles();
        for (String profile : profiles) {
            JMenuItem profileMenuItem = new JMenuItem(profile);
            loadProfilesMenu.add(profileMenuItem);
            profileMenuItem.addActionListener(e -> loadProfile(profile));
        }
    }

    public void write(Register register) {
        write(Collections.singletonList(register));
    }

    // TODO: this method should be somewhere else
    public void write(List<Register> registers) {
        StringBuilder builder = new StringBuilder();
        builder.append("Write the following?\n\n");
        for (Register register : registers.stream().filter(Register::isWritable).collect(Collectors.toList())) {
            builder.append(register.getName())
                    .append(": ")
                    .append(register.getBinaryString())
                    .append(" (Addr: ")
                    .append(register.getAddress()).append(')').append('\n');
        }
        int option = JOptionPane.showConfirmDialog(getActiveFrame(), builder.toString(), "Write", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            System.out.println("written");
        } else {
            System.out.println("not written");
        }
    }

    public void writeAll() {
        write(registers);
    }

    public List<Register> registers() {
        return Collections.unmodifiableList(registers);
    }

    private JFrame getActiveFrame() {
        return activeWindow.getFrame();
    }


}
