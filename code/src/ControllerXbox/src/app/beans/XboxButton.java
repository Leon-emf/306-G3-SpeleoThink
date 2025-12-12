/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.beans;

/**
 *
 * @author AzevedoZaralA
 */
public class XboxButton {

    private String nom;
    private boolean pressed;

    public XboxButton(String nom, boolean pressed) {
        this.nom = nom;
        this.pressed = pressed;
    }

    public String getNom() {
        return nom;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
