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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Main class
 */
public class SamplifierGUI {

    public static final String SAMPLIFIER_GUI_VERSION = "v1.6-pre";

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

        this.registers = RegMapBootstrapper.buildFromResource(promptRegisterMapOrDefault());
        this.profileManager = new ProfileManager();
        this.common = new GUICommon(this.registers, this.profileManager);
    }

    public static String getMapName() {
        if (mapName == null) {
            throw new RuntimeException("getMapName called before promptRegisterMapOrDefault");
        }
        return mapName;
    }

    /**
     * Returns resource paths
     */
    private String promptRegisterMapOrDefault() {
        Preferences preferences = Preferences.userRoot().node("nzero/samplifier/regmap");
        String mapName;
        if (preferences.get("last", null) != null) { // there is a last reg map recorded
            mapName = preferences.get("last", null);
        } else { // no last reg map recorded, ask
            mapName = (String) JOptionPane.showInputDialog(null,
                    "Select register mapping",
                    "Startup", JOptionPane.PLAIN_MESSAGE,
                    null,
                    RegMapBootstrapper.getAvailableRegMaps(),
                    null);
            if (mapName == null) { // "Cancel" was pressed
                System.out.println("Exiting");
                System.exit(0);
            } else { // Valid selection, save it to prefs
                preferences.put("last", mapName);
            }
        }
        SamplifierGUI.mapName = mapName;
        return "map/" + mapName + ".json";
    }

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
