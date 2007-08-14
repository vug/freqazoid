/*
 * ResourceManager.java
 *
 * Created on March 25, 2007, 4:24 AM
 */

package gui;


import java.awt.BorderLayout;
import javax.swing.JFrame;

import realtimesound.AudioEngine;
public class ResourceManager implements Runnable {
    
    private AudioEngine audioEngine;
    private Thread audioThread;
    private Thread canvasThread;
    private Window window;
    private Settings settings;
    //private Oscilloscope canvas;
    private Display display;
    //private Spectrogram canvas;
	private Menu menuBar;
	private StatusBar statusBar;
    
    public ResourceManager() {
    	window = new Window(this);

    	//canvas = new Spectrogram(this);
    	display = new Display(this);
    	window.add(display, BorderLayout.CENTER);
    	
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
        
        canvasThread = new Thread(this);
        canvasThread.start();
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

	public void run() {
		while (true) {
			display.repaint();
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		
	}
}