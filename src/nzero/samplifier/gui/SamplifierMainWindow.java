package nzero.samplifier.gui;

import nzero.samplifier.gui.basic.RegisterPopOutWindow;
import nzero.samplifier.model.Register;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

/**
 * Implemented by children of GUICommon so GUICommon can communicate with the windows
 */
public interface SamplifierMainWindow {

    JFrame getFrame();

    /* Only valid in basic */
    void addPopOutWindow(RegisterPopOutWindow window);

    void fireWriteRegistersDataChange();

    void fireReadRegistersDataChange();

    /* Only valid in basic */
    void addHintFor(Component component, Supplier<String> hint);


}
