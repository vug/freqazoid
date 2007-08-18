/*
 * Main.java
 *
 * Created on March 24, 2007, 2:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package realtimesound;

import gui.ResourceManager;

/**
 *
 * @author HAL
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	/*
    	 * Apply deglitching
    	 */
    	AudioEngine audioEngine = new AudioEngine();
        new ResourceManager(audioEngine);
    }
    
}