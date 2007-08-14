package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StatusBar extends JPanel implements ActionListener {
	
	private JLabel labelWidthDuration;
	private JLabel labelHopSize;
	private JButton buttonPlayPause;
	private JButton buttonStop;
	private ResourceManager rm;
	
	public StatusBar(ResourceManager rm) {
		super();
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.rm = rm;
		int windowSize = rm.getAudioEngine().getAudioAnalyser().getWindowSize(); 
		String duration = Double.toString((double)windowSize/44.1).substring(0,3);
		labelWidthDuration = new JLabel(duration+" msec");
		
		duration = Double.toString(((double)windowSize/rm.getAudioEngine().getAudioAnalyser().getNumberOfHops()/44.1)).substring(0, 3);
		labelHopSize = new JLabel(duration+" msec ");
		
		//labelWidthDuration.setPreferredSize(new Dimension(80,10));
//		labelWidthDuration.setBackground(Color.MAGENTA);
		//this.setMaximumSize(new Dimension(1000,20));
		//this.setPreferredSize(new Dimension(10,20));
		//labelWidthDuration.setAlignmentX(Component.);
		buttonPlayPause = new JButton("Play");		
		buttonPlayPause.setPreferredSize(new Dimension(60,10));
		buttonPlayPause.addActionListener(this);
		this.add(buttonPlayPause);
		
		buttonStop = new JButton("Stop");		
		buttonStop.setPreferredSize(new Dimension(60,10));
		buttonStop.addActionListener(this);
		this.add(buttonStop);
		
		this.add(Box.createHorizontalGlue());
		this.add(labelHopSize);
		this.add(labelWidthDuration);
	}
	public void actionPerformed(ActionEvent ae) {
		JButton source = (JButton) ae.getSource();
		if(source == buttonPlayPause) {
			rm.getAudioEngine().reopenFile();
			rm.getAudioEngine().muteFile = false;
		} else if(source == buttonStop) {
			rm.getAudioEngine().muteFile = true;
			
		}
	}

}
