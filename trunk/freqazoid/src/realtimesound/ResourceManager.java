/*
 * ResourceManager.java
 *
 * Created on March 25, 2007, 4:24 AM
 */

package realtimesound;

import gui.Menu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

/**
 *
 * @author HAL
 */
public class ResourceManager implements ActionListener {
    
    private AudioEngine audioEngine;
    private Thread audioThread;
    private JFrame window = new JFrame("Audio Engine");
    private JButton buttonPause = new JButton("pause");
    private JButton buttonStop = new JButton("stop");
    private Canvas canvas;
    private Menu menuBar;
    
    /** Creates a new instance of ResourceManager */
    public ResourceManager() {
        System.out.println("Opening audio system...");
        audioEngine = new AudioEngine(this);
        System.out.println("Audio system opened.");
        audioThread = new Thread(audioEngine);        
        System.out.println("starting the audio engine...");        
        audioThread.start();
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(500,200);
        window.setLocation(100,100);
        window.setLayout(new BorderLayout());        
        
        buttonPause.addActionListener(this);
        window.add(buttonPause, BorderLayout.PAGE_START);
        
        buttonStop.addActionListener(this);        
        window.add(buttonStop, BorderLayout.PAGE_END);
        
        canvas = new Canvas();
        window.add(canvas, BorderLayout.CENTER);
        
        menuBar = new Menu(this);
        window.setJMenuBar(menuBar);
        window.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==buttonPause) {
            audioEngine.pauseEngine();
        }
        if(e.getSource()==buttonStop) {
            System.out.println("stopping");
            audioEngine.stopEngine();
        }
    }
    
    public JFrame getFrame() {
    	return this.window;
    }
    
    public AudioEngine getAudioEngine() {
    	return audioEngine;
    }
    
    public Canvas getCanvas() {
        return this.canvas;
    }
    
}
