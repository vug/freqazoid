package realtimesound;

import gui.ResourceManager;

/**
 *
 * @author ugur guney
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