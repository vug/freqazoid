/*
 * ResourceManager.java
 *
 * Created on March 25, 2007, 4:24 AM
 */

package gui;


import java.awt.BorderLayout;
import javax.swing.JFrame;

import realtimesound.AudioEngine;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 *
 * @author HAL
 */
public class ResourceManager {
    
    private AudioEngine audioEngine;
    private Thread audioThread;
    private Window window;
    //private Oscilloscope canvas;
    private Spectroscope canvas;
    //private Spectrogram canvas;
	private Menu menuBar;
	private StatusBar statusBar;
    
    /** Creates a new instance of ResourceManager */
    public ResourceManager() {
    	window = new Window(this);
    	
    	canvas = new Spectroscope(this);
    	//canvas = new Spectrogram(this);
    	window.add(canvas, BorderLayout.CENTER);
    	
        System.out.println("Opening audio system...");
        audioEngine = new AudioEngine(this);
        System.out.println("Audio system opened.");
        audioThread = new Thread(audioEngine);        
        System.out.println("starting the audio engine...");        
        audioThread.start();
        
        
		menuBar = new Menu(this);
        window.setJMenuBar(menuBar);
        statusBar = new StatusBar(this);
        window.add(statusBar, BorderLayout.SOUTH);
        
        window.setVisible(true);
    }

    public JFrame getFrame() {
    	return this.window;
    }
    
    public AudioEngine getAudioEngine() {
    	return audioEngine;
    }
    
    //public Spectrogram getCanvas() {
    public Spectroscope getCanvas() {    
        return this.canvas;
    }
    
}