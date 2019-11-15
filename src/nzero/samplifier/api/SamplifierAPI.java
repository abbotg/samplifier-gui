package nzero.samplifier.api;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static java.lang.System.exit;

public class SamplifierAPI {

    private SamplifierAPI() {
    }

    @Nullable
    public static SamplifierConnection createConnection(String portName, SamplifierResponseListener listener) {
        SerialPort comPort = SerialPort.getCommPort(portName);
        comPort.setBaudRate(9600);

        PrintWriter output = null;
        BufferedReader input = null;

        //If the port is not closed, open the USB port.
        if (!comPort.isOpen()) {
            try {
                //Open the USB port and initialize the PrintWriter.
                comPort.openPort();
                Thread.sleep(1000);
                try { // TODO: bad practice, but library is the source of the problem here
                    output = new PrintWriter(comPort.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(comPort.getInputStream()));
                } catch (NullPointerException ignored) {
                    return null;
                }
            } catch (Exception c) {
                c.printStackTrace();
                exit(1);
            }

            //Update the console and status.
            System.out.println("Connection to Arduino successful.");

        } else {
            //If the port couldn't be opened print out to the console.
            System.out.println("Error opening port.");
            exit(1);
        }
        SamplifierConnection connection = new SamplifierConnection(portName, comPort, output, input, listener);
        return connection;
    }

    public static boolean isValidPort(String port) {
        try {
            SerialPort.getCommPort(port);
            return true;
        } catch (SerialPortInvalidPortException e) {
            return false;
        }
    }
}
