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

import realtimesound.ResourceManager;

@SuppressWarnings("serial")
public class Spectroscope extends JPanel {
	
	//private ResourceManager rm;
    private double[] amplitude;
    private Complex[] magnitude;
    private Vector<Peak> peaks;
    private int nPoints = 512;
    private int head = 0;
	
	public Spectroscope(ResourceManager rm) {
		super();
		//this.rm = rm;
		
		amplitude = new double[nPoints];
		magnitude = new Complex[nPoints];
	}
	
    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(new Color(100,100,100));
        Rectangle2D.Double rect = new Rectangle2D.Double(0,0,getWidth(),getHeight());
        g2.fill(rect);
        
        double x, y0, l;
        for(int i=0; i<nPoints/2-1; i++) {
            //x=0.0;
            y0 = this.getHeight();
            l=(double)this.getWidth()/(nPoints/2);
            
            //System.out.println(magnitude[i]);
            double sample1 = -30*Math.log10(Complex.abs(magnitude[i]));
            double sample2 = -30*Math.log10(Complex.abs(magnitude[i+1]));
            
            Line2D.Double line = new Line2D.Double(l*i, sample1+y0, l*(i+1), sample2+y0);
            
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
        double x = (this.getHeight()/2)*((double)newPoint/1000);
        //System.out.println(head);
        amplitude[head] = x;
        head++;
        if(head == nPoints) {
        	//magnitude = DFT.magnitude(amplitude);
        	
        	amplitude = DFT.window(amplitude);
        	magnitude = FFT.forward( Tools.makeComplex(amplitude) );
        	
        	//magnitude = Tools.lowpass(magnitude,3);
            head = 0;
            //peaks = PeakDetector.detect( Complex.abs(magnitude));
        }
        this.repaint();
    }
    
	public int getNPoints() {
		return nPoints;
	}

}
