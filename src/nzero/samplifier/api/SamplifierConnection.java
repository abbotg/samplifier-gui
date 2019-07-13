package nzero.samplifier.api;

import com.fazecast.jSerialComm.SerialPort;

public class SamplifierConnection {
    private String serialPortName;
    private SerialPort serialPort;

    SamplifierConnection(String serialPortName, SerialPort serialPort) {
        this.serialPortName = serialPortName;
        this.serialPort = serialPort;
    }

    public void writeRegister(int address, int data) {

    }

    public int readRegister(int address) {
        return 0;
    }

    public void disconnect() {

    }

    public boolean isConnected() {
        return false;
    }

}
