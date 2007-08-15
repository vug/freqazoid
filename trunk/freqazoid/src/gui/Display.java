package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import math.Peak;

public class Display extends JPanel implements Runnable {
	
	private int mode;
	public static final int OSCILLOSCOPE = 0;
	public static final int SPECTROSCOPE = 1;
	public static final int FREQUENCY_TRACKER = 2;
	private boolean showPeaks;
	private ResourceManager rm;
	protected Thread displayThread;
	private int refreshRate;
	
	public Display(ResourceManager rm) {
		super();
		this.rm = rm;
		mode = SPECTROSCOPE;
		showPeaks = false;
		this.setBackground(ColorsAndStrokes.BACKGROUND);
		displayThread = new Thread(this);
		refreshRate = 20;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;        
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double y0, l;
        
        switch (mode) {
		case SPECTROSCOPE:
			double[] magnitude = rm.getAudioEngine().getAudioAnalyser().getMagnitudeSpectrum();			
			y0 = this.getHeight()-1;
			l=(double)this.getWidth()/(magnitude.length/2);
			
			for(int i=0; i<magnitude.length/2-1; i++) {		
	            double sample1 = 60*Math.log10(magnitude[i]+1);
	            double sample2 = 60*Math.log10(magnitude[i+1]+1);
	            
	            Line2D.Double line = new Line2D.Double(l*i, y0-sample1, l*(i+1), y0-sample2);
	            g2.setColor( ColorsAndStrokes.GREEN );
	            g2.draw(line);
			}
			
			g2.setColor( ColorsAndStrokes.RED );	            
            if(showPeaks == true) {
            	Peak[] peaks = rm.getAudioEngine().getAudioAnalyser().getPeaks();
            	for(int n=0; n<peaks.length; n++) {
            		Line2D.Double line1 = new Line2D.Double(peaks[n].frequency*l*magnitude.length/44100,0,
            												peaks[n].frequency*l*magnitude.length/44100,y0);         		
            		g2.draw(line1);
            	}
//            	g2.setColor( Colors.BLUE );
//            	double f = rm.getAudioEngine().getAudioAnalyser().getFundamentalFrequency();
//            	Line2D.Double line1 = new Line2D.Double(f*l,0,f*l,y0);
//            	g2.draw(line1);
            	g2.setColor( ColorsAndStrokes.YELLOW );
            	double threshold = 60*Math.log1p(rm.getAudioEngine().getAudioAnalyser().getPeakThreshold());
            	Line2D.Double line2 = new Line2D.Double(0,threshold,getWidth(),threshold);
            	g2.draw(line2);
            }
			
			break;
		case OSCILLOSCOPE:
			double[] amplitude = rm.getAudioEngine().getAudioAnalyser().getCurrentFrame();
			y0 = this.getHeight()/2;
			l=(double)this.getWidth()/amplitude.length;
	        for(int i=0; i<amplitude.length-1; i++) {           
	            Line2D.Double line = new Line2D.Double(l*i, y0*amplitude[i]+y0, l*(i+1), y0*amplitude[i+1]+y0);
	            g2.setColor( ColorsAndStrokes.GREEN );
	            g2.draw(line);
	        }
			break;
		case FREQUENCY_TRACKER:
			y0 = this.getHeight();
			double[] freqs = rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().getRecord();
			l=(double)this.getWidth()/freqs.length;
			
			g2.setColor( ColorsAndStrokes.GRAY );
			Line2D.Double axis = new Line2D.Double(5.0, 0.0, 5.0, getHeight());			
			g2.draw(axis);		
			double fmin=110.0;
			double log102 = 1/Math.log10(2);
			for(int i=0; i<60; i+=2) {
				double f = fmin*Math.pow(2, i/12.0);
				double pitch = 12*log102*Math.log10(f/fmin);
				Line2D.Double line = new Line2D.Double(0, y0-y0*pitch/60, getWidth(), y0-y0*pitch/60);
				g2.setStroke( ColorsAndStrokes.DASHED );
				g2.draw(line);
			}
			
			g2.setStroke( ColorsAndStrokes.NORMAL );
			for(int i=0; i<freqs.length-1; i++) {
				if(freqs[i] > fmin && freqs[i+1] > fmin)
				{
					double pitch1 = 12*log102*Math.log10(freqs[i]/fmin);
					double pitch2 = 12*log102*Math.log10(freqs[i+1]/fmin);
//					Line2D.Double line = new Line2D.Double(l*i, y0-y0*freqs[i]/1000, l*(i+1), y0-y0*freqs[i+1]/1000);
					Line2D.Double line = new Line2D.Double(l*i, y0-y0*pitch1/60, l*(i+1), y0-y0*pitch2/60);
					g2.setColor( ColorsAndStrokes.GREEN );
					g2.draw(line);
				}
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
	
	public int getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}
	
	public void setShowPeaks(boolean b) {
		showPeaks = b;
	}

	public void run() {
		while (true) {
			this.repaint();
			
			try {
				Thread.sleep(refreshRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		
	}
}
