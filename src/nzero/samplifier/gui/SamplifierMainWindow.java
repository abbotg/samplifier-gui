package nzero.samplifier.gui;

import nzero.samplifier.model.Register;

import javax.swing.*;

/**
 * Implemented by children of GUICommon so GUICommon can communicate with the windows
 */
public interface SamplifierMainWindow {

    JFrame getFrame();

    void fireWriteRegistersDataChange();


}
