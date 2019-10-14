package nzero.samplifier.gui;

import nzero.samplifier.gui.SamplifierMainWindow;
import nzero.samplifier.model.Register;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface GUIInputHandler {
    void write(ActionEvent e);

    void read(ActionEvent e);

    void writeAll(ActionEvent e);

    void readAll(ActionEvent e);

    SamplifierMainWindow mainWindow();
}
