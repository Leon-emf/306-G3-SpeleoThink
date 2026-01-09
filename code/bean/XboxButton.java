package bean;

/**
 * Bean class representing Xbox controller button state and analog values
 * 
 * @author SpeleoThink Team
 */
public class XboxButton {
    
    // Button states
    private boolean buttonA;
    private boolean buttonB;
    private boolean buttonX;
    private boolean buttonY;
    private boolean leftBumper;
    private boolean rightBumper;
    private boolean back;
    private boolean start;
    private boolean leftStickButton;
    private boolean rightStickButton;
    private boolean guideButton; // Bouton Xbox (ID 10)
    private boolean dPadUp;
    private boolean dPadDown;
    private boolean dPadLeft;
    private boolean dPadRight;
    
    // Analog values (-1.0 to 1.0)
    private double leftStickX;
    private double leftStickY;
    private double rightStickX;
    private double rightStickY;
    private double leftTrigger;  // 0.0 to 1.0
    private double rightTrigger; // 0.0 to 1.0
    
    public XboxButton() {
        reset();
    }
    
    /**
     * Reset all button states and analog values to default
     */
    public void reset() {
        buttonA = false;
        buttonB = false;
        buttonX = false;
        buttonY = false;
        leftBumper = false;
        rightBumper = false;
        back = false;
        start = false;
        leftStickButton = false;
        rightStickButton = false;
        guideButton = false;
        dPadUp = false;
        dPadDown = false;
        dPadLeft = false;
        dPadRight = false;
        leftStickX = 0.0;
        leftStickY = 0.0;
        rightStickX = 0.0;
        rightStickY = 0.0;
        leftTrigger = 0.0;
        rightTrigger = 0.0;
    }
    
    // Getters and Setters
    
    public boolean isButtonA() {
        return buttonA;
    }
    
    public void setButtonA(boolean buttonA) {
        this.buttonA = buttonA;
    }
    
    public boolean isButtonB() {
        return buttonB;
    }
    
    public void setButtonB(boolean buttonB) {
        this.buttonB = buttonB;
    }
    
    public boolean isButtonX() {
        return buttonX;
    }
    
    public void setButtonX(boolean buttonX) {
        this.buttonX = buttonX;
    }
    
    public boolean isButtonY() {
        return buttonY;
    }
    
    public void setButtonY(boolean buttonY) {
        this.buttonY = buttonY;
    }
    
    public boolean isLeftBumper() {
        return leftBumper;
    }
    
    public void setLeftBumper(boolean leftBumper) {
        this.leftBumper = leftBumper;
    }
    
    public boolean isRightBumper() {
        return rightBumper;
    }
    
    public void setRightBumper(boolean rightBumper) {
        this.rightBumper = rightBumper;
    }
    
    public boolean isBack() {
        return back;
    }
    
    public void setBack(boolean back) {
        this.back = back;
    }
    
    public boolean isStart() {
        return start;
    }
    
    public void setStart(boolean start) {
        this.start = start;
    }
    
    public boolean isLeftStickButton() {
        return leftStickButton;
    }
    
    public void setLeftStickButton(boolean leftStickButton) {
        this.leftStickButton = leftStickButton;
    }
    
    public boolean isRightStickButton() {
        return rightStickButton;
    }
    
    public void setRightStickButton(boolean rightStickButton) {
        this.rightStickButton = rightStickButton;
    }
    
    public boolean isdPadUp() {
        return dPadUp;
    }
    
    public void setdPadUp(boolean dPadUp) {
        this.dPadUp = dPadUp;
    }
    
    public boolean isdPadDown() {
        return dPadDown;
    }
    
    public void setdPadDown(boolean dPadDown) {
        this.dPadDown = dPadDown;
    }
    
    public boolean isdPadLeft() {
        return dPadLeft;
    }
    
    public void setdPadLeft(boolean dPadLeft) {
        this.dPadLeft = dPadLeft;
    }
    
    public boolean isdPadRight() {
        return dPadRight;
    }
    
    public void setdPadRight(boolean dPadRight) {
        this.dPadRight = dPadRight;
    }
    
    public double getLeftStickX() {
        return leftStickX;
    }
    
    public void setLeftStickX(double leftStickX) {
        this.leftStickX = applyDeadzone(leftStickX);
    }
    
    public double getLeftStickY() {
        return leftStickY;
    }
    
    public void setLeftStickY(double leftStickY) {
        this.leftStickY = applyDeadzone(leftStickY);
    }
    
    public double getRightStickX() {
        return rightStickX;
    }
    
    public void setRightStickX(double rightStickX) {
        this.rightStickX = applyDeadzone(rightStickX);
    }
    
    public double getRightStickY() {
        return rightStickY;
    }
    
    public void setRightStickY(double rightStickY) {
        this.rightStickY = applyDeadzone(rightStickY);
    }
    
    public double getLeftTrigger() {
        return leftTrigger;
    }
    
    public void setLeftTrigger(double leftTrigger) {
        this.leftTrigger = leftTrigger;
    }
    
    public double getRightTrigger() {
        return rightTrigger;
    }
    
    public void setRightTrigger(double rightTrigger) {
        this.rightTrigger = rightTrigger;
    }
    
    public boolean isGuideButton() {
        return guideButton;
    }
    
    public void setGuideButton(boolean guideButton) {
        this.guideButton = guideButton;
    }
    
    /**
     * Apply deadzone to analog stick values to avoid drift
     * @param value The raw analog value
     * @return Adjusted value with deadzone applied
     */
    private double applyDeadzone(double value) {
        final double DEADZONE = 0.15;
        if (Math.abs(value) < DEADZONE) {
            return 0.0;
        }
        return value;
    }
    
    @Override
    public String toString() {
        return String.format("XboxButton[LS(%.2f,%.2f) RS(%.2f,%.2f) LT:%.2f RT:%.2f A:%b B:%b X:%b Y:%b]",
                leftStickX, leftStickY, rightStickX, rightStickY, 
                leftTrigger, rightTrigger, buttonA, buttonB, buttonX, buttonY);
    }
}
