package wrk;

import bean.XboxButton;
import ctrl.ICtrlXboxInput;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Worker thread that monitors Xbox controller input and notifies the controller
 * Uses JInput library for physical gamepad support with keyboard fallback
 * 
 * @author SpeleoThink Team
 */
public class WrkXboxController extends Thread {
    
    private volatile boolean running;
    private final ICtrlXboxInput refCtrl;
    private XboxButton xboxButton;          // Combined state (keyboard + gamepad)
    private XboxButton keyboardState;       // Keyboard-only state
    private static final int POLL_RATE = 50; // milliseconds
    
    // Dead zones to prevent drift/noise
    private static final double STICK_DEADZONE = 0.15;
    private static final double TRIGGER_DEADZONE = 0.1;
    
    // Previous state for button press detection
    private XboxButton previousState;
    
    // JInput controller
    private boolean jinputReady = false;
    private Controller gamepad = null;
    
    // Debug logging interval
    private int debugCounter = 0;
    private static final int DEBUG_LOG_INTERVAL = 100; // Log every 5 seconds (100 * 50ms)
    
    /**
     * Extract native libraries from jinput-platform jar to temp folder
     */
    private static void extractNatives() {
        try {
            String[] possiblePaths = {
                "lib/jinput-platform-2.0.7-natives-windows.jar",
                "code/lib/jinput-platform-2.0.7-natives-windows.jar"
            };
            
            File nativesJar = null;
            for (String path : possiblePaths) {
                File f = new File(path);
                if (f.exists()) {
                    nativesJar = f;
                    break;
                }
            }
            
            if (nativesJar == null) {
                System.out.println("[JINPUT] Natives jar not found");
                return;
            }
            
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "jinput-natives");
            tempDir.mkdirs();
            
            // Set library path for JInput
            System.setProperty("net.java.games.input.librarypath", tempDir.getAbsolutePath());
            
            try (JarFile jar = new JarFile(nativesJar)) {
                var entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".dll")) {
                        File outFile = new File(tempDir, new File(entry.getName()).getName());
                        if (!outFile.exists()) {
                            try (InputStream in = jar.getInputStream(entry);
                                FileOutputStream out = new FileOutputStream(outFile)) {
                                byte[] buffer = new byte[4096];
                                int len;
                                while ((len = in.read(buffer)) > 0) {
                                    out.write(buffer, 0, len);
                                }
                            }
                            System.out.println("[JINPUT] Extracted: " + outFile.getName());
                        }
                    }
                }
            }
            System.out.println("[JINPUT] Native libraries ready at: " + tempDir.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("[JINPUT] Failed to extract natives: " + e.getMessage());
        }
    }
    
    /**
     * Initialize JInput and find gamepad controller
     */
    private void initJInput() {
        try {
            // Extract native DLLs first
            extractNatives();
            
            // Get all controllers
            Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            System.out.println("[JINPUT] Found " + controllers.length + " controller(s)");
            
            for (Controller controller : controllers) {
                Controller.Type type = controller.getType();
                System.out.println("[JINPUT] - " + controller.getName() + " (Type: " + type + ")");
                
                if (type == Controller.Type.GAMEPAD || type == Controller.Type.STICK) {
                    gamepad = controller;
                    jinputReady = true;
                    System.out.println("[JINPUT] *** Selected: " + controller.getName() + " ***");
                    
                    // List components for debugging
                    System.out.println("[JINPUT] Components:");
                    for (Component comp : controller.getComponents()) {
                        System.out.println("[JINPUT]   - " + comp.getName() + 
                            " (ID: " + comp.getIdentifier() + ", Analog: " + comp.isAnalog() + ")");
                    }
                    break;
                }
            }
            
            if (!jinputReady) {
                System.out.println("[JINPUT] No gamepad found - using keyboard fallback");
            }
        } catch (Throwable t) {
            System.out.println("[JINPUT] Init failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            t.printStackTrace();
            jinputReady = false;
        }
    }
    
    /**
     * Poll gamepad and update XboxButton state
     */
    private void pollJInput() {
        if (!jinputReady || gamepad == null) return;
        
        try {
            if (!gamepad.poll()) {
                System.out.println("[JINPUT] Gamepad disconnected!");
                jinputReady = false;
                return;
            }
            
            // Note: xboxButton is already reset in run() loop
            
            for (Component comp : gamepad.getComponents()) {
                String name = comp.getName().toLowerCase();
                Component.Identifier id = comp.getIdentifier();
                float value = comp.getPollData();
                boolean isAnalog = comp.isAnalog();
                
                // Axes - apply dead zones
                if (id == Component.Identifier.Axis.X) {
                    xboxButton.setLeftStickX(applyDeadzone(value, STICK_DEADZONE));
                } else if (id == Component.Identifier.Axis.Y) {
                    xboxButton.setLeftStickY(applyDeadzone(value, STICK_DEADZONE));
                } else if (id == Component.Identifier.Axis.RX) {
                    xboxButton.setRightStickX(applyDeadzone(value, STICK_DEADZONE));
                } else if (id == Component.Identifier.Axis.RY) {
                    xboxButton.setRightStickY(applyDeadzone(value, STICK_DEADZONE));
                } else if (id == Component.Identifier.Axis.Z) {
                    // Z axis: can be combined triggers or left trigger depending on controller
                    // XInput controllers: Z goes from -1 (LT full) to +1 (RT full) combined
                    // DirectInput: Z might be 0 to 1 for one trigger
                    double triggerValue = normalizeTriggersFromZ(value);
                    // For combined axis, negative = LT, positive = RT
                    if (triggerValue < 0) {
                        xboxButton.setLeftTrigger(applyDeadzone(Math.abs(triggerValue), TRIGGER_DEADZONE));
                    } else {
                        xboxButton.setRightTrigger(applyDeadzone(triggerValue, TRIGGER_DEADZONE));
                    }
                } else if (id == Component.Identifier.Axis.RZ) {
                    // RZ is often the right trigger on some controllers
                    double triggerVal = (value + 1.0) / 2.0; // Normalize from [-1,1] to [0,1]
                    xboxButton.setRightTrigger(applyDeadzone(triggerVal, TRIGGER_DEADZONE));
                } else if (id == Component.Identifier.Axis.POV) {
                    // D-Pad as POV hat
                    if (value == Component.POV.UP) {
                        xboxButton.setdPadUp(true);
                    } else if (value == Component.POV.DOWN) {
                        xboxButton.setdPadDown(true);
                    } else if (value == Component.POV.LEFT) {
                        xboxButton.setdPadLeft(true);
                    } else if (value == Component.POV.RIGHT) {
                        xboxButton.setdPadRight(true);
                    } else if (value == Component.POV.UP_LEFT) {
                        xboxButton.setdPadUp(true);
                        xboxButton.setdPadLeft(true);
                    } else if (value == Component.POV.UP_RIGHT) {
                        xboxButton.setdPadUp(true);
                        xboxButton.setdPadRight(true);
                    } else if (value == Component.POV.DOWN_LEFT) {
                        xboxButton.setdPadDown(true);
                        xboxButton.setdPadLeft(true);
                    } else if (value == Component.POV.DOWN_RIGHT) {
                        xboxButton.setdPadDown(true);
                        xboxButton.setdPadRight(true);
                    }
                }
                
                // Buttons (digital)
                if (!isAnalog && id instanceof Component.Identifier.Button) {
                    boolean pressed = value > 0.5f;
                    String buttonId = id.getName();
                    
                    // Debug: log button presses
                    if (pressed) {
                        System.out.println("[JINPUT] Button pressed: id=" + buttonId + ", name=" + name);
                    }
                    
                    // Map by button index (most reliable for Xbox controllers)
                    switch (buttonId) {
                        case "0": xboxButton.setButtonA(pressed); break;
                        case "1": xboxButton.setButtonB(pressed); break;
                        case "2": xboxButton.setButtonX(pressed); break;
                        case "3": xboxButton.setButtonY(pressed); break;
                        case "4": xboxButton.setLeftBumper(pressed); break;
                        case "5": xboxButton.setRightBumper(pressed); break;
                        case "6": xboxButton.setBack(pressed); break;
                        case "7": xboxButton.setStart(pressed); break;
                        case "8": xboxButton.setLeftStickButton(pressed); break;
                        case "9": xboxButton.setRightStickButton(pressed); break;
                        case "10": xboxButton.setGuideButton(pressed); break; // Bouton Xbox/Guide
                        default:
                            // Fallback to name matching for non-standard controllers
                            if (name.equals("a") || name.equals("button a")) {
                                xboxButton.setButtonA(pressed);
                            } else if (name.equals("b") || name.equals("button b")) {
                                xboxButton.setButtonB(pressed);
                            } else if (name.equals("x") || name.equals("button x")) {
                                xboxButton.setButtonX(pressed);
                            } else if (name.equals("y") || name.equals("button y")) {
                                xboxButton.setButtonY(pressed);
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[JINPUT] Poll error: " + e.getMessage());
            jinputReady = false;
        }
    }
    
    public WrkXboxController(ICtrlXboxInput refCtrl) {
        super("Thread Xbox Controller");
        this.refCtrl = refCtrl;
        this.xboxButton = new XboxButton();
        this.keyboardState = new XboxButton();
        this.previousState = new XboxButton();
        initJInput();
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public XboxButton getXboxButton() {
        return xboxButton;
    }
    
    /**
     * Updates button state from keyboard input (for testing without controller)
     * Can be called from JavaFX thread - updates separate keyboard state
     * 
     * Mapping:
     * W / UP = RT (avancer), S / DOWN = LT (reculer)
     * A / LEFT = tourner gauche, D / RIGHT = tourner droite
     */
    public void updateFromKeyboard(KeyEvent event, boolean pressed) {
        if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
            // W/UP = Right Trigger = avancer
            keyboardState.setRightTrigger(pressed ? 1.0 : 0.0);
        } else if (event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN) {
            // S/DOWN = Left Trigger = reculer
            keyboardState.setLeftTrigger(pressed ? 1.0 : 0.0);
        } else if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
            // A/LEFT = tourner à gauche (stick X négatif)
            keyboardState.setLeftStickX(pressed ? -1.0 : 0.0);
        } else if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
            // D/RIGHT = tourner à droite (stick X positif)
            keyboardState.setLeftStickX(pressed ? 1.0 : 0.0);
        } else if (event.getCode() == KeyCode.SPACE) {
            keyboardState.setButtonA(pressed);
        } else if (event.getCode() == KeyCode.F) {
            keyboardState.setButtonB(pressed); // F = Flashlight/LED toggle
        } else if (event.getCode() == KeyCode.L) {
            keyboardState.setButtonX(pressed);
        } else if (event.getCode() == KeyCode.H) {
            keyboardState.setButtonY(pressed);
        } else if (event.getCode() == KeyCode.U) {
            keyboardState.setdPadUp(pressed);
        } else if (event.getCode() == KeyCode.J) {
            keyboardState.setdPadDown(pressed);
        } else if (event.getCode() == KeyCode.Q) {
            keyboardState.setLeftBumper(pressed);
        } else if (event.getCode() == KeyCode.E) {
            keyboardState.setRightBumper(pressed);
        } else if (event.getCode() == KeyCode.R) {
            keyboardState.setStart(pressed); // R = Toggle Recording (Start button)
        }
    }
    
    /**
     * Manual update method for gamepad values from JavaFX scene
     */
    public synchronized void updateGamepadState(XboxButton newState) {
        this.xboxButton = newState;
    }
    
    @Override
    public void run() {
        running = true;
        System.out.println("[XBOX] Controller thread started (JInput ready: " + jinputReady + ")");
        
        while (running) {
            _sleep(POLL_RATE);
            
            // Reset xboxButton state before combining inputs
            xboxButton.reset();
            
            // Poll physical gamepad if available (updates xboxButton)
            if (jinputReady) {
                pollJInput();
            }
            
            // Combine keyboard state with gamepad state
            // Keyboard takes priority if it has input
            combineInputs();
            
            // Notify controller
            if (refCtrl != null) {
                refCtrl.onXboxInputReceived(xboxButton);
                checkButtonPress();
                copyState(xboxButton, previousState);
            }
        }
        
        System.out.println("[XBOX] Controller thread stopped");
    }
    
    /**
     * Apply dead zone to an axis value
     */
    private double applyDeadzone(double value, double deadzone) {
        if (Math.abs(value) < deadzone) {
            return 0.0;
        }
        // Scale the remaining range to 0-1
        double sign = value > 0 ? 1.0 : -1.0;
        return sign * (Math.abs(value) - deadzone) / (1.0 - deadzone);
    }
    
    /**
     * Normalize trigger value from Z axis
     * Some controllers use Z as combined triggers (-1 to +1)
     * Others use Z as a single trigger (0 to 1 or -1 to 1)
     */
    private double normalizeTriggersFromZ(float value) {
        // Return value as-is, the caller will handle negative (LT) vs positive (RT)
        return value;
    }
    
    /**
     * Combine keyboard and gamepad inputs - keyboard takes priority when active
     */
    private void combineInputs() {
        // Axes: use keyboard if it has input, otherwise use gamepad
        if (Math.abs(keyboardState.getLeftStickX()) > 0.01) {
            xboxButton.setLeftStickX(keyboardState.getLeftStickX());
        }
        if (Math.abs(keyboardState.getLeftStickY()) > 0.01) {
            xboxButton.setLeftStickY(keyboardState.getLeftStickY());
        }
        
        // Triggers: use keyboard if it has input (for RT/LT movement)
        if (keyboardState.getRightTrigger() > 0.01) {
            xboxButton.setRightTrigger(keyboardState.getRightTrigger());
        }
        if (keyboardState.getLeftTrigger() > 0.01) {
            xboxButton.setLeftTrigger(keyboardState.getLeftTrigger());
        }
        
        // Debug logging
        if (debugCounter++ % DEBUG_LOG_INTERVAL == 0) {
            double lt = xboxButton.getLeftTrigger();
            double rt = xboxButton.getRightTrigger();
            double lx = xboxButton.getLeftStickX();
            if (lt > 0.01 || rt > 0.01 || Math.abs(lx) > 0.01) {
                System.out.printf("[XBOX DEBUG] LT=%.2f RT=%.2f StickX=%.2f%n", lt, rt, lx);
            }
        }
        
        // Buttons: OR them together (either source can trigger)
        if (keyboardState.isButtonA()) xboxButton.setButtonA(true);
        if (keyboardState.isButtonB()) xboxButton.setButtonB(true);
        if (keyboardState.isButtonX()) xboxButton.setButtonX(true);
        if (keyboardState.isButtonY()) xboxButton.setButtonY(true);
        if (keyboardState.isLeftBumper()) xboxButton.setLeftBumper(true);
        if (keyboardState.isRightBumper()) xboxButton.setRightBumper(true);
        if (keyboardState.isdPadUp()) xboxButton.setdPadUp(true);
        if (keyboardState.isdPadDown()) xboxButton.setdPadDown(true);
        if (keyboardState.isdPadLeft()) xboxButton.setdPadLeft(true);
        if (keyboardState.isdPadRight()) xboxButton.setdPadRight(true);
    }
    
    /**
     * Check for button press events (state change from false to true)
     * Also triggers light vibration feedback on button press
     */
    private void checkButtonPress() {
        boolean anyButtonPressed = false;
        
        if (xboxButton.isButtonA() && !previousState.isButtonA()) {
            refCtrl.onButtonAPressed();
            anyButtonPressed = true;
        }
        if (xboxButton.isButtonB() && !previousState.isButtonB()) {
            refCtrl.onButtonBPressed();
            anyButtonPressed = true;
        }
        if (xboxButton.isButtonX() && !previousState.isButtonX()) {
            refCtrl.onButtonXPressed();
            anyButtonPressed = true;
        }
        if (xboxButton.isButtonY() && !previousState.isButtonY()) {
            refCtrl.onButtonYPressed();
            anyButtonPressed = true;
        }
        
        // Start button for recording toggle
        if (xboxButton.isStart() && !previousState.isStart()) {
            refCtrl.onStartPressed();
            anyButtonPressed = true;
        }
        
        // Guide/Xbox button for connection toggle (ID 10)
        if (xboxButton.isGuideButton() && !previousState.isGuideButton()) {
            refCtrl.onGuidePressed();
            anyButtonPressed = true;
        }
        
        // Light vibration feedback on any button press
        if (anyButtonPressed) {
            WrkXboxVibration.vibrateLight(0);
        }
        
        // Also vibrate on bumper presses
        if ((xboxButton.isLeftBumper() && !previousState.isLeftBumper()) ||
            (xboxButton.isRightBumper() && !previousState.isRightBumper())) {
            WrkXboxVibration.vibrateLight(0);
        }
    }
    
    /**
     * Copy state from source to destination
     */
    private void copyState(XboxButton source, XboxButton dest) {
        dest.setButtonA(source.isButtonA());
        dest.setButtonB(source.isButtonB());
        dest.setButtonX(source.isButtonX());
        dest.setButtonY(source.isButtonY());
        dest.setLeftBumper(source.isLeftBumper());
        dest.setRightBumper(source.isRightBumper());
        dest.setBack(source.isBack());
        dest.setStart(source.isStart());
        dest.setLeftStickButton(source.isLeftStickButton());
        dest.setRightStickButton(source.isRightStickButton());
        dest.setGuideButton(source.isGuideButton());
        dest.setdPadUp(source.isdPadUp());
        dest.setdPadDown(source.isdPadDown());
        dest.setdPadLeft(source.isdPadLeft());
        dest.setdPadRight(source.isdPadRight());
        dest.setLeftStickX(source.getLeftStickX());
        dest.setLeftStickY(source.getLeftStickY());
        dest.setRightStickX(source.getRightStickX());
        dest.setRightStickY(source.getRightStickY());
        dest.setLeftTrigger(source.getLeftTrigger());
        dest.setRightTrigger(source.getRightTrigger());
    }
    
    private void _sleep(int millis) {
        try {
            sleep(millis);
        } catch (InterruptedException ex) {
            System.err.println("Erreur lors du sleep du thread " + super.getName()
                    + ". \n" + ex.getMessage());
        }
    }
}
