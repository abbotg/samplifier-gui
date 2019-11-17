package nzero.samplifier.api;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SamplifierConnection {
    private String serialPortName;
    private SerialPort serialPort;
    private SamplifierResponseListener listener;
    private PrintWriter output;
    private BufferedReader input;

//    private byte[] readBuffer;
//    private int bufferIndex;
    private Queue<Byte> readBuffer;
    private Lock bufferLock;

    private static final byte
            READ_OPERATION_CODE = 0x0,
            WRITE_OPERATION_CODE = 0x1,
            CHIP_RESET_CODE = 0x2,
            READ_CALLBACK_CODE = 0x0,
            WRITE_CALLBACK_CODE = 0x1;


    SamplifierConnection(String serialPortName,
                         SerialPort serialPort,
                         PrintWriter output,
                         BufferedReader input,
                         SamplifierResponseListener listener) {
        this.serialPortName = serialPortName;
        this.serialPort = serialPort;
        this.output = output;
        this.input = input;
        this.bufferLock = new ReentrantLock();
//        this.readBuffer = new byte[4];
        this.readBuffer = new LinkedList<>();
        this.listener = listener;

        this.serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                int avail = serialPort.bytesAvailable();
                if (avail == -1) {
                    listener.portClosed(serialPortName);
                }
                byte[] newData = new byte[avail];
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
            readBuffer.add(b);
        }
        if (readBuffer.size() >= 4) {
            dispatchSerialEvent();
            readBuffer.clear();
        }
        bufferLock.unlock();
    }

    private void dispatchSerialEvent() {
        byte[] data = new byte[4];
        for (int i = 0; i < 4; i++) {
            data[i] = readBuffer.poll();
        }
        /*
         * Implementation details
         * @see development.md
         */
        int address, val;
        // Handle op code
        switch (data[0]) {
            case READ_CALLBACK_CODE: // Read (single)
                address = (int) data[1];
                val = splice16(data[2], data[3]);
                listener.didReadRegister(address, val);
                break;
            case WRITE_CALLBACK_CODE: // Write (single)
                address = (int) data[1];
                val = data[2];
                listener.didWriteRegister(address, val > 0);
                break;
            default:
//                throw new UnsupportedOperationException();
        }
    }

    public void writeRegister(int address, int data) {
        if (listener == null) {
            throw new RuntimeException();
        }
//        short send1 = splice16(WRITE_OPERATION_CODE, (byte) address);
//        short send2 = (short) data;

        byte[] buf = new byte[4];
        buf[0] = WRITE_OPERATION_CODE;
        buf[1] = (byte) address;
        buf[2] = upperHalf((short) data);
        buf[3] = lowerHalf((short) data);

//        System.out.printf("GUI: %s ");
//        System.out.printf("Write: %s (Opcode: %s) (Addr: %s) (Data top 8: %s) (Data bottom 8: %s)%n",
//                Integer.toBinaryString(splice32(send1, send2)),
//                Integer.toBinaryString((send1 & 0xFF00) >>> 8),
//                Integer.toBinaryString((send1 & 0x00FF)),
//                Integer.toBinaryString((send2 & 0xFF00) >>> 8),
//                Integer.toBinaryString((send2 & 0x00FF)));
        serialPort.writeBytes(buf, 4);
//        output.write(send1);
//        output.write(send2);
//        output.flush();
    }

    public void readRegister(int address) {
        byte[] buf = new byte[4];
        buf[0] = READ_OPERATION_CODE;
        buf[1] = (byte) address;
        buf[2] = 0;
        buf[3] = 0;

        int send = splice32(splice16(buf[0], buf[1]), splice16(buf[2], buf[3]));
        System.out.printf("Read: %s (Opcode: %s) (Addr: %s) (Data top 8: %s) (Data bottom 8: %s)%n",
                Integer.toBinaryString(send),
                Integer.toBinaryString(buf[0]),
                Integer.toBinaryString(buf[1]),
                Integer.toBinaryString(buf[2]),
                Integer.toBinaryString(buf[3]));
//        output.write(c);
//        output.write(send);
//        output.write(0);
        serialPort.writeBytes(buf, 4);
//        output.flush();
    }

    public void chipReset() {
        byte[] buf = new byte[4];
        buf[0] = CHIP_RESET_CODE;
        buf[1] = buf[2] = buf[3] = 0;
        System.out.println("GUI: Sending reset command...");
        serialPort.writeBytes(buf, 4);
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

    private int splice32(short msb, short lsb) {
        return (int) (((int) msb << 16) | lsb);
    }

    private short splice16(byte msb, byte lsb) {
        return (short) (((short) msb << 8) | lsb);
    }

    private byte upperHalf(short data) {
        return (byte) (data >>> 8);
    }

    private byte lowerHalf(short data) {
        return (byte) data;
    }
}
