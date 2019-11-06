package nzero.samplifier.test;

import nzero.samplifier.api.SamplifierAPI;
import nzero.samplifier.api.SamplifierConnection;
import nzero.samplifier.api.SamplifierResponseListener;

import java.text.MessageFormat;

public class ArduinoConnectionTest {
    public static void main(String[] args) {
        SamplifierConnection connection = SamplifierAPI.createConnection("/dev/ttyACM0");
        connection.setSamplifierResponseListener(new Listener());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(connection.isConnected());
        while (true) {
            connection.readRegister((char)0x24);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        connection.writeRegister((char)0x0024, 0x7766);
    }

    static class Listener implements SamplifierResponseListener {

        @Override
        public void didReadRegister(int address, int data) {
            System.out.println("Read register: address: " + address + ", data: " + data);
        }

        @Override
        public void didWriteRegister(int address, int data) {
            System.out.println("Wrote register: address " + address + ", data: " + data);
        }

        @Override
        public void digitalIOUpdate(String pin, boolean value) {

        }
    }
}
