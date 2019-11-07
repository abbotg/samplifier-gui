package nzero.samplifier.api;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SamplifierConnection {
    private String serialPortName;
    private SerialPort serialPort;
    private SamplifierResponseListener listener;
    private PrintWriter output;
    private BufferedReader input;

    private StringBuilder readBuffer;
    private Lock bufferLock;

    private static final char
            READ_OPERATION_CODE = 0x0,
            WRITE_OPERATION_CODE = 0x1,
            READ_CALLBACK_CODE = 0x0,
            WRITE_CALLBACK_CODE = 0x1;


    SamplifierConnection(String serialPortName,
                         SerialPort serialPort,
                         PrintWriter output,
                         BufferedReader input) {
        this.serialPortName = serialPortName;
        this.serialPort = serialPort;
        this.output = output;
        this.input = input;
        this.bufferLock = new ReentrantLock();
        this.readBuffer = new StringBuilder();

        this.serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[serialPort.bytesAvailable()];
                int numRead = serialPort.readBytes(newData, newData.length);
//                System.out.println("Read " + numRead + " bytes.");
//                System.out.println("Got data" + Arrays.toString(newData));
                bufferSerialData(newData);
            }
        });
    }

    private void bufferSerialData(byte[] data) {
        bufferLock.lock();
        if (data.length > 4) {
            System.err.println("Error: too many bytes from serial"); //todo: fix this
        }
        for (byte b : data) {
            readBuffer.append((char) b);
        }
        if (readBuffer.length() == 4) {
            dispatchSerialEvent(readBuffer.toString().toCharArray());
            readBuffer.delete(0, readBuffer.length());
            readBuffer.setLength(0);
        }
        bufferLock.unlock();
    }

    private void dispatchSerialEvent(char[] data) {

        /*
         * Implementation details
         * @see development.md
         */
        int address, val;
        try {
            // Handle op code
            switch (data[0]) {
                case READ_CALLBACK_CODE: // Read (single)
                    address = (int) data[1];
                    val = splice(data[2], data[3]);
                    listener.didReadRegister(address, val);
                    break;
                case WRITE_CALLBACK_CODE: // Write (single)
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (IndexOutOfBoundsException e) {
            // malformed
        }
    }

    public void setSamplifierResponseListener(SamplifierResponseListener listener) {
        this.listener = listener;
    }

    public void writeRegister(char address, int data) {
        if (listener == null) {
            return;
        }
        char[] buffer = new char[4];
        buffer[0] = WRITE_OPERATION_CODE; // write opcode TODO: replace w/ global constant
        buffer[1] = address;
        buffer[2] = upperHalf(data);
        buffer[3] = lowerHalf(data);
        output.write(buffer);
        output.flush();
    }

    public void readRegister(char address) {
        char[] buffer = new char[4];
        buffer[0] = READ_OPERATION_CODE;
        buffer[1] = address;
        buffer[2] = 0;
        buffer[3] = 0;
        output.write(buffer);
        output.flush();
    }

    public void disconnect() {
        output.close();
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serialPort.closePort();
    }

    public boolean isConnected() {
        return serialPort.isOpen();
    }

    private int splice(char msb, char lsb) {
        return (((int) msb) << 8) | lsb;
    }

    private char upperHalf(int data) {
        return (char) (data >>> 8);
    }

    private char lowerHalf(int data) {
        return (char) data;
    }

}
