package nzero.samplifier.profile;

import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.DataType;
import nzero.samplifier.model.Register;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProfileManagerTest {

    private List<Register> registers;
    private Register register0, register1;

    @Before
    public void setUp() throws Exception {
        List<BitMap> bitMaps0 = new ArrayList<>();
//        bitMaps0.add(new BitMap("Gain Reset", 13, 10));
//        bitMaps0.add(new BitMap("Offset Reset", 9, 4));
//        bitMaps0.add(new BitMap("Gain Mux",3,3));
//        bitMaps0.add(new BitMap("Gain Enable", 2,2));
//        bitMaps0.add(new BitMap("Offset Enable", 1, 1));
//        bitMaps0.add(new BitMap("AGOC", 0, 0));

        List<BitMap> bitMaps1 = new ArrayList<>();
//        bitMaps1.add(new BitMap("Gain Reset", 13, 10, DataType.BIN, Integer.MAX_VALUE, Integer.MIN_VALUE, 9));
//        bitMaps1.add(new BitMap("Offset Reset", 9, 4, DataType.BIN, Integer.MAX_VALUE, Integer.MIN_VALUE, 16));
//        bitMaps1.add(new BitMap("Gain Mux",3,3, DataType.BIN, Integer.MAX_VALUE, Integer.MIN_VALUE, 1));
//        bitMaps1.add(new BitMap("Gain Enable", 2,2, DataType.BIN, Integer.MAX_VALUE, Integer.MIN_VALUE, 0));
//        bitMaps1.add(new BitMap("Offset Enable", 1, 1, DataType.BIN, Integer.MAX_VALUE, Integer.MIN_VALUE, 1));
//        bitMaps1.add(new BitMap("AGOC", 0, 0, DataType.BIN, Integer.MAX_VALUE, Integer.MIN_VALUE, 0));

        register0 = new Register("RCV_AGOC5", 0, true, bitMaps0);
        register1 = new Register("RCV_AGOC5", 0, false, bitMaps1);


        registers = new ArrayList<>();
        registers.add(register0);
        registers.add(register1);
    }

    @Test
    public void loadProfile() {

    }
}