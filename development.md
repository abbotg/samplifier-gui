# samplifier-gui

## Features

- Swing GUI
    - 21 write registers
    - 4 read registers
    - Write all/read all button
    - Write profiles
        - Default write profile (settable)
    - Basic mode, showing breakdown of bits within each register
        - Fields render as checkboxes for single bit mappings
    - Advanced mode, showing all editable register fields
        - Manual address and data write field
- Detect/select Arduino device
- Communicate with Arduino using jSerialComm library
- Cross platform (Win, Mac, Linux)

## Future Features

- xRegister input data can be hex or decimal
- Oscilloscope-like waveform viewing
- Real time register updates
- Documentation
- Bundled self-install of Arduino sketch?


## To-do

- xStore bitmap data as raw integer, handle cast to Boolean/etc on transaction
- xReg map bootstrapper
- xForce use Metal LAF
- xFigure out local pref/profile storage schema
- xProfile bootstrapper
- xIntegrate profile controls into menus
- GUI/Arduino communication sketch
- Test bench/CLI wrapper
- xdataMin and dataMax in RegisterMapping.json can be null
- xHandling of "Dont care" bitmaps separately than "Inaccessible", DC should be a recorded bit map in the register mapping.
This is because the register doesn't hold onto its size as an attribute, it infers it from the bit map sizes
- Get bit map data types

## Documentation notes

- dataType is a Dont Care if length is one (RegMapBootstrapper)
- 

## Menus

- Connection
    - Connect
    - Disconnect
- View
    - Register edit mode
        - Manual
        - Basic
        - Advanced
        - Debug
    - Format data as checkbox, etc?
    - Show bit ranges?
    - Show write regs in tabbed pane or combo box
- Profile
    - Save as...
    - Load...
    - Load default
- Help
    - Documentation
    

## USB Communication protocol

First byte is op code

### Operations (to Arduino)

- Write (single) (4 bytes)
    - 0: op code
    - 1: address
    - 2-3: data
- Read (single) (4 bytes)
    - 0: op code
    - 1: address
    - 2-3: blank
    

### Callbacks (from Arduino)

- Read (single) (4 bytes)
    - 0: op code
    - 1: address
    - 2-3: data
- Write (single)
    - 0: op code
    - 1: address
    - 2: 1 or 0, for success or failure
    - 3: blank
    
    
## GUI refactor

- xDynamic read pane, write pane, and read/write pane (they don't render if there are no valid registers)
    - xtables automatically detect number of regs and render with tabs or combo boxes
    - with option to choose manually
- xAny individual register can be popped out in its own window


## Final features before feature freeze

- Automatic Arduino port detection
- Continuous read
- xSamplifier generation popup (S1 or S2)
- Import profile
- Import samplifier gen
- Command line switches
- Documentation window
- Mouse-over hints (in progress)
- Basic mode column selection, enable/disable
- Encapsulate all calls to Preferences API within custom class
- Move more global constants to SamplifierConstants

## Notable debug items

- Arduino serial communication

## After feature freeze

- Create a test suite