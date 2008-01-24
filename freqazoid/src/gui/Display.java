package gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import math.Peak;
import math.PeakDetector;
import math.Tools;

public class Display extends JPanel implements Runnable, ComponentListener, MouseInputListener {
	
	private static final long serialVersionUID = -5114358819176793973L;
	private ResourceManager rm;
	
	private int mode;
	public static final int OSCILLOSCOPE = 0;
	public static final int SPECTROSCOPE = 1;
	public static final int FREQUENCY_TRACKER = 2;
	public static final int TWM_ERROR = 3;
	
	private int refreshRate;
	
	private boolean antialiased;
	
	private int width, height;
	
	private Line2D.Double line;
	private double[] amplitude;
	private double oscilloscopeMaxAmplitude;
	private double oscilloscopeOldMaxAmplitude;
	double y0;
	
	private boolean showPeaks;
	private int spectrosopceNSpectralPoints;
	private int spectroscopeOldNSpectralPoints;
	private double spectroscopeMaxMagnitude;
	private double spectroscopeOldMaxMagnitude;
	
	private double f0Min;
	private double f0OldMin;
	private double f0Max;	
	private double f0OldMax;
	private double freqTrackerDuration;
	private double freqTrackerOldDuration;
	
	private BufferedImage imageFrequencyTrackerPlot;	
	private BufferedImage backgroundFrequencyTracker;
	private BufferedImage backgroundSpectroscope;
	
	private Point mousePressedPoint;
	private Point mouseDraggedPoint;
	
	protected Thread displayThread;
		
