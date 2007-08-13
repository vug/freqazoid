/*
 * ResourceManager.java
 *
 * Created on March 25, 2007, 4:24 AM
 */

package gui;


import java.awt.BorderLayout;
import javax.swing.JFrame;

import realtimesound.AudioEngine;
public class ResourceManager {
    
    private AudioEngine audioEngine;
    private Thread audioThread;
    private Window window;
    private Settings settings;
    //private Oscilloscope canvas;
    private Spectroscope canvas;
    //private Spectrogram canvas;
	private Menu menuBar;
	private StatusBar statusBar;
    
    /** Creates a new instance of ResourceManager */
    public ResourceManager() {
    	window = new Window(this);
    	
    	canvas = new Spectroscope(this);
//    	System.out.println("canvas double buffered: "+ canvas.isDoubleBuffered());
    	//canvas = new Spectrogram(this);
    	window.add(canvas, BorderLayout.CENTER);
    	
//        System.out.println("Opening audio system...");
        audioEngine = new AudioEngine(this);
//        System.out.println("Audio system opened.");
        audioThread = new Thread(audioEngine);        
//        System.out.println("starting the audio engine...");        
        audioThread.start();
        
        
		menuBar = new Menu(this);
        window.setJMenuBar(menuBar);
        statusBar = new StatusBar(this);
        window.add(statusBar, BorderLayout.SOUTH);
        
        settings = new Settings(this);
        
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
    
    public Settings getSettings() {
    	return this.settings;
    }
}