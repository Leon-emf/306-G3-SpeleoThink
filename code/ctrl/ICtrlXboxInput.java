package ctrl;

import bean.XboxButton;

/**
 * Interface for receiving Xbox controller input events
 * 
 * @author SpeleoThink Team
 */
public interface ICtrlXboxInput {
    
    /**
     * Called when Xbox controller state is updated
     * @param xboxButton Current state of the Xbox controller
     */
    void onXboxInputReceived(XboxButton xboxButton);
    
    /**
     * Called when button A is pressed
     */
    void onButtonAPressed();
    
    /**
     * Called when button B is pressed
     */
    void onButtonBPressed();
    
    /**
     * Called when button X is pressed
     */
    void onButtonXPressed();
    
    /**
     * Called when button Y is pressed
     */
    void onButtonYPressed();
    
    /**
     * Called when Start button is pressed (toggle recording)
     */
    void onStartPressed();
    
    /**
     * Called when Guide/Xbox button is pressed (ID 10 - toggle connection)
     */
    void onGuidePressed();
}
