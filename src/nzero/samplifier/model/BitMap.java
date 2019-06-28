package nzero.samplifier.model;

/**
 * Mapping of certain bits in a register to a function
 */
public class BitMap {
    private String name;
    private int msb, lsb;
    private Object data; // instanceof either String, Integer, or Boolean

    public BitMap(String name, int msb, int lsb, Object data) {
        this.name = name;
        this.msb = msb;
        this.lsb = lsb;

        int length = getLength();
        if (data == null) {
            if (length == 1) {
                this.data = Boolean.FALSE;
            } else {
                this.data = "";
                for (int i = 0; i < getLength(); i++) {
                    this.data += "0";
                }
            }
        } else {
            if (length == 1 && data instanceof Integer) {
                int intData = (Integer) data;
                this.data = intData == 0 ? Boolean.FALSE : Boolean.TRUE;
            }
            this.data = data;
        }

    }

    public BitMap(String name, int msb, int lsb) {
        this(name, msb, lsb, null);
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

    /**
     * Used for the GUI
     */
    public Object getData() {
        return data;
    }

    /**
     * Used for sending information via serial
     */
    public int getNumericData() {
        if (data instanceof Integer) {
            return (Integer) data;
        } else if (data instanceof String) {
            String str = (String) data;
            return Integer.valueOf(str);
        } else if (data instanceof Boolean) {
            return (Boolean) data ? 1 : 0;
        } else {
            assert false; // TODO
            return 0;
        }
    }

    public void setData(Object data) {
        this.data = data;
    }
}
