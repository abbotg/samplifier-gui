package nzero.samplifier;

public interface SamplifierConstants {

    String VERSION = "1.0";

    /**
     * Names of files, without extension or parent resource path.
     */
    String[] BundledRegisterMaps = {
            "S1",
            "S2"
    };

    interface Parser {
        String
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
    }
}
