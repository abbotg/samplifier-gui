package nzero.samplifier.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Register {

    private @NotNull String name;
    //private int bitWidth; // handled by individual BitMaps
    private @NotNull RegisterType registerType;
    private @NotNull List<BitMap> bitMaps;
    private int address; //TODO: implement this
    private int data;
    private int length;

    @Deprecated
    public Register(@NotNull String name, int address, boolean isWritable, @NotNull List<BitMap> bitMaps) {
        this.name = name;
        this.bitMaps = bitMaps;
        this.address = address;
        if (isWritable) {
            registerType = RegisterType.WRITE;
        } else {
            registerType = RegisterType.READ; // TODO: read-write
        }

    }

    public Register(@NotNull String name,
                    @NotNull RegisterType registerType,
                    @NotNull List<BitMap> bitMaps,
                    int address,
                    int length,
                    int data) {
        this.name = name;
        this.registerType = registerType;
        this.bitMaps = bitMaps;
        this.address = address;
        this.length = length;
        this.data = data;
    }

    public Register(Register other) {
        this.name = other.name;
        this.registerType = other.registerType;
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
//        int count = 0;
//        for (BitMap map : bitMaps) {
//            count += map.getLength();
//        }
//        return count;
        return length;
    }

    public int getLength() {
        return getBitWidth();
    }

    @NotNull
    public RegisterType getRegisterType() {
        return registerType;
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

    public boolean isWritable() {
        return registerType == RegisterType.WRITE || registerType == RegisterType.READ_WRITE;
    }

    public boolean isReadable() {
        return registerType == RegisterType.READ || registerType == RegisterType.READ_WRITE;
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
        return String.format("%"  + getBitWidth() + "s", Integer.toBinaryString(data)).replace(' ', '0');
    }

    @Override
    public String toString() {
        return getName();
    }
}

