package nzero.samplifier.model;

import java.util.List;

public class Register {

    private String name;
    //private int bitWidth; // handled by individual BitMaps
    private boolean isWritable;
    private List<BitMap> bitMaps;
    private int address; //TODO: implement this

    public Register(String name, int address, boolean isWritable, List<BitMap> bitMaps) {
        this.name = name;
        this.isWritable = isWritable;
        this.bitMaps = bitMaps;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public List<BitMap> getBitMaps() {
        return bitMaps;
    }

    public int getBitWidth() {
        int count = 0;
        for (BitMap map : bitMaps) {
            count += map.getLength();
        }
        return count;
    }

    public boolean isWritable() {
        return isWritable;
    }

    /**
     * Gets the number of bit space mappings within the register
     */
    public int getNumMappings() {
        return bitMaps.size();
    }

    public int getAddress() {
        return address;
    }
}

