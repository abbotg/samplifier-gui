# Samplifier GUI

## Installation

- Requirements
    - A Java runtime environment (JRE) version 8 or greater (1.8.0) which can be installed [here](https://www.oracle.com/technetwork/java/javase/downloads/index.html) (this is required to run any Java programs)
    - The Arduino IDE (for the USB drivers it bundles for Windows) which is found [here](https://www.arduino.cc/en/main/software)
- Download the file `samplifier-gui.jar` from the [releases page](https://github.com/abbotg/samplifier-gui/releases)
- Double click the file to run it

### Running from the command line

You can alternatively run the program from the command line if double clicking the file does not work.

On macOS and Linux, run
```shell script
java -jar /path/to/samplifier-gui.jar
```

Where `/path/to/` is the directory containing your `.jar` file. For example, on macOS, this would look something like

```shell script
java -jar /Users/Gunther/Downloads/samplifier-gui.jar
```

### Updating
To update, all you have to do is download the latest `samplifier-gui.jar` file from the [releases page](https://github.com/abbotg/samplifier-gui/releases) and delete your previously downloaded copy.

## Usage

Currently the COM port (on Windows) or device file (on macOS/Linux) has to be manually selected by the user. To find the port used, 
- Plug in the Arduino
- Open the Arduino IDE
- Navigate to Tools > Port in the menu bar
- The COM port or device file will be listed here. On Windows, it will look something like `COM1` or `COM3`. On macOS and Linux, it will look something like `/dev/ttyACM0` or `/dev/ttyS0`. Note this port, and enter it into the prompt in the Samplifier GUI that opens when you select Connection > Connect in the menu bar.