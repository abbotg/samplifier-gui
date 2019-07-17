package nzero.samplifier.model;

import java.util.ArrayList;
import java.util.List;

public class Register {

    private String name;
    //private int bitWidth; // handled by individual BitMaps
    private boolean isWritable;
    private List<BitMap> bitMaps;
    private int address; //TODO: implement this
    private int data;

    public Register(String name, int address, boolean isWritable, List<BitMap> bitMaps) {
        this.name = name;
        this.isWritable = isWritable;
        this.bitMaps = bitMaps;
        this.address = address;
    }

    public Register(Register other) {
        this.name = other.name;
        this.isWritable = other.isWritable;
        this.address = other.address;
        this.data = other.data;
        this.bitMaps = new ArrayList<>(other.bitMaps.size());
        for (BitMap bitMap : other.bitMaps) {
            this.bitMaps.add(new BitMap(bitMap));
        }
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

    public int getData() {
        return data;
    }

//    /**
//     * Concatenate all bitmaps into the single data value for this register
//     * @return
//     */
//    public int getData() {
//        int data = 0;
//        int position = 0;
//        for (ListIterator<BitMap> iterator = bitMaps.listIterator(bitMaps.size()); iterator.hasPrevious(); ) {
//            BitMap map = iterator.previous();
//            int mapData = map.getRawData();
//            mapData <<= position;
//            data |= mapData;
//            position += map.getLength();
//        }
//        return data;
//    }

    public int getData(int bitMapIndex) {
        assert bitMapIndex >= 0 && bitMapIndex < bitMaps.size();
        BitMap bitMap = bitMaps.get(bitMapIndex);
        return getData(bitMap);
    }

    public int getData(BitMap bitMap) {
        assert bitMaps.contains(bitMap);

        return (getMask(bitMap) & data) >> bitMap.getLsb(); // grab data using the mask
    }

    /**
     * Returns the proper bit mask of 1's for the mapping
     */
    private int getMask(BitMap bitMap) {
        int mask = (1 << bitMap.getLength()) - 1; // create proper sized mask
        mask <<= bitMap.getLsb(); // shift over to appropriate space
        return mask;
    }

    public void setData(int data) {
        this.data = data;
    }

    /**
     * Performs no checks on data size, etc. Assumes data is rightmost aligned.
     * @param bitMapIndex
     * @param data
     */
    public void setData(int bitMapIndex, int data) {
        BitMap bitMap = bitMaps.get(bitMapIndex);
        setData(bitMap, data);
    }

    public void setData(BitMap bitMap, int data) {
        this.data &= ~getMask(bitMap);
        this.data |= data << bitMap.getLsb();
    }

    /**
     * Returns a binary string representing the register, padded to its length
     * @return
     */
    public String getBinaryString() {
        return String.format("%"  + getBitWidth() + "s", Integer.toBinaryString(1));
    }

    @Override
    public String toString() {
        return getName();
    }
}

