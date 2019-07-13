package nzero.samplifier;

import nzero.samplifier.gui.MainWindow;
import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.Register;
import nzero.samplifier.util.RegMapBootstrapper;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String... args) {
        /* Turn off metal's use of bold fonts */
        //UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(Main::createAndShowGUI);
    }


    public static void createAndShowGUI() {

        List<BitMap> bitMaps = new ArrayList<>();
        bitMaps.add(new BitMap("Gain Reset", 13, 10));
        bitMaps.add(new BitMap("Offset Reset", 9, 4));
        bitMaps.add(new BitMap("Gain Mux",3,3));
        bitMaps.add(new BitMap("Gain Enable", 2,2));
        bitMaps.add(new BitMap("Offset Enable", 1, 1));
        bitMaps.add(new BitMap("AGOC", 0, 0));

        List<BitMap> bitMaps1 = new ArrayList<>();
        bitMaps1.add(new BitMap("Stuff", 15,10));
        bitMaps1.add(new BitMap("Things", 9,8));
        bitMaps1.add(new BitMap("Thing", 7,7));
        bitMaps1.add(new BitMap("Items",6,0));

        Register register = new Register("RCV_AGOC5", 0, true, bitMaps);
        Register register1 = new Register("TestReg", 0, false,bitMaps1);
        List<Register> registers = new ArrayList<>();
        registers.add(register);
        registers.add(register1);

        Register register2 = new Register("WRT_AGOC5", 0, false, bitMaps);
        Register register3 = new Register("WRT_AGOC6", 0, true, bitMaps1);

        registers.add(register2);
        registers.add(register3);

//        new MainWindow(registers);

        new MainWindow(RegMapBootstrapper.buildFromFile("res/RegisterMapping.json"));
    }
}
