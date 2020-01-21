package nzero.samplifier;

import nzero.samplifier.gui.GUICommon;
import nzero.samplifier.gui.GUIMode;
import nzero.samplifier.model.Register;
import nzero.samplifier.profile.ProfileManager;
import nzero.samplifier.util.RegMapBootstrapper;
import nzero.samplifier.util.UncaughtExceptionHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.prefs.Preferences;

import static nzero.samplifier.util.RegMapBootstrapper.*;

/**
 * Main class
 */
public class SamplifierGUI {

    public static final String SAMPLIFIER_GUI_VERSION = "v1.8";

    private List<Register> registers;
    private ProfileManager profileManager;
    private GUICommon common;
    private static String mapName;

    private SamplifierGUI() {
        // empty
    }

    /**
     * Holds the main thread
     */
    private static JWindow splash() {
        // Load image
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("img/splash.png");
        assert input != null;
        Image image = null;
        try {
            image = ImageIO.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert image != null;

        // Create window
        JWindow window = new JWindow();
        window.getContentPane().add(
                new JLabel("", new ImageIcon(image), SwingConstants.CENTER));
        window.setBounds(500, 150, 300, 200);
        window.setAlwaysOnTop(true);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                window.setVisible(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        return window;
    }

    public static void main(String[] args) {
        initUncaughtExceptionHandler();

        JWindow window = splash();
        new Thread(() -> {
            pause(1000);
            window.setVisible(false);
            window.dispose();
        }).start();
        pause(500);


        SamplifierGUI samplifierGUI = new SamplifierGUI();
        samplifierGUI.initWithArgs(args);

        SwingUtilities.invokeLater(samplifierGUI::createAndShowGUI);

//        pause(1000);

    }

    private void initWithArgs(String[] args) {

        // TODO: parse args

        this.registers = initRegisterMap();
        this.profileManager = new ProfileManager();
        this.common = new GUICommon(this.registers, this.profileManager);
    }


    private List<Register> initRegisterMap() {
        if (isDefaultRegMapSet()) {
            try {
                return isDefaultBuiltIn() ? buildFromResource(String.format("map/%s.json", getDefaultRegMap())) : buildFromFile(getDefaultRegMap());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, String.format("Error reading file %s: %s", getDefaultRegMap(), e.getMessage()));
                clearDefaultRegMapPreferences();
                System.exit(1);
                return null;
            }
        } else {
            Preferences preferences = Preferences.userRoot().node("nzero/samplifier/regmap");

            if (promptUseExternalRegMap()) { // show filesystem popup
                String mapPath = promptRegMapExternal();
                preferences.put("last", mapPath);
                preferences.putBoolean("lastBuiltIn", false);
                try {
                    return buildFromFile(mapPath);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, String.format("Error reading file %s: %s", getDefaultRegMap(), e.getMessage()));
                    clearDefaultRegMapPreferences();
                    System.exit(1);
                    return null;
                }
            } else { // use built in map
                String mapName = promptRegMapBuiltIn();
                preferences.put("last", mapName);
                preferences.putBoolean("lastBuiltIn", true);
                return buildFromResource(String.format("map/%s.json", mapName));
            }
        }
    }

    private String promptRegMapBuiltIn() {
        String mapName;
        mapName = (String) JOptionPane.showInputDialog(null,
                "Select register mapping",
                "Startup", JOptionPane.PLAIN_MESSAGE,
                null,
                RegMapBootstrapper.getAvailableRegMaps(),
                null);
        if (mapName == null) { // "Cancel" was pressed
            System.out.println("Exiting");
            System.exit(0);
        }
        return mapName;
    }

    private String promptRegMapExternal() {
        String mapPath;
        final JFileChooser fileChooser = new JFileChooser();
        int retVal = fileChooser.showOpenDialog(null);
        File file = fileChooser.getSelectedFile();
        if (retVal != JFileChooser.APPROVE_OPTION || file == null) {
            System.out.println("Exiting");
            System.exit(1);
            return null;
        }
        mapPath = file.getAbsolutePath();
        return mapPath;
    }

    public static String getMapName() {
        if (mapName == null) {
            throw new RuntimeException("getMapName called before promptRegisterMapOrDefault");
        }
        return mapName;
    }

    private boolean promptUseExternalRegMap() {
        int ret = JOptionPane.showConfirmDialog(null,
                "Use an external register map?",
                "Register Map",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        return ret == JOptionPane.YES_OPTION;
    }

//    /**
//     * Returns resource paths
//     */
//    private String promptRegisterMap() {
//        Preferences preferences = Preferences.userRoot().node("nzero/samplifier/regmap");
//        String mapName;
//        if (preferences.get("last", null) != null) { // there is a last reg map recorded
//            mapName = preferences.get("last", null);
//        } else { // no last reg map recorded, ask
//            final String LOC_BUILTIN = "Select a built in register map";
//            final String LOC_EXTERNAL = "Select a register map from the filesystem";
//            String[] locOpts = {LOC_BUILTIN, LOC_EXTERNAL};
//            String mapLocation = (String) JOptionPane.showInputDialog(null,
//                    "Select register mapping",
//                    "Startup", JOptionPane.PLAIN_MESSAGE,
//                    null,
//                    locOpts,
//                    null);
//            if (mapLocation == null) { // cancelled
//                System.out.println("Exiting");
//                System.exit(0);
//            } else if (mapLocation.equals(LOC_BUILTIN)) {
//                mapName = (String) JOptionPane.showInputDialog(null,
//                        "Select register mapping",
//                        "Startup", JOptionPane.PLAIN_MESSAGE,
//                        null,
//                        RegMapBootstrapper.getAvailableRegMaps(),
//                        null);
//                if (mapName == null) { // "Cancel" was pressed
//                    System.out.println("Exiting");
//                    System.exit(0);
//                } else { // Valid selection, save it to prefs
//                    preferences.put("last", mapName);
//                }
//            } else if (mapLocation.equals(LOC_EXTERNAL)) {
//                final JFileChooser fileChooser = new JFileChooser();
//                int retVal = fileChooser.showOpenDialog(null);
//                File file = fileChooser.getSelectedFile();
//                if (retVal != JFileChooser.APPROVE_OPTION || file == null) {
//                    System.out.println("Exiting");
//                    System.exit(1);
//                    return null;
//                }
//
//            } else {
//                throw new RuntimeException("Bad option");
//                System.exit(1);
//                return null;
//            }
//        }
//        SamplifierGUI.mapName = mapName;
//        return "map/" + mapName + ".json";
//    }

    private void createAndShowGUI() {
        /* This is on the Event Dispatch Thread */

        // Force use metal
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        common.setWindow(GUIMode.BASIC);
    }


    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void initUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

}
