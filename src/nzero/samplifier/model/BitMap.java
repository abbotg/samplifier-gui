package nzero.samplifier.model;

/**
 * Mapping of certain bits in a register to a function
 */
public class BitMap {
    private final String name;
    private final int msb, lsb;
    private final DataType dataType; // immutable
    private final int maxVal, minVal;
    private int data; // instanceof either String, Integer, or Boolean

    public BitMap(String name,
                  int msb,
                  int lsb,
                  DataType dataType,
                  int maxVal,
                  int minVal,
                  int data) {
        this.name = name;
        this.msb = msb;
        this.lsb = lsb;
        this.data = data;

        if (maxVal - minVal == 0) {
            this.maxVal = 2 << getLength() - 1;
            this.minVal = 0;
        } else {
            this.maxVal = maxVal;
            this.minVal = minVal;
        }

        if (msb - lsb == 0) { // single bit
            this.dataType = DataType.BOOL;
        } else {
            this.dataType = dataType;
        }
    }

    public BitMap(BitMap other) {
        this.name = other.name;
        this.msb = other.msb;
        this.lsb = other.lsb;
        this.dataType = other.dataType;
        this.maxVal = other.maxVal;
        this.minVal = other.minVal;
        this.data = other.data;
    }

    public String getName() {
        return name;
    }

    public String getBitRange() {
        return msb + ":" + lsb;
    }

    public int getLength() {
        return msb - lsb + 1;
    }

    public int getDataMaxVal() {
        return maxVal;
    }

    public int getDataMinVal() {
        return minVal;
    }

    public int getDataRange() {
        return maxVal - minVal;
    }

    public DataType getDataType() {
        return dataType;
    }

//    /**
//     * Used for the GUI
//     */
//    public Object getData() {
//        switch (dataType) {
//            case BOOL:
//                return data == 1 ? Boolean.TRUE : Boolean.FALSE;
//            case BIN:
//                return String.format("%" + getLength() + "s", Integer.toBinaryString(data)).replace(' ', '0');
//            case DEC:
//                return Integer.toString(data);
//            case HEX:
//                return Integer.toHexString(data);
//            default:
//                return null; // TODO: ?
//        }
//    }

//    public int getRawData() {
//        return data;
//    }

//    public void setData(int data) {
//        this.data = data;
//    }

//    /**
//     * Used for the GUI
//     * @param data
//     * @throws NumberFormatException
//     * @throws InvalidInputLengthException
//     */
//    public void setData(Object data) throws NumberFormatException, InvalidInputLengthException {
//        int val = 0;
//        if (dataType == DataType.BOOL) {
//            assert data instanceof Boolean; // this should be handled elsewhere
//            val = (Boolean) data ? 1 : 0;
//        } else {
//            assert data instanceof String;
//            switch (dataType) {
//                case BIN:
//                    val = Integer.parseUnsignedInt((String) data, 2);
//                    break;
//                case DEC:
//                    val = Integer.parseUnsignedInt((String) data, 10);
//                    break;
//                case HEX:
//                    val = Integer.parseUnsignedInt((String) data, 16);
//                    break;
//            }
//            if (val > maxVal || val < minVal) {
//                throw new InvalidInputLengthException();
//            }
//        }
//        setData(val);
//    }

//    public void fillWithEmptyData() {
//        this.data = 0;
//    }

    public int getMsb() {
        return msb;
    }

    public int getLsb() {
        return lsb;
    }

}
