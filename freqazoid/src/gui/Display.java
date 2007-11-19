package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import math.Peak;
import math.Tools;

public class Display extends JPanel implements Runnable, ComponentListener {
	
	private static final long serialVersionUID = -5114358819176793973L;
	private ResourceManager rm;
	
	private int mode;
	public static final int OSCILLOSCOPE = 0;
	public static final int SPECTROSCOPE = 1;
	public static final int FREQUENCY_TRACKER = 2;
	
	private int refreshRate;
	
	private boolean showPeaks;
	private boolean antialiased;
	
	private BufferedImage backgroundFrequencyTracker;
	private BufferedImage backgroundSpectroscope;
	
	protected Thread displayThread;
		
	public Display(ResourceManager rm) {
		super();
		this.rm = rm;
		mode = SPECTROSCOPE;
		showPeaks = false;
		this.setBackground(ColorsAndStrokes.BACKGROUND);
		displayThread = new Thread(this);
		refreshRate = 25; // milliseconds
		antialiased = true;
		this.addComponentListener(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (antialiased == true) {
        	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
        
        int width = this.getWidth();
        int height = this.getHeight();
        
        double y0, l;
        
        switch (mode) {
		case SPECTROSCOPE:
			if( backgroundSpectroscope == null ) {
				createBackgroundImages();
			}
			double[] magnitude = rm.getAudioEngine().getAudioAnalyser().getMagnitudeSpectrum();			
			y0 = height-1;
			l=(double)width/(magnitude.length/2);
			
			g2.setColor( ColorsAndStrokes.GREEN );
			
//			for(int i=0; i<magnitude.length/2-1; i++) {		
//	            double sample1 = 60*Math.log10(magnitude[i]+1);
//	            double sample2 = 60*Math.log10(magnitude[i+1]+1);
//	            
//	            Line2D.Double line = new Line2D.Double(l*i, y0-sample1, l*(i+1), y0-sample2);
//	            g2.draw(line);
//			}
			for (int i = 0; i < width; i++) {
				int index = i*magnitude.length/width/2;
				double sample1 = 60*Math.log10(magnitude[index]+1);
				
				Line2D.Double line = new Line2D.Double(i, y0, i, y0-sample1);	            
	            g2.draw(line);
			}
				            
            if(showPeaks == true) {
            	g2.setColor( ColorsAndStrokes.RED );
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
            	double threshold = 60*Math.log10(rm.getAudioEngine().getAudioAnalyser().getPeakThreshold()+1);
            	Line2D.Double line2 = new Line2D.Double(0,getHeight()-threshold,getWidth(),getHeight()-threshold);
            	g2.draw(line2);
            }
			
			break;
		case OSCILLOSCOPE:
			double[] amplitude = rm.getAudioEngine().getAudioAnalyser().getCurrentFrame();
			y0 = this.getHeight()/2;
//			l=(double)this.getWidth()/amplitude.length;
//	        for(int i=0; i<amplitude.length-1; i++) {           
//	            Line2D.Double line = new Line2D.Double(l*i, y0*amplitude[i]+y0, l*(i+1), y0*amplitude[i+1]+y0);
//	            g2.setColor( ColorsAndStrokes.GREEN );
//	            g2.draw(line);
//	        }     
			g2.setColor( ColorsAndStrokes.GREEN );
	        for (int i = 0; i < width-1; i++) {
				int index = i*amplitude.length/width;
				int index2 = (i+1)*amplitude.length/width;
//				double sample1 = 60*Math.log10(magnitude[index]+1);
				
				Line2D.Double line = new Line2D.Double(i, y0-y0*amplitude[index], i+1, y0-y0*amplitude[index2]);
//				Line2D.Double line = new Line2D.Double(i, y0, i, y0-sample1);	            
	            g2.draw(line);
			}
	        
			break;
		case FREQUENCY_TRACKER:
			if(backgroundFrequencyTracker == null) {
				createBackgroundImages();
			}
			
			y0 = this.getHeight();
			double[] freqs = rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().getRecord();
			l=(double)width/freqs.length;
			
			double f0Min = rm.getAudioEngine().getAudioAnalyser().getF0Min();
			double f0Max = rm.getAudioEngine().getAudioAnalyser().getF0Max();
			
			int nSemiTones = (int)Math.floor(12*Tools.LOG_OF_2_BASE_10*Math.log10(f0Max/f0Min));
			double ly = y0/nSemiTones;
			
			g2.drawImage(backgroundFrequencyTracker, 0, 0, null);
			
			g2.setStroke( ColorsAndStrokes.NORMAL );
			for(int i=0; i<freqs.length-1; i++) {
				if(freqs[i] > f0Min && freqs[i+1] > f0Min)
				{
					double pitch1 = 12*Tools.LOG_OF_2_BASE_10*Math.log10(freqs[i]/f0Min);
					double pitch2 = 12*Tools.LOG_OF_2_BASE_10*Math.log10(freqs[i+1]/f0Min);
//					Line2D.Double line = new Line2D.Double(l*i, y0-y0*freqs[i]/1000, l*(i+1), y0-y0*freqs[i+1]/1000);
					Line2D.Double line = new Line2D.Double(l*i, y0-pitch1*ly, l*(i+1), y0-pitch2*ly);
					g2.setColor( ColorsAndStrokes.GREEN );
					g2.draw(line);
				}
	        }
			break;
		}
        
		g2.dispose();
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
	
	private void createBackgroundImages() {
		int width = this.getWidth();
		int height = this.getHeight();
		Graphics2D g2;
		
		backgroundFrequencyTracker = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2 = backgroundFrequencyTracker.createGraphics();
		
		g2.setColor( ColorsAndStrokes.BACKGROUND );
		g2.fillRect(0, 0, width, height);
		
		double y0 = height;
		double f0Min = rm.getAudioEngine().getAudioAnalyser().getF0Min();
		double f0Max = rm.getAudioEngine().getAudioAnalyser().getF0Max();
		double log102 = 1/Math.log10(2);
		
		g2.setColor( ColorsAndStrokes.GRAY );
		Line2D.Double axis = new Line2D.Double(5.0, 0.0, 5.0, height);			
		g2.draw(axis);
		
		int nSemiTones = (int)Math.floor(12*log102*Math.log10(f0Max/f0Min));
		double ly = y0/nSemiTones;
		for(int i=0; i<nSemiTones; i++) {
//			double f = f0Min*Math.pow(2, i/12.0);
//			double pitch = 12*log102*Math.log10(f/f0Min);
//			Line2D.Double line = new Line2D.Double(0, y0-y0*pitch/60, getWidth(), y0-y0*pitch/60);
			Line2D.Double line = new Line2D.Double(0, y0-i*ly, width, y0-i*ly);
			g2.setStroke( ColorsAndStrokes.DASHED );
			g2.draw(line);
		}
		
//		double time=(double)freqs.length*rm.getAudioEngine().getAudioAnalyser().getWindowSize()/		
//		rm.getAudioEngine().getAudioAnalyser().getNumberOfHops()/44100;
//		g2.drawString(String.valueOf(time).substring(0, 5)+"sec", getWidth()-100, getHeight()-5);
		g2.drawString(String.valueOf(f0Min)+" Hz", 10, getHeight());
		g2.drawString(String.valueOf(f0Max)+" Hz", 10, 10);
		
		backgroundSpectroscope = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2 = backgroundSpectroscope.createGraphics();
		
		g2.setColor( ColorsAndStrokes.BACKGROUND );
		g2.fillRect(0, 0, width, height);
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

	public boolean isAntialiased() {
		return antialiased;
	}

	public void setAntialiased(boolean antialiased) {
		this.antialiased = antialiased;
	}

	public void componentHidden(ComponentEvent arg0) {
		
	}

	public void componentMoved(ComponentEvent arg0) {
		
	}

	public void componentResized(ComponentEvent arg0) {
		backgroundFrequencyTracker = null;
	}

	public void componentShown(ComponentEvent arg0) {
		
	}
}
