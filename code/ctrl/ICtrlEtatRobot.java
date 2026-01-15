/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

/**
 *
 * @author AudergonV01
 */
public interface ICtrlEtatRobot {
    void onBatteryReceived(int battery);
    void onImageReceived(byte[] image);
    void onAudioReceived(byte[] audio);
    void onConnectionStateReceived(boolean state);
}
