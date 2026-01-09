package wrk;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Xbox controller vibration support using XInput API via JNA
 * Provides haptic feedback for button presses and events
 * 
 * Uses Windows XInput1_4.dll (or XInput9_1_0.dll as fallback)
 * 
 * @author SpeleoThink Team
 */
public class WrkXboxVibration {
    
    private static boolean available = false;
    private static XInput xinput = null;
    private static ScheduledExecutorService executor;
    
    // Vibration intensities (0-65535)
    private static final int LIGHT_LEFT = 8000;
    private static final int LIGHT_RIGHT = 12000;
    private static final int MEDIUM_LEFT = 20000;
    private static final int MEDIUM_RIGHT = 25000;
    private static final int STRONG_LEFT = 45000;
    private static final int STRONG_RIGHT = 50000;
    
    // Durations
    private static final int SHORT_PULSE_MS = 80;
    private static final int MEDIUM_PULSE_MS = 150;
    private static final int LONG_PULSE_MS = 300;
    
    /**
     * XInput VIBRATION structure
     */
    @FieldOrder({"wLeftMotorSpeed", "wRightMotorSpeed"})
    public static class XINPUT_VIBRATION extends Structure {
        public short wLeftMotorSpeed;
        public short wRightMotorSpeed;
        
        public XINPUT_VIBRATION() {
            super();
        }
        
        public XINPUT_VIBRATION(int left, int right) {
            super();
            this.wLeftMotorSpeed = (short) left;
            this.wRightMotorSpeed = (short) right;
        }
    }
    
    /**
     * XInput native library interface
     */
    public interface XInput extends Library {
        int XInputSetState(int dwUserIndex, XINPUT_VIBRATION pVibration);
        int XInputGetState(int dwUserIndex, byte[] pState);
    }
    
    static {
        try {
            // Try to load XInput1_4 (Windows 8+)
            xinput = Native.load("XInput1_4", XInput.class);
            available = true;
            System.out.println("[VIBRATION] ✓ XInput1_4.dll loaded successfully - vibrations enabled!");
        } catch (UnsatisfiedLinkError e1) {
            try {
                // Fallback to XInput9_1_0 (Windows Vista/7)
                xinput = Native.load("XInput9_1_0", XInput.class);
                available = true;
                System.out.println("[VIBRATION] ✓ XInput9_1_0.dll loaded - vibrations enabled!");
            } catch (UnsatisfiedLinkError e2) {
                try {
                    // Last fallback
                    xinput = Native.load("XInput1_3", XInput.class);
                    available = true;
                    System.out.println("[VIBRATION] ✓ XInput1_3.dll loaded - vibrations enabled!");
                } catch (UnsatisfiedLinkError e3) {
                    System.out.println("[VIBRATION] ✗ XInput DLL not found - vibrations disabled");
                    available = false;
                }
            }
        } catch (Exception e) {
            System.out.println("[VIBRATION] ✗ Error loading XInput: " + e.getMessage());
            available = false;
        }
        
        if (available) {
            executor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "Xbox-Vibration");
                t.setDaemon(true);
                return t;
            });
            System.out.println("[VIBRATION] Vibration executor ready");
        }
    }
    
    /**
     * Check if vibration is available
     */
    public static boolean isAvailable() {
        return available;
    }
    
    /**
     * Light vibration for button press feedback
     */
    public static void vibrateLight(int controllerIndex) {
        vibrate(controllerIndex, LIGHT_LEFT, LIGHT_RIGHT, SHORT_PULSE_MS);
    }
    
    /**
     * Medium vibration for important actions
     */
    public static void vibrateMedium(int controllerIndex) {
        vibrate(controllerIndex, MEDIUM_LEFT, MEDIUM_RIGHT, MEDIUM_PULSE_MS);
    }
    
    /**
     * Strong vibration for alerts/warnings
     */
    public static void vibrateStrong(int controllerIndex) {
        vibrate(controllerIndex, STRONG_LEFT, STRONG_RIGHT, LONG_PULSE_MS);
    }
    
    /**
     * Double pulse vibration (for recording start/stop)
     */
    public static void vibrateDoublePulse(int controllerIndex) {
        if (!available) return;
        
        executor.execute(() -> {
            setVibration(controllerIndex, MEDIUM_LEFT, MEDIUM_RIGHT);
            sleep(100);
            setVibration(controllerIndex, 0, 0);
            sleep(80);
            setVibration(controllerIndex, MEDIUM_LEFT, MEDIUM_RIGHT);
            sleep(100);
            setVibration(controllerIndex, 0, 0);
        });
    }
    
    /**
     * Success pattern (ascending pulse)
     */
    public static void vibrateSuccess(int controllerIndex) {
        if (!available) return;
        
        executor.execute(() -> {
            setVibration(controllerIndex, 5000, 5000);
            sleep(50);
            setVibration(controllerIndex, 15000, 15000);
            sleep(50);
            setVibration(controllerIndex, 30000, 30000);
            sleep(80);
            setVibration(controllerIndex, 0, 0);
        });
    }
    
    /**
     * Error pattern (descending pulse)
     */
    public static void vibrateError(int controllerIndex) {
        if (!available) return;
        
        executor.execute(() -> {
            setVibration(controllerIndex, 40000, 40000);
            sleep(100);
            setVibration(controllerIndex, 20000, 20000);
            sleep(100);
            setVibration(controllerIndex, 10000, 10000);
            sleep(100);
            setVibration(controllerIndex, 0, 0);
        });
    }
    
    /**
     * Custom vibration with auto-stop
     * @param controllerIndex Controller index (0-3)
     * @param leftMotor Left motor intensity (0-65535)
     * @param rightMotor Right motor intensity (0-65535)
     * @param durationMs Duration before auto-stop
     */
    public static void vibrate(int controllerIndex, int leftMotor, int rightMotor, int durationMs) {
        if (!available) return;
        
        executor.execute(() -> {
            setVibration(controllerIndex, leftMotor, rightMotor);
            sleep(durationMs);
            setVibration(controllerIndex, 0, 0);
        });
    }
    
    /**
     * Set vibration directly (doesn't auto-stop)
     */
    public static void setVibration(int controllerIndex, int leftMotor, int rightMotor) {
        if (!available || xinput == null) return;
        
        try {
            XINPUT_VIBRATION vibration = new XINPUT_VIBRATION(leftMotor, rightMotor);
            int result = xinput.XInputSetState(controllerIndex, vibration);
            if (result != 0 && leftMotor > 0) {
                // Error code 1167 = device not connected, ignore silently
                if (result != 1167) {
                    System.out.println("[VIBRATION] XInputSetState error: " + result);
                }
            }
        } catch (Exception e) {
            System.err.println("[VIBRATION] Error: " + e.getMessage());
        }
    }
    
    /**
     * Stop vibration immediately
     */
    public static void stopVibration(int controllerIndex) {
        setVibration(controllerIndex, 0, 0);
    }
    
    /**
     * Stop all controllers
     */
    public static void stopAll() {
        for (int i = 0; i < 4; i++) {
            stopVibration(i);
        }
    }
    
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Cleanup resources
     */
    public static void shutdown() {
        stopAll();
        if (executor != null) {
            executor.shutdown();
            try {
                executor.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
