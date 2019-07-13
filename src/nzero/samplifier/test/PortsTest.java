package nzero.samplifier.test;

import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;

public class PortsTest {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(SerialPort.getCommPorts()));
    }
}
