package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;


public class Menu extends JMenuBar implements ActionListener, ItemListener {
	
	private static final long serialVersionUID = 1L;

	private ResourceManager rm;
	
	private JMenu menuFile;
	private JMenuItem itemOpenSoundFile;
	private JMenuItem itemQuit;
	
	private JMenu menuView;
	private ButtonGroup groupView;
	private JRadioButtonMenuItem itemOscilloscope;
	private JRadioButtonMenuItem itemSpectroscope;
	private JRadioButtonMenuItem itemFrequencyTracker;
	private JCheckBoxMenuItem itemShowPeaks;
	private JRadioButtonMenuItem itemTWMErrors;
		
	private JMenu menuOptions;
	private JMenuItem itemSelectAudioDevices;
	private JCheckBoxMenuItem itemMuteMicrophone;
	private JCheckBoxMenuItem itemMuteSpeaker;
	private JCheckBoxMenuItem itemPause;
	private JMenuItem itemStartStop;
	private boolean running = true;
	private JMenuItem itemSettings;
		
	private JMenu menuHelp;
	private JMenuItem itemAbout;
	
	public Menu(ResourceManager rm) {
		this.rm = rm;
		
		menuFile = new JMenu("File");
		itemOpenSoundFile = new JMenuItem("Open a sound file", KeyEvent.VK_O);
		itemOpenSoundFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
		itemOpenSoundFile.addActionListener(this);
		menuFile.add(itemOpenSoundFile);
		itemQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
		itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
		itemQuit.addActionListener(this);
		menuFile.add(itemQuit);
		
		menuView = new JMenu("View");
		groupView = new ButtonGroup();
		itemOscilloscope = new JRadioButtonMenuItem("Oscilloscope");
		itemOscilloscope.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,ActionEvent.CTRL_MASK));
		itemOscilloscope.addActionListener(this);
		groupView.add(itemOscilloscope);
		menuView.add(itemOscilloscope);
		itemSpectroscope = new JRadioButtonMenuItem("Spectroscope");
		itemSpectroscope.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,ActionEvent.CTRL_MASK));
		itemSpectroscope.addActionListener(this);
		groupView.add(itemSpectroscope);
		menuView.add(itemSpectroscope);
		itemShowPeaks = new JCheckBoxMenuItem("Spectroscope with Peaks");
		itemShowPeaks.setSelected(rm.getDisplay().getShowPeaks());
		itemShowPeaks.addItemListener(this);
		menuView.add(itemShowPeaks);
		itemFrequencyTracker = new JRadioButtonMenuItem("Frequency Tracker");
		itemFrequencyTracker.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
		itemFrequencyTracker.addActionListener(this);
		groupView.add(itemFrequencyTracker);
		menuView.add(itemFrequencyTracker);
		itemTWMErrors = new JRadioButtonMenuItem("TWM Errors");
		itemTWMErrors.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
		itemTWMErrors.addActionListener(this);
		groupView.add(itemTWMErrors);
		menuView.add(itemTWMErrors);
		
		menuOptions = new JMenu("Options");
		itemSelectAudioDevices = new JMenuItem("Select Audio Devices");
		itemSelectAudioDevices.addActionListener(this);
		menuOptions.add(itemSelectAudioDevices);
		menuOptions.addSeparator();
		itemMuteMicrophone = new JCheckBoxMenuItem("Mute Mic");
		itemMuteMicrophone.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,ActionEvent.CTRL_MASK));
//		itemMuteMicrophone.setSelected(rm.getAudioEngine().get)
		itemMuteMicrophone.addItemListener(this);
		menuOptions.add(itemMuteMicrophone);
		itemMuteSpeaker = new JCheckBoxMenuItem("Mute Speaker");
		itemMuteSpeaker.addItemListener(this);
		menuOptions.add(itemMuteSpeaker);
		menuOptions.addSeparator();
		itemPause = new JCheckBoxMenuItem("Pause");
		itemPause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		itemPause.addItemListener(this);
		menuOptions.add(itemPause);
		itemStartStop = new JMenuItem("Stop Engine");
		itemStartStop.addActionListener(this);
		menuOptions.add(itemStartStop);
		menuOptions.addSeparator();
		itemSettings = new JMenuItem("Settings");
		itemSettings.addActionListener(this);
		menuOptions.add(itemSettings);
		
		
		menuHelp = new JMenu("Help");
		itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(this);
		menuHelp.add(itemAbout);
		
		this.add(menuFile);
		this.add(menuView);
		this.add(menuOptions);
		this.add(menuHelp);
	}

	public void actionPerformed(ActionEvent ae) {
		if( ae.getSource() == itemAbout ) {
			JOptionPane.showMessageDialog(rm.getWindow(), 
					"Real-time implementation of Beauchamp's two-way mismatch algorithm\n" +
					"(test version)\n"+
					"written by Ugur Guney", "About", JOptionPane.INFORMATION_MESSAGE);
		}		
		else if(ae.getSource() == itemQuit ) {
			rm.exitProgram();
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
		else if(ae.getSource() == itemOpenSoundFile) {
			JFileChooser fileChooser = new JFileChooser();
			int returnValue = fileChooser.showOpenDialog(rm.getWindow());
			if( returnValue == JFileChooser.APPROVE_OPTION ) {
				rm.getAudioEngine().openFile( fileChooser.getSelectedFile() );
			}
		}
		else if(ae.getSource() == itemSelectAudioDevices) {
			rm.getAudioEngine().selectInputAndOutputLine();
		}
		else if(ae.getSource() == itemSettings) {
			rm.getSettings().setVisible(true);
		}
		else if(ae.getSource() == itemOscilloscope) {
			rm.getDisplay().setMode(Display.OSCILLOSCOPE);
		}
		else if(ae.getSource() == itemSpectroscope) {
			rm.getDisplay().setMode(Display.SPECTROSCOPE);
		}
		else if(ae.getSource() == itemFrequencyTracker) {
			rm.getDisplay().setMode(Display.FREQUENCY_TRACKER);
		}
		else if(ae.getSource() == itemTWMErrors) {
			rm.getDisplay().setMode(Display.TWM_ERROR);
		}
	}

	public void itemStateChanged(ItemEvent ie) {
		if( ie.getSource() == itemPause ) {
			//System.out.println(itemPause.getState());
			rm.getAudioEngine().pauseEngine();
		}
		else if(ie.getSource() == itemMuteMicrophone ) {
			rm.getAudioEngine().setMuteMicrophone( itemMuteMicrophone.getState() );
		}
		else if(ie.getSource() == itemMuteSpeaker ) {
			rm.getAudioEngine().setMuteSpeaker( itemMuteSpeaker.getState() );
		}
		else if(ie.getSource() == itemShowPeaks ) {
			rm.getDisplay().setShowPeaks( itemShowPeaks.getState() );
		}
	}
}
