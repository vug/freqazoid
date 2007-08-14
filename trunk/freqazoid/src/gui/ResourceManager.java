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
    private Window window;
    private Settings settings;
    private Display display;
	private Menu menuBar;
	private StatusBar statusBar;
    
    public ResourceManager(AudioEngine audioEngine) {
    	this.audioEngine = audioEngine;
    	
    	window = new Window(this);
    	display = new Display(this);    	
    	window.add(display, BorderLayout.CENTER);
        
		menuBar = new Menu(this);
        window.setJMenuBar(menuBar);
        statusBar = new StatusBar(this);
        window.add(statusBar, BorderLayout.SOUTH);
        
        settings = new Settings(this);
        
        window.setVisible(true);
        
        display.displayThread.start();
    }

    public JFrame getFrame() {
    	return this.window;
    }
    
    public AudioEngine getAudioEngine() {
    	return audioEngine;
    }
    
    public Display getDisplay() {    
        return this.display;
    }
    
    public Settings getSettings() {
    	return this.settings;
    }
}