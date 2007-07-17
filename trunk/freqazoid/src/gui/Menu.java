package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import realtimesound.ResourceManager;

public class Menu extends JMenuBar implements ActionListener, ItemListener {
	
	private ResourceManager rm;
	
	private JMenu menuFile;
	private JMenuItem itemExit;
		
	private JMenu menuOptions;
	private JMenu subMenuAudioDriver;
	private JCheckBoxMenuItem itemPause;
	private JMenuItem itemStartStop;
	private boolean running = true;
		
	private JMenu menuHelp;
	private JMenuItem itemAbout;
	
	public Menu(ResourceManager rm) {
		this.rm = rm;
		
		menuFile = new JMenu("File");
		itemExit = new JMenuItem("Quit", KeyEvent.VK_Q);
		itemExit.addActionListener(this);
		menuFile.add(itemExit);
		
		menuOptions = new JMenu("Options");
		subMenuAudioDriver = new JMenu("Audio Driver");
		menuOptions.add(subMenuAudioDriver);
		String[] inputInfos = rm.getAudioEngine().getInputInfos();
		for(int i=0; i<inputInfos.length; i++) {
			if(inputInfos[i]!=null) {
				JMenuItem itemDriver = new JMenuItem(inputInfos[i]);
				subMenuAudioDriver.add(itemDriver);
			}
		}
		subMenuAudioDriver.addSeparator();
		String[] outputInfos = rm.getAudioEngine().getOutputInfos();
		for(int i=0; i<outputInfos.length; i++) {
			if(outputInfos[i]!=null) {
				JMenuItem itemDriver = new JMenuItem(outputInfos[i]);
				subMenuAudioDriver.add(itemDriver);
			}
		}
		menuOptions.addSeparator();
		itemPause = new JCheckBoxMenuItem("Pause");
		itemPause.addItemListener(this);
		menuOptions.add(itemPause);
		itemStartStop = new JMenuItem("Stop Engine");
		itemStartStop.addActionListener(this);
		menuOptions.add(itemStartStop);
		
		
		menuHelp = new JMenu("Help");
		itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(this);
		menuHelp.add(itemAbout);
		
		this.add(menuFile);
		this.add(menuOptions);
		this.add(menuHelp);
	}

	public void actionPerformed(ActionEvent ae) {
		if( ae.getSource() == itemAbout ) {
			JOptionPane.showMessageDialog(rm.getFrame(), "Real-time implementation of Beauchamp's two-way mismatch algorithm\n written by Ugur Guney", "About", JOptionPane.INFORMATION_MESSAGE);
		}
		
		else if(ae.getSource() == itemExit ) {
			System.exit(0);
		}
		
		else if(ae.getSource() == itemStartStop ) {
			if(running==true) {
				rm.getAudioEngine().stopEngine();
				itemStartStop.setText("Start Engine");
			}
			else {
				rm.getAudioEngine().startEngine();
				itemStartStop.setText("Stop Engine");
			}
			running = !running;
		}
	}

	public void itemStateChanged(ItemEvent ie) {
		if( ie.getSource() == itemPause ) {
			//System.out.println(itemPause.getState());
			rm.getAudioEngine().pauseEngine();
		}
	}
}
