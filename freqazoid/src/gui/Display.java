package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class Display extends JPanel {
	
	private int mode;
	public static final int OSCILLOSCOPE = 0;
	public static final int SPECTROSCOPE = 1;
	private boolean showPeaks;
	private ResourceManager rm;
	
	public Display(ResourceManager rm) {
		super();
		this.rm = rm;
		mode = SPECTROSCOPE;
		showPeaks = false;
		this.setBackground(new Color(100,100,100));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double y0, l;
        
        switch (mode) {
		case SPECTROSCOPE:
			double[] magnitude = rm.getAudioEngine().getAudioAnalyser().getMagnitudeSpectrum();			
			y0 = this.getHeight()-1;
			for(int i=0; i<magnitude.length/2-1; i++) {				
	            l=(double)this.getWidth()/(magnitude.length/2);
	            
	            double sample1 = 60*Math.log10(magnitude[i]+1);
	            double sample2 = 60*Math.log10(magnitude[i+1]+1);
	            
	            Line2D.Double line = new Line2D.Double(l*i, y0-sample1, l*(i+1), y0-sample2);
	            
	            g2.setColor(new Color(255,0,0));
	            
//	            if(peaks!=null) {
//	            	for(int n=0; n<peaks.size(); n++) {
//	            		Line2D.Double line1 = new Line2D.Double(peaks.elementAt(n).frequency*l,0,peaks.elementAt(n).frequency*l,y0);         		
//	            		//g2.draw(line1);
//	            	}
//	            }
	            
	            g2.setColor(new Color(0,255,0));
	            g2.draw(line);
			}
			
			break;
		case OSCILLOSCOPE:
			double[] amplitude = rm.getAudioEngine().getAudioAnalyser().getCurrentFrame();
			y0 = this.getHeight()/2;
	        for(int i=0; i<amplitude.length-1; i++) {	            
	            l=(double)this.getWidth()/amplitude.length;
	            Line2D.Double line = new Line2D.Double(l*i, y0*amplitude[i]+y0, l*(i+1), y0*amplitude[i+1]+y0);
	            g2.setColor(new Color(0,255,0));
	            g2.draw(line);
	        }
			break;
		}
        
		g2.dispose();
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
	
}
