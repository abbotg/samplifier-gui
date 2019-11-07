package nzero.samplifier.gui;

import com.fazecast.jSerialComm.SerialPort;
import nzero.samplifier.api.SamplifierAPI;
import nzero.samplifier.api.SamplifierConnection;
import nzero.samplifier.api.SamplifierResponseListener;
import nzero.samplifier.gui.advanced.AdvancedMainWindow;
import nzero.samplifier.gui.basic.BasicMainWindow;
import nzero.samplifier.model.Register;
import nzero.samplifier.profile.ProfileManager;
import nzero.samplifier.profile.ProfileMismatchException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class GUICommon {
    private List<Register> registers;
    private ProfileManager profileManager;
    private JMenu loadProfilesMenu, connectionMenu;
    private JMenuItem connectionStatus, connectionButton;
    private JRadioButtonMenuItem basicModeButton, advancedModeButton;
    private GUIMode mode;
    private SamplifierMainWindow activeWindow;
    private SamplifierConnection connection;

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
        JMenu viewMenu, profileMenu, helpMenu, samplifierMenu;
        JRadioButtonMenuItem radioButtonMenuItem;
        JMenuItem menuItem;

        // Create menu bar
        menuBar = new JMenuBar();

        /// Connection menu ///
        connectionMenu = new JMenu("Connection");

        connectionStatus = new JMenuItem("Status: Disconnected");
        connectionStatus.setEnabled(false);
        connectionMenu.add(connectionStatus);

        connectionButton = new JMenuItem("Connect");
        connectionButton.addActionListener(this::connectionToggleButton);
        connectionMenu.add(connectionButton);

        updateConnectionMenu();

        /// View Menu ///
        viewMenu = new JMenu("View");
//        menuBar.add(viewMenu);

        ButtonGroup buttonGroup = new ButtonGroup();

        basicModeButton = new JRadioButtonMenuItem("Basic");
        basicModeButton.addActionListener(e -> GUICommon.this.setWindow(GUIMode.BASIC));
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

        //  samplifier menu
        samplifierMenu = new JMenu("Samplifier");
        menuItem = new JMenuItem("Change Register Map...");
        menuItem.addActionListener(this::changeRegisterMap);
        samplifierMenu.add(menuItem);


        // placeholder
        helpMenu = new JMenu("Help");

        menuBar.add(samplifierMenu);
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

    private void changeRegisterMap(ActionEvent e) {
        Preferences preferences = Preferences.userRoot().node("nzero/samplifier/regmap");
        preferences.remove("last");
        JOptionPane.showMessageDialog(getActiveFrame(), "The register map selection popup will now appear the next time the program is restarted.");
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
            profileManager = new ProfileManager(); // TODO: will never happen, profile manager created in SamplifierGUI.java
        }
        loadProfilesMenu.removeAll();
        List<String> profiles = profileManager.getProfiles();
        for (String profile : profiles) {
            JMenuItem profileMenuItem = new JMenuItem(profile);
            loadProfilesMenu.add(profileMenuItem);
            profileMenuItem.addActionListener(e -> loadProfile(profile));
        }
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public void updateConnectionMenu() {
        if (isConnected()) {
            connectionStatus.setText("Status: Connected");
            connectionButton.setText("Disconnect");
        } else {
            connectionStatus.setText("Status: Disconnected");
            connectionButton.setText("Connect");
        }
    }

    public void connectionToggleButton(ActionEvent e) {
        if (connection == null) {
            String input;
            boolean valid;
            SerialPort[] ports = SerialPort.getCommPorts();
            StringBuilder sb = new StringBuilder();
            sb.append("Choose COM port or device file from the following:\n\n");
            for (SerialPort port : ports) {
                sb.append(port.getSystemPortName());
                sb.append('\n');
            }
            sb.append('\n');
            do {
                input = JOptionPane.showInputDialog(getActiveFrame(),
                        sb.toString(),
                        "/dev/ttyACM0"); // TODO: scan for existing
                if (input == null) {
                    return;
                } else if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(getActiveFrame(), "Input cannot be empty");
                    valid = false;
                } else {
                    if (SamplifierAPI.isValidPort(input)) {
                        connection = SamplifierAPI.createConnection(input);
                        if (connection == null) {
                            valid = false;
                            JOptionPane.showMessageDialog(getActiveFrame(),
                                    "Connection failed",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            valid = true;
                        }

                    } else {
                        valid = false;
                        JOptionPane.showMessageDialog(getActiveFrame(),
                                "Invalid port",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } while (!valid);
        } else { // Connection already exists
            connection.disconnect();
            connection = null;
        }
        updateConnectionMenu();
    }

    class Listener implements SamplifierResponseListener {

        @Override
        public void didReadRegister(int address, int data) {
            Register register = null;
            for (Register r : registers) {
                if (r.getAddress() == address) {
                    register = r;
                    break;
                }
            }
            if (register == null) {
                System.err.println("Arduino callback: got invalid register address");
                // TODO: alert gui
                return;
            }
            System.out.printf("Read of register %s addr %d (0b%s) got data %d (0b%s)%n",
                    register.getName(),
                    register.getAddress(),
                    Integer.toBinaryString(register.getAddress()),
                    data,
                    Integer.toBinaryString(data)
            );
            register.setData(data);
            activeWindow.fireReadRegistersDataChange();
        }

        @Override
        public void didWriteRegister(int address, int data) {

        }

        @Override
        public void digitalIOUpdate(String pin, boolean value) {

        }
    }

    private void write(Register register) {
        connection.writeRegister((char) register.getAddress(), register.getData()); //TODO: char vs int?
        System.out.printf("Wrote register %s at address %d (0b%s) with value %d (0b%s)%n",
                register.getName(),
                register.getAddress(),
                Integer.toBinaryString(register.getAddress()),
                register.getData(),
                register.getBinaryString()
        );
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) { }
    }

    public void confirmAndWrite(Register register) {
        confirmAndWrite(Collections.singletonList(register));

    }

    public void confirmAndWrite(ActionEvent e) {
        String name = e.getActionCommand();
        confirmAndWrite(getRegister(name));
    }

    // TODO: this method should be somewhere else
    public void confirmAndWrite(Collection<Register> registers) {
        StringBuilder builder = new StringBuilder();
        List<Register> writable = registers.stream().filter(Register::isWritable).collect(Collectors.toList());
        builder.append("Write the following?\n\n");
        for (Register register : writable) {
            builder.append(register.getName())
                    .append(": ")
                    .append(register.getBinaryString())
                    .append(" (Addr: ")
                    .append(register.getAddress()).append(')').append('\n');
        }
        int option = JOptionPane.showConfirmDialog(getActiveFrame(), builder.toString(), "Write", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            for (Register register : writable) {
                write(register);
            }
        } else {
            System.out.println("not written");
        }
    }

    public void confirmAndWriteAll() {
        confirmAndWrite(registers);
    }

    public void read(Register register) { // TODO: what if register not read?
        if (isConnected()) {
            connection.setSamplifierResponseListener(new Listener());
            System.out.printf("Requesting read of register %s addr %d%n", register.getName(), register.getAddress());
            connection.readRegister((char) register.getAddress()); // TODO: address char or int?
        } else {
            System.err.println("Not connected"); // TODO: gui alert
        }
    }

    public void readAll(Collection<Register> registers) {
        if (isConnected()) {
            connection.setSamplifierResponseListener(new Listener());
            for (Register register : registers) {
                connection.readRegister((char) register.getAddress()); // TODO: address char or int?
            }
        } else {
            System.err.println("Not connected"); // TODO: gui alert
        }
    }

    public List<Register> registers() {
        return Collections.unmodifiableList(registers);
    }

    private JFrame getActiveFrame() {
        return activeWindow.getFrame();
    }

    public Register getRegister(String name) {
        for (Register register : registers) {
            if (register.getName().equals(name)) {
                return register;
            }
        }
        return null;
    }


}
