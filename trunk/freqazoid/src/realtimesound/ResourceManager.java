/*
 * ResourceManager.java
 *
 * Created on March 25, 2007, 4:24 AM
 */

package realtimesound;

import gui.Oscilloscope;
import gui.Menu;
import gui.Spectroscope;
import gui.StatusBar;
import gui.Window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

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
	private Menu menuBar;
	private StatusBar statusBar;
    
    /** Creates a new instance of ResourceManager */
    public ResourceManager() {
    	window = new Window(this);
    	
    	canvas = new Spectroscope(this);
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
    
    public Spectroscope getCanvas() {
        return this.canvas;
    }
    
}