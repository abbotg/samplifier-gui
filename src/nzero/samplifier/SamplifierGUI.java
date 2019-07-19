package nzero.samplifier;

import nzero.samplifier.gui.GUICommon;
import nzero.samplifier.gui.GUIMode;
import nzero.samplifier.gui.basic.BasicMainWindow;
import nzero.samplifier.model.Register;
import nzero.samplifier.profile.Profile;
import nzero.samplifier.profile.ProfileManager;
import nzero.samplifier.util.RegMapBootstrapper;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class SamplifierGUI {
    private List<Register> registers;
    private ProfileManager profileManager;
    private GUICommon common;

    /**
     * Holds the main thread
     */
    private static JWindow splash() {
        JWindow window = new JWindow();
        window.getContentPane().add(
                new JLabel("", new ImageIcon("res/splash.png"), SwingConstants.CENTER));
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
        /* Turn off metal's use of bold fonts */
        //UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.

        JWindow window = splash();
        pause(500);

        SamplifierGUI samplifierGUI = new SamplifierGUI();
        samplifierGUI.initWithArgs(args);

        SwingUtilities.invokeLater(samplifierGUI::createAndShowGUI);

        pause(1000);
        window.setVisible(false);
        window.dispose();
    }

    private void initWithArgs(String[] args) {

        // TODO: parse args

        this.registers = RegMapBootstrapper.buildFromFile("res/RegisterMapping.json");
        this.profileManager = new ProfileManager();
        this.common = new GUICommon(this.registers, this.profileManager);
    }

    private void createAndShowGUI() {
        /* This is on the Event Dispatch Thread */
        common.setWindow(GUIMode.BASIC);
    }


    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
