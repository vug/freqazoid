package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
	private JButton buttonSave;
	private JButton buttonDeglitch;
	private ResourceManager rm;
	
	public StatusBar(ResourceManager rm) {
		super();
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.rm = rm;		
		labelWidthDuration = new JLabel("...");
		labelHopSize = new JLabel("...");
		
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
		buttonClear.setPreferredSize(new Dimension(70,10));
		buttonClear.addActionListener(this);
		this.add(buttonClear);
		
		buttonDeglitch = new JButton("Deglitch");
		buttonDeglitch.setPreferredSize(new Dimension(75,10));
		buttonDeglitch.addActionListener(this);
		this.add(buttonDeglitch);
		
		buttonSave = new JButton("Save");
		buttonSave.setPreferredSize(new Dimension(70,10));
		buttonSave.addActionListener(this);
		this.add(buttonSave);
		
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
		} else if(source == buttonDeglitch) {
//			System.out.println("deglitch");
		} else if(source == buttonSave) {
			File imageFile = new File("record.png");
			try {
				ImageIO.write(rm.getDisplay().getFrequencyTrackerPlot(800,600), "png", imageFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().recordToAFile("record.csv");
		}
	}

}
