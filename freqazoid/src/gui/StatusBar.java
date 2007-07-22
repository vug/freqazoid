package gui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import realtimesound.ResourceManager;

@SuppressWarnings("serial")
public class StatusBar extends JPanel {
	
	private JLabel labelWidthDuration;
	//private ResourceManager rm;
	
	public StatusBar(ResourceManager rm) {
		super(new FlowLayout(FlowLayout.RIGHT));
		
		//this.rm = rm;
		String duration = Double.toString((double)rm.getCanvas().getNPoints()/44.1).substring(0,3);
		labelWidthDuration = new JLabel(duration+" msec");	
		
		//labelWidthDuration.setPreferredSize(new Dimension(80,10));
		labelWidthDuration.setBackground(Color.MAGENTA);
		//this.setMaximumSize(new Dimension(1000,20));
		//this.setPreferredSize(new Dimension(10,20));

		this.add(labelWidthDuration);
	}

}
