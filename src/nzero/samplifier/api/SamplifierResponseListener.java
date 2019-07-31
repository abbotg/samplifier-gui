package nzero.samplifier.api;

public interface SamplifierResponseListener {

    void didReadRegister(int address, int data);

    void didWriteRegister(int address, int data);

    void digitalIOUpdate(String pin, boolean value);

    // ??
}
