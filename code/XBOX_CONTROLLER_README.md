# Xbox Controller Integration - SpeleoThink Robot

## Overview
Xbox controller support has been integrated into the SpeleoThink robot control application. You can now control the robot using either a physical Xbox controller or keyboard inputs.

## Control Mapping

### Movement (Left Analog Stick / WASD Keys)
- **Left Stick Y-Axis** / **W/S Keys**: Move forward/backward
- **Left Stick X-Axis** / **A/D Keys**: Turn left/right
- The controls use tank drive calculation for smooth turning

### Head Control (D-Pad / Arrow Keys)
- **D-Pad Up** / **U Key**: Head up
- **D-Pad Down** / **J Key**: Head down
- Release to return head to neutral position

### Actions
- **Button A** / **Space**: Stand up robot
- **Button B**: Toggle LED on/off
- **Button X** / **L Key**: **Toggle Xbox control mode** (must press to enable controller)
- **Button Y** / **H Key**: Emergency stop (stops all movement)
- **Left Bumper** / **Q Key**: Dock
- **Right Bumper** / **E Key**: Undock

## How to Use

### With Physical Xbox Controller:
1. Connect your Xbox controller to the computer via USB or Bluetooth
2. Launch the application and connect to a robot
3. **Press Button X to enable Xbox control mode** (hostname will turn green and show "[XBOX MODE]")
4. Use the controls as mapped above
5. Press Button X again to disable Xbox control and return to manual button control

### With Keyboard (Testing Mode):
1. Launch the application and connect to a robot
2. Click on the main window to ensure it has focus
3. **Press L key to enable Xbox control mode**
4. Use WASD or arrow keys for movement
5. Use other mapped keys for functions
6. Press L again to disable Xbox control mode

## Safety Features
- **Dead zone**: Small stick movements (< 15%) are ignored to prevent drift
- **Emergency stop**: Button Y/H key immediately stops all robot movement
- **Auto-stop**: Robot stops automatically when Xbox control mode is disabled
- **Speed normalization**: Prevents motor speed commands from exceeding maximum values

## Technical Details

### New Files Created:
1. **bean/XboxButton.java**: Bean class for Xbox controller state
2. **wrk/WrkXboxController.java**: Worker thread for polling controller input
3. **ctrl/ICtrlXboxInput.java**: Interface for Xbox input callbacks

### Modified Files:
1. **ctrl/MainViewController.java**: Added Xbox controller integration and ICtrlXboxInput interface implementation

### Architecture:
- The `WrkXboxController` runs in a separate thread polling at 50ms intervals
- Button state changes are detected and trigger callbacks
- Analog stick values are continuously sent to the robot when Xbox mode is enabled
- Tank drive algorithm: `leftMotor = forward - turn`, `rightMotor = forward + turn`

## Troubleshooting

### Controller not responding:
- Ensure Xbox controller is properly connected
- Press Button X (or L key) to enable Xbox control mode
- Check that robot is connected (indicator should be green)

### Robot moving when stick is centered:
- Dead zone is set to 15% to prevent drift
- If still drifting, controller may need calibration in Windows settings

### Keyboard not working:
- Click on the robot control window to give it focus
- Ensure Xbox control mode is enabled (press L key)

## Future Enhancements
- Support for right analog stick (camera control if available)
- Trigger controls for variable speed
- Vibration feedback
- Configuration of button mappings
- Support for multiple controller types (PlayStation, etc.)
