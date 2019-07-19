package nzero.samplifier.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RegisterTest {

    private List<Register> registers;
    private Register register, register1;

    @Before
    public void setUp() throws Exception {
        List<BitMap> bitMaps = new ArrayList<>();
//        bitMaps.add(new BitMap("Gain Reset", 13, 10));
//        bitMaps.add(new BitMap("Offset Reset", 9, 4));
//        bitMaps.add(new BitMap("Gain Mux",3,3));
//        bitMaps.add(new BitMap("Gain Enable", 2,2));
//        bitMaps.add(new BitMap("Offset Enable", 1, 1));
//        bitMaps.add(new BitMap("AGOC", 0, 0));

        List<BitMap> bitMaps1 = new ArrayList<>();
//        bitMaps1.add(new BitMap("Stuff", 15,10));
//        bitMaps1.add(new BitMap("Things", 9,8));
//        bitMaps1.add(new BitMap("Thing", 7,7));
//        bitMaps1.add(new BitMap("Items",6,0));

        register = new Register("RCV_AGOC5", 0, true, bitMaps);
        register1 = new Register("TestReg", 0, false, bitMaps1);

        registers = new ArrayList<>();
        registers.add(register);
        registers.add(register1);
    }

    @Test
    public void setData() throws Exception {
        BitMap bitMap = register.getBitMaps().get(0);
        register.setData(bitMap, 0xF);
        assertEquals(0x3C00, register.getData());
    }

    @Test
    public void setData2() {
        BitMap bitMap = register.getBitMaps().get(1);
        register.setData(bitMap, 0x3F);
        assertEquals(0x3F0, register.getData());
    }

    @Test
    public void getData() {
        register.setData(0x3C00);
        assertEquals(0xF, register.getData(0));
    }
}