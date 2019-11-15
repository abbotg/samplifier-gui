package nzero.samplifier.api;

public interface SamplifierResponseListener {

    void didReadRegister(int address, int data);

    void didWriteRegister(int address, boolean success);

    void digitalIOUpdate(String pin, boolean value);

    void portClosed(String portName);

    // ??
}
