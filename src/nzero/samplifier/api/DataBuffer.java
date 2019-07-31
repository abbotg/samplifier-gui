package nzero.samplifier.api;

import java.util.Optional;

public class DataBuffer {
    private byte[] data;
    private boolean hasBeenRead;

    public DataBuffer(byte[] data) {
        this.data = data;
        this.hasBeenRead = false;
    }

    public synchronized Optional<byte[]> read() {
        byte[] tmp = data;
        data = null;
        hasBeenRead = true;
        return Optional.ofNullable(tmp);
    }

    public synchronized void set(byte[] bytes) {
        this.data = bytes;
        this.hasBeenRead = false;
    }

    public synchronized boolean hasBeenRead() {
        return hasBeenRead;
    }
}
