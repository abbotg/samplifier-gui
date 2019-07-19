package nzero.samplifier.util;

import nzero.samplifier.model.BitMap;
import nzero.samplifier.model.DataType;
import nzero.samplifier.model.Register;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegMapBootstrapper {

    private static final int DEFAULT_DATA = 0;

    private RegMapBootstrapper() {}

    public static List<Register> buildFromFile(String filename) {
        List<Register> registers = new ArrayList<>();

        String jsonData = FileUtils.readFile(filename);
        JSONArray registerArray = new JSONArray(jsonData);
        for (int i = 0; i < registerArray.length(); i++) {
            JSONObject registerObject = registerArray.getJSONObject(i);
            JSONArray bitMapsArray = registerObject.getJSONArray("bitMaps");

            List<BitMap> bitMaps = new ArrayList<>();
            for (int j = 0; j < bitMapsArray.length(); j++) {
                JSONObject bitMap = bitMapsArray.getJSONObject(j);

                bitMaps.add(new BitMap(
                        bitMap.getString("name"),
                        bitMap.getInt("msb"),
                        bitMap.getInt("lsb"),
                        DataType.valueOf(bitMap.getString("dataType")),
                        bitMap.getInt("dataMax"),
                        bitMap.getInt("dataMin"), DEFAULT_DATA));
            }
            registers.add(new Register(
                    registerObject.getString("name"),
                    registerObject.getInt("address"),
                    registerObject.getBoolean("isWritable"),
                    bitMaps));
        }

        return registers;
    }
}