	public Display(ResourceManager rm) {
		super();
		this.rm = rm;
		mode = SPECTROSCOPE;
		showPeaks = false;
		this.setBackground(ColorsAndStrokes.BACKGROUND);
		displayThread = new Thread(this);
		refreshRate = 50; // milliseconds
		antialiased = true;
		this.addComponentListener(this);
		width = this.getWidth();
		height = this.getHeight();
		line = new Line2D.Double(0,0,10,10);

		spectrosopceNSpectralPoints = rm.getAudioEngine().getAudioAnalyser().getWindowSize()/2;
		spectroscopeMaxMagnitude = 1.0;
		oscilloscopeMaxAmplitude = 0.1;
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (antialiased == true) {
        	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
        
//        int width = this.getWidth();
//        int height = this.getHeight();
        
        switch (mode) {
		case SPECTROSCOPE:
			drawSpectroscope(g2);			
			break;
		case OSCILLOSCOPE:
			drawOscilloscope(g2);	        
			break;
		case FREQUENCY_TRACKER:
			createFrequencyTrackerPlot();
			g2.drawImage(imageFrequencyTrackerPlot, 0, 5, null);
			break;
		case TWM_ERROR:
			drawTWMError(g2);
			break;
		}
        
		g2.dispose();
	}
	
	private void drawTWMError(Graphics2D g2) {
		double f0Min = rm.getAudioEngine().getAudioAnalyser().getF0Min();
		double f0Max = rm.getAudioEngine().getAudioAnalyser().getF0Max();
		double y0Min = 0;
		double xlength = f0Max-f0Min;
		double ylength = 10;
		
		double[] ftrials = rm.getAudioEngine().getAudioAnalyser().getTrialFrequencies1();
		double[] errors = rm.getAudioEngine().getAudioAnalyser().getErrors1();
		
		double width = this.getWidth();
		double height = this.getHeight();
		
		g2.setColor( ColorsAndStrokes.GREEN );
		if(ftrials != null) {
//			for(int i=0; i<ftrials.length; i++) {
//				System.out.print(ftrials[i]+" "+errors[i]+", ");
//			}
//			System.out.println();
			
			for(int i=0; i<ftrials.length-1; i++) {
//				double e1 = Math.exp( -errors[i] );
//				double e2 = Math.exp( -errors[i+1] );
				double e1 = errors[i];
				double e2 = errors[i+1];
				
				line.setLine(width*(ftrials[i]-f0Min)/xlength,
						height-height*(e1-y0Min)/ylength,
						width*(ftrials[i+1]-f0Min)/xlength,
						height-height*(e2-y0Min)/ylength);
				g2.draw(line);
				
				g2.draw(new Ellipse2D.Double(
						width*(ftrials[i]-f0Min)/xlength,
						height-height*(errors[i])/ylength,
						5,
						5));
			}			
		}
	}


	private void createFrequencyTrackerPlot() {
		if(imageFrequencyTrackerPlot==null) {
			imageFrequencyTrackerPlot = new BufferedImage(getWidth(),getHeight()-20,BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g2 = (Graphics2D)imageFrequencyTrackerPlot.getGraphics();
		
		if( backgroundFrequencyTracker == null ) {
			createFrequencyTrackerBackground(imageFrequencyTrackerPlot.getWidth(), imageFrequencyTrackerPlot.getHeight());
		}
		g2.drawImage(backgroundFrequencyTracker, 0, 0, null);
		
		y0 = imageFrequencyTrackerPlot.getHeight();
		double[] freqs = rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().getRecord();
		int headPosition = rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().getHeadPoisition();
		
		double l=(double)imageFrequencyTrackerPlot.getWidth()/freqs.length;
		
		f0Min = rm.getAudioEngine().getAudioAnalyser().getF0Min();
		f0Max = rm.getAudioEngine().getAudioAnalyser().getF0Max();
		
		double ylength = 12*Tools.LOG_OF_2_BASE_10*Math.log10(f0Max/f0Min); 
		
		g2.setStroke( ColorsAndStrokes.NORMAL );
		for(int i=0; i<freqs.length-1; i++) {
			if(freqs[i] > f0Min && freqs[i+1] > f0Min)
			{
				double pitch1 = 12*Tools.LOG_OF_2_BASE_10*Math.log10(freqs[i]/f0Min);
				double pitch2 = 12*Tools.LOG_OF_2_BASE_10*Math.log10(freqs[i+1]/f0Min);
				line.setLine(l*i, y0-y0*pitch1/ylength, l*(i+1), y0-y0*pitch2/ylength);
				g2.setColor( ColorsAndStrokes.GREEN );
				g2.draw(line);
			}
		}
		g2.setColor( ColorsAndStrokes.YELLOW );
		// position indicator
		line.setLine(l*headPosition, 0, l*headPosition, y0);
		g2.draw(line);
	}
	
	private void createFrequencyTrackerBackground(int width, int height) {
		Graphics2D g2;
		
		backgroundFrequencyTracker = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2 = backgroundFrequencyTracker.createGraphics();
		
		g2.setColor( ColorsAndStrokes.BACKGROUND );
		g2.fillRect(0, 0, width, height);
		
		y0 = height;
		double f0Min = rm.getAudioEngine().getAudioAnalyser().getF0Min();
		double f0Max = rm.getAudioEngine().getAudioAnalyser().getF0Max();
		
		int nMin=0, nMax=0;
		double fC0 = 32.7032/2;
		for(int n=0; n<100; n++) {
			if(fC0*Math.pow(2, (double)n/12) <= f0Min)
				nMin = n;			
		}
		for(int n=0; n<100; n++) {
			if(fC0*Math.pow(2, (double)n/12) <= f0Max)
				nMax = n;
		}
		
		String[] pitchNames = new String[nMin+nMax+1];
		for(int n=nMin; n<=nMax; n++) {
			if(n%12==0 || n%12==2 || n%12==4 || n%12==5 || n%12==7 || n%12==9 || n%12==11) {
				pitchNames[n-nMin] = Tools.pitchName(n%12)+(n/12);
			}
		}
		
		double log102 = 1/Math.log10(2);
		
		g2.setColor( ColorsAndStrokes.GRAY );
		
		// x-axis
		line.setLine(0.0, height-1, width, height-1);			
		g2.draw(line);		
		
		g2.setStroke( ColorsAndStrokes.DASHED );
		int nSemiTones = (int)Math.floor(12*log102*Math.log10(f0Max/f0Min));
		double ylength = 12*Tools.LOG_OF_2_BASE_10*Math.log10(f0Max/f0Min);
		for(int i=0; i<nSemiTones; i++) {
			double f = fC0*Math.pow(2, (nMin+i)/12.0);
			double pitch = 12*Tools.LOG_OF_2_BASE_10*Math.log10(f/f0Min);
			g2.setColor( ColorsAndStrokes.GRAY );
			line.setLine(0, y0-y0*pitch/ylength, width, y0-y0*pitch/ylength);			
			g2.draw(line);
			g2.setColor( ColorsAndStrokes.WHITE );
			g2.setFont(new Font("Dialog", Font.PLAIN, 10));
			if(pitchNames[i]!=null) {				
				g2.drawString(pitchNames[i], 0, (int)(y0-y0*pitch/ylength)+5);
			}
		}
		
		freqTrackerDuration = rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().getDuration();		
		for(int i=1; i<=(int)freqTrackerDuration; i++) {
			line.setLine(i*width/freqTrackerDuration, 0, i*width/freqTrackerDuration, height);
			g2.draw(line);
		}
	}
	

	private void drawOscilloscope(Graphics2D g2) {
		double y0;
		amplitude = rm.getAudioEngine().getAudioAnalyser().getCurrentFrame();
		y0 = this.getHeight()/2;
//			l=(double)this.getWidth()/amplitude.length;
//	        for(int i=0; i<amplitude.length-1; i++) {           
//	            Line2D.Double line = new Line2D.Double(l*i, y0*amplitude[i]+y0, l*(i+1), y0*amplitude[i+1]+y0);
//	            g2.setColor( ColorsAndStrokes.GREEN );
//	            g2.draw(line);
//	        }     
		int index, index2;
		g2.setColor( ColorsAndStrokes.GREEN );
		for (int i = 0; i < width-1; i++) {
			index = i*amplitude.length/width;
			index2 = (i+1)*amplitude.length/width;
//				double sample1 = 60*Math.log10(magnitude[index]+1);
			
			line.setLine(i, y0-y0*amplitude[index]/oscilloscopeMaxAmplitude, i+1, y0-y0*amplitude[index2]/oscilloscopeMaxAmplitude);
//				Line2D.Double line = new Line2D.Double(i, y0, i, y0-sample1);	            
		    g2.draw(line);
		}
	}


	private void drawSpectroscope(Graphics2D g2) {
		double l;
		double x0;
		
		if(backgroundSpectroscope == null) {
			createSpectroscopeBackground();
		}
		
		double[] magnitude = rm.getAudioEngine().getAudioAnalyser().getMagnitudeSpectrum();			
		y0 = height-10;
		x0 = 10;
		
		l=(double)(width-2*x0)/spectrosopceNSpectralPoints;
		
		g2.drawImage(backgroundSpectroscope, 0, 0, width, height, null);
		g2.setColor( ColorsAndStrokes.GREEN );
		
		double[] points = new double[spectrosopceNSpectralPoints];
		for (int i = 0; i < spectrosopceNSpectralPoints; i++) {
			points[i] = Tools.lin2dB(magnitude[i])*y0/spectroscopeMaxMagnitude;
		}
		for(int i=0; i<spectrosopceNSpectralPoints-1; i++) {	            
		    line.setLine(x0+l*i, y0-points[i], x0+l*(i+1), y0-points[i+1]);
		    g2.draw(line);
		}
		
//		for (int i = 0; i < width; i++) {
//			int index = i*nSpectralPoints/width;				
//			double sample1 = Tools.m2vMagnitude(magnitude[index]);				
//			Line2D.Double line = new Line2D.Double(i, y0, i, y0-sample1);	            
//	           g2.draw(line);
//		}
			            
		if(showPeaks == true) {
			g2.setColor( ColorsAndStrokes.RED );
			Peak[] peaks = rm.getAudioEngine().getAudioAnalyser().getPeaks();
			for(int n=0; n<peaks.length; n++) {
//				Line2D.Double line1 = new Line2D.Double(x0+peaks[n].frequency*l*magnitude.length/44100,0,
//														x0+peaks[n].frequency*l*magnitude.length/44100,y0);         		
//				g2.draw(line1);
				Ellipse2D.Double circle = new Ellipse2D.Double(x0+peaks[n].frequency*l*magnitude.length/44100-2, y0-Tools.lin2dB(peaks[n].amplitude)*y0/spectroscopeMaxMagnitude-2, 4, 4);
				g2.fill(circle);
			}
            
			g2.setColor( ColorsAndStrokes.BLUE );
            double f = rm.getAudioEngine().getAudioAnalyser().getFundamentalFrequency();
            // fundamental
            line.setLine(x0+f*l*magnitude.length/44100,0,x0+f*l*magnitude.length/44100,y0);
//            Ellipse2D.Double circFund = new Ellipse2D.Double(x0+f*l*magnitude.length/44100-5, y0-5, 10, 10);
            g2.draw(line);
			
            // threshold line
			g2.setColor( ColorsAndStrokes.YELLOW );
			double threshold = PeakDetector.getPeakThreshold();
			threshold = threshold*y0/spectroscopeMaxMagnitude;
			line.setLine(0,y0-threshold,getWidth(),y0-threshold);
			g2.draw(line);
			
			for(int i=0; i<spectrosopceNSpectralPoints; i++) {
				double freq = i*44100/magnitude.length;
				threshold =	PeakDetector.threshold(freq);
				line.setLine(x0+l*i, y0-threshold*y0/spectroscopeMaxMagnitude, x0+l*(i+1), y0-threshold*y0/spectroscopeMaxMagnitude);
				g2.draw(line);
			}
		}
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
	
	private void createSpectroscopeBackground() {
		int width = this.getWidth();
		int height = this.getHeight();
		Graphics2D g2;
		
		backgroundSpectroscope = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2 = backgroundSpectroscope.createGraphics();
		
		g2.setColor( ColorsAndStrokes.BACKGROUND );
		g2.fillRect(0, 0, width, height);
		
		// coordinate center
		int ox = 10, oy = height - 10;
		g2.setColor( ColorsAndStrokes.GRAY );
		// x-axis, frequency-axis
		g2.drawLine(ox, oy, width-10, oy);
//		int Nx = 22;
//		for(int i=1; i<=Nx; i++) {
//			int deltax = (width-2*10)/Nx; 
//			g2.drawLine(ox+i*deltax, oy-5, ox+i*deltax, oy+5);
//		}
		g2.drawLine(ox,10,ox,oy);
		
		// y-axis, amplitude-axis, dB
		
		int deltay = 10;
		int Ny = (int)Math.floor((height-10)/deltay);
		for(int i=1; i<=Ny; i++) {
			g2.drawLine(ox-5, oy-(i*deltay), ox+5, oy-(i*deltay));
//			if(i%2==0)g2.drawString(String.valueOf(i*10), 15, oy-(i*deltay)+4);
		}
		
		g2.drawString("Magnitude vs. Frequency (dB-Hz)", 100, 20);
		double fmax = 44100*spectrosopceNSpectralPoints/(rm.getAudioEngine().getAudioAnalyser().getWindowSize());
		g2.drawString(Double.toString(fmax)+" Hz", width-ox-40, oy-10);	
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
	
	public boolean getShowPeaks() {
		return showPeaks;
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
		refresh();
	}

	public void componentShown(ComponentEvent arg0) {
		
	}
	
	public void refresh() {
		backgroundFrequencyTracker = null;
		backgroundSpectroscope = null;
		imageFrequencyTrackerPlot = null;
		
		width = this.getWidth();
		height = this.getHeight();
	}
	
	public void setNOfSpectralPoints(int N) {
		spectrosopceNSpectralPoints = N;
		refresh();
	}

	public double getMaximumMagnitude() {
		return spectroscopeMaxMagnitude;
	}

	public void setMaximumMagnitude(double maxMagnitude) {
		this.spectroscopeMaxMagnitude = maxMagnitude;
		refresh();
	}

	public void mouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub		
		mousePressedPoint = me.getPoint();
		oscilloscopeOldMaxAmplitude = oscilloscopeMaxAmplitude;
		spectroscopeOldMaxMagnitude = spectroscopeMaxMagnitude;
		spectroscopeOldNSpectralPoints = spectrosopceNSpectralPoints;
		f0OldMin = f0Min;
		f0OldMax = f0Max;
		freqTrackerOldDuration = freqTrackerDuration;
//		System.out.println("p: "+mousePressedPoint.x +", "+mousePressedPoint.y);		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseDragged(MouseEvent me) {
		mouseDraggedPoint = me.getPoint();		
		double deltaY = (double)(mouseDraggedPoint.y - mousePressedPoint.y)/getHeight();
		double deltaX = (double)(mouseDraggedPoint.x - mousePressedPoint.x)/getWidth();
		
		if((me.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
			if( mode == OSCILLOSCOPE ) {
				oscilloscopeMaxAmplitude = oscilloscopeOldMaxAmplitude*Math.exp(deltaY);
//				System.out.println(oscilloscopeMaxAmplitude);
			} else if( mode == SPECTROSCOPE ) {
				backgroundSpectroscope = null;
				spectroscopeMaxMagnitude = spectroscopeOldMaxMagnitude*Math.exp(deltaY);
//				System.out.println(spectroscopeMaxMagnitude);
				int nNewPoints = spectroscopeOldNSpectralPoints+(int)(deltaX*1000);
				if(nNewPoints < rm.getAudioEngine().getAudioAnalyser().getMagnitudeSpectrum().length/2 &&
					nNewPoints > 0) {
					spectrosopceNSpectralPoints = nNewPoints;
				}
//				System.out.println(spectrosopceNSpectralPoints);				
			} else if( mode == FREQUENCY_TRACKER ) {
				backgroundFrequencyTracker = null;
				rm.getAudioEngine().getAudioAnalyser().setF0Max( f0OldMax*Math.exp(-deltaY) );
//				rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().getDuration();
				rm.getAudioEngine().getAudioAnalyser().getRecordFundamental().setDuration( freqTrackerOldDuration+(deltaX*5) );
			}
		}
		if( (me.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != MouseEvent.CTRL_DOWN_MASK ) {
			if( mode == FREQUENCY_TRACKER ) {
				backgroundFrequencyTracker = null;
				rm.getAudioEngine().getAudioAnalyser().setF0Max( f0OldMax*Math.exp(-deltaY) );
				rm.getAudioEngine().getAudioAnalyser().setF0Min( f0OldMin*Math.exp(-deltaY) );
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
