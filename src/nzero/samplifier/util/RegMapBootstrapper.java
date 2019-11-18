package nzero.samplifier.util;

import nzero.samplifier.SamplifierConstants;
import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.DataType;
import nzero.samplifier.model.Register;
import nzero.samplifier.model.RegisterType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RegMapBootstrapper {

    private static final int DEFAULT_DATA = 0;
    private static final DataType DEFAULT_DATA_TYPE = DataType.BIN;

    private static final String
            /* Bit map keys */
            KEY_BIT_MAP_NAME = "name",
            KEY_MSB = "msb",
            KEY_LSB = "lsb",
            KEY_DATA_TYPE = "dataType",
            KEY_MAX_VAL = "dataMax",
            KEY_MIN_VAL = "dataMin",
            /* Register keys */
            KEY_REGISTER_NAME = "name",
            KEY_IS_WRITABLE = "isWritable",
            KEY_IS_READABLE = "isReadable",
            KEY_ADDRESS = "address",
            KEY_BIT_MAPS = "bitMaps",
            KEY_LENGTH = "length";



    private RegMapBootstrapper() {}

    public static List<Register> buildFromFile(String filename) {
        try {
            return buildFromJsonString(FileUtils.readFile(filename));
        } catch (Exception e) {
            e.printStackTrace(); // TODO
            System.exit(1);
            return null;
        }
    }

    public static List<Register> buildFromResource(String resourcePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(resourcePath);
        assert input != null;
        String text = new Scanner(input, "UTF-8").useDelimiter("\\A").next();
        return buildFromJsonString(text);
    }

    public static List<Register> buildFromJsonString(String jsonData) {
        List<Register> registers = new ArrayList<>();

        JSONArray registerArray = new JSONArray(jsonData);
        for (int i = 0; i < registerArray.length(); i++) {
            JSONObject registerObject = registerArray.getJSONObject(i);
            JSONArray bitMapsArray = registerObject.getJSONArray(KEY_BIT_MAPS);

            List<BitMap> bitMaps = new ArrayList<>();
            for (int j = 0; j < bitMapsArray.length(); j++) {
                JSONObject bitMap = bitMapsArray.getJSONObject(j);

                String name = null;
                int msb = 0, lsb = 0, dataMin, dataMax;
                DataType dataType;

                if (bitMap.has(KEY_BIT_MAP_NAME)) {
                    name = bitMap.getString(KEY_BIT_MAP_NAME);
                } else {
                    fail("Bit map with no name");
                }

                if (bitMap.has(KEY_MSB)) {
                    msb = bitMap.getInt(KEY_MSB);
                } else {
                    fail("Bit map with no MSB");
                }

                if (bitMap.has(KEY_LSB)) {
                    lsb = bitMap.getInt(KEY_LSB);
                } else {
                    fail("Bit map with no LSB");
                }

                if (bitMap.has(KEY_DATA_TYPE)) {
                    dataType = DataType.valueOf(bitMap.getString("dataType"));
                } else {
                    dataType = DEFAULT_DATA_TYPE;
                }

                if (bitMap.has(KEY_MAX_VAL)) {
                    dataMax = bitMap.getInt(KEY_MAX_VAL);
                } else {
                    dataMax = 0;
                }

                if (bitMap.has(KEY_MIN_VAL)) {
                    dataMin = bitMap.getInt(KEY_MIN_VAL);
                } else {
                    dataMin = 0;
                }

                if (dataMax < dataMin) {
                    fail("Max val (" + dataMax + ") is less than min val (" + dataMin + ") for bit map " + name);
                }

                if (msb < lsb) {
                    fail("MSB (" + msb + ") is less than LSB (" + lsb + ") for bit map " + name);
                }

                assert name != null;
                assert dataType != null;

                if (dataMin == 0 && dataMax == 0) {
                    bitMaps.add(new BitMap(name, msb, lsb, dataType));
                } else {
                    bitMaps.add(new BitMap(name, msb, lsb, dataType, dataMax, dataMin));
                }
            }

            String name = null;
            int address = 0, length = 0;

            if (registerObject.has(KEY_REGISTER_NAME)) {
                name = registerObject.getString(KEY_REGISTER_NAME);
            } else {
                fail("Register with no name");
            }

            assert name != null;

            if (registerObject.has(KEY_ADDRESS)) {
                address = registerObject.getInt(KEY_ADDRESS);
            } else {
                fail("Register " + name + " with no address");
            }

            if (registerObject.has(KEY_LENGTH)) {
                length = registerObject.getInt(KEY_LENGTH);
            } else {
                fail("Register " + name + " does not specify length");
            }

            if (!registerObject.has(KEY_IS_READABLE) && !registerObject.has(KEY_IS_WRITABLE)) {
                // No read or write specification
                fail("Register " + name + " is no specified as read or write");
            }

            boolean isReadable = registerObject.has(KEY_IS_READABLE) && registerObject.getBoolean(KEY_IS_READABLE);
            boolean isWritable = registerObject.has(KEY_IS_WRITABLE) && registerObject.getBoolean(KEY_IS_WRITABLE);

            if (!isReadable && !isWritable) {
                fail("Register " + name + " is not specified as read nor write");
            }

            RegisterType registerType = null;
            if (isReadable && isWritable) {
                registerType = RegisterType.READ_WRITE;
            } else if (isReadable) {
                registerType = RegisterType.READ;
            } else if (isWritable) {
                registerType = RegisterType.WRITE;
            } else {
                assert false;
            }

            registers.add(new Register(name, registerType, bitMaps, address, length, DEFAULT_DATA));
        }

        return registers;
    }

    private static void fail(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    private static void warn(String msg) {
        System.err.println(msg);
    }

    public static String[] getAvailableRegMaps() {
        return SamplifierConstants.BundledRegisterMaps;
    }


}
