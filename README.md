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

- Register input data can be hex or decimal
- Oscilloscope-like waveform viewing
- Real time register updates
- Documentation
- Bundled self-install of Arduino sketch?


## To-do

- Store bitmap data as raw integer, handle cast to Boolean/etc on transaction
- Reg map bootstrapper
- Force use Metal LAF
- Figure out local pref/profile storage schema

## Figure out

- Bit map data types: negative? bin, hex, dec?

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
- Profile
    - Save as...
    - Load...
    - Load default
- Help
    - Documentation
    
## Development timeline

Feature (Goal)
- Internal code restructuring (Jul 10)
- USB communication b/t GUI and Arduino (Jul 12)
- Profiles (Jul 14)
- Extra features: continuous read, manual register entry mode, documentation over the next few days
