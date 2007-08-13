package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import javax.swing.JPanel;

import math.Complex;
import math.DFT;
import math.FFT;
import math.Peak;
import math.PeakDetector;
import math.Tools;


@SuppressWarnings("serial")
public class Spectroscope extends JPanel {
	
	//private ResourceManager rm;
    private double[] amplitude;
    private double[] magnitude;
    private Vector<Peak> peaks;
    private int nPoints = 2048;
    private int head = 0;
	
	public Spectroscope(ResourceManager rm) {
		super();
		//this.rm = rm;
		
		amplitude = new double[nPoints];
		magnitude = new double[nPoints];
	}
	
    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(new Color(100,100,100));
        Rectangle2D.Double rect = new Rectangle2D.Double(0,0,getWidth(),getHeight());
        g2.fill(rect);
        
        double y0, l;
        for(int i=0; i<nPoints/2-1; i++) {
        	
        }
        for(int i=0; i<nPoints/2-1; i++) {
            //x=0.0;
            y0 = this.getHeight()-1;
            l=(double)this.getWidth()/(nPoints/2);
            
            double sample1 = 60*Math.log10(magnitude[i]+1);
            double sample2 = 60*Math.log10(magnitude[i+1]+1);
            //if(sample1 < 0 ) sample1 = 0;
            //if(sample2 < 0 ) sample1 = 0;
            
            //double sample1 = -5*magnitude[i];
            //double sample2 = -5*magnitude[i+1];
            
            Line2D.Double line = new Line2D.Double(l*i, y0-sample1, l*(i+1), y0-sample2);
            
            g2.setColor(new Color(255,0,0));
            
            if(peaks!=null) {
            	for(int n=0; n<peaks.size(); n++) {
            		Line2D.Double line1 = new Line2D.Double(peaks.elementAt(n).frequency*l,0,peaks.elementAt(n).frequency*l,y0);         		
            		//g2.draw(line1);
            	}
            }
            
            g2.setColor(new Color(0,255,0));
            g2.draw(line);
        }
                
        g2.dispose();
    }
    
    public void setData(int newPoint) {
        double x = ((double)newPoint)/32768;
        //System.out.println(head);
        amplitude[head] = x;
        head++;
        if(head == nPoints) {
        	//magnitude = DFT.magnitude(amplitude);        	
        	amplitude = DFT.window(amplitude,DFT.BLACKMANN);
        	magnitude = Complex.abs( FFT.forward( Tools.makeComplex(amplitude) ) );
        	
        	//magnitude = Tools.lowpass(magnitude,10);
            head = 0;
            //peaks = PeakDetector.detect( Complex.abs(magnitude));
        }
        this.repaint();
    }
    
	public int getNPoints() {
		return nPoints;
	}

}
