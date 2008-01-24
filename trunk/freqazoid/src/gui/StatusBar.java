package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 4971656319853172047L;
	private JLabel labelWidthDuration;
	private JLabel labelHopSize;
	private JButton buttonStart;
	private JButton buttonStop;
	private JButton buttonFreeze;
	private JButton buttonClear;
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
		buttonStart = new JButton("Start");		
		buttonStart.setPreferredSize(new Dimension(65,10));
		buttonStart.addActionListener(this);
		this.add(buttonStart);
		
		buttonStop = new JButton("Stop");		
		buttonStop.setPreferredSize(new Dimension(60,10));
		buttonStop.addActionListener(this);
		this.add(buttonStop);
		
		buttonFreeze = new JButton("Freeze");
		buttonFreeze.setPreferredSize(new Dimension(80,10));
		buttonFreeze.addActionListener(this);
		this.add(buttonFreeze);
		
		buttonClear = new JButton("Clear");
		buttonClear.setPreferredSize(new Dimension(60,10));
		buttonClear.addActionListener(this);
		this.add(buttonClear);
		
		this.add(Box.createHorizontalGlue());
		this.add(labelHopSize);
		this.add(labelWidthDuration);
	}
	public void actionPerformed(ActionEvent ae) {
		JButton source = (JButton) ae.getSource();
		if(source == buttonStart) {
			rm.getAudioEngine().reopenFile();
			rm.getAudioEngine().muteFile = false;
		} else if(source == buttonStop) {
			rm.getAudioEngine().muteFile = true;			
		} else if(source == buttonFreeze) {
			rm.getAudioEngine().pauseEngine();
		} else if(source == buttonClear) {
			rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().reset();
		}
	}

}