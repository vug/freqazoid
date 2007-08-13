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
import math.Tools;

public class Spectrogram extends JPanel {
	
//	private ResourceManager rm;
    private double[] amplitude;
    private double[] magnitude;
    private Vector<Peak> peaks;
    private int nPoints = 512;
    private int head = 0;
    
    private Color[] colorGradient;
    private int[][] colorMap;    
	private int nColors=100;
	private int nSegments=10;
	private int colorHead = 0;
	
	public Spectrogram(ResourceManager rm) {
		super();
		//this.rm = rm;
		
		amplitude = new double[nPoints];
		magnitude = new double[nPoints];
		
		colorGradient = new Color[nColors];
		for(int i=0; i<nColors; i++) {
			int g = (int)(256.0*i/nColors);
			colorGradient[i] = new Color(g, g ,g);
		}
		
		colorMap = new int[nPoints][nSegments];
		for(int y=0; y<nPoints; y++) {
			for(int x=0; x<nSegments; x++) {		
				colorMap[y][x] = 0;
			}
		}
	}
	
	public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        float dy=(float)getHeight()/nPoints;
        float dx=(float)getWidth()/nSegments;
        
        for(int y=0; y<nPoints; y++) {
        	for(int x=0; x<nSegments; x++) {
        		g2.setColor(colorGradient[ colorMap[y][x] ]);
        		Rectangle2D.Float square = new Rectangle2D.Float(x*dx,y*dy,(x+1)*dx,(y+1)*dy);
        		g2.fill(square);
        	}
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
            
            for(int i=0; i<nPoints; i++) {
            	colorMap[i][colorHead] = (int)(2*magnitude[i]);
            }
            
            
            colorHead++;
            if(colorHead==nSegments) colorHead=0;
            
            //peaks = PeakDetector.detect( Complex.abs(magnitude));
            this.repaint();
        }
        
    }
	
	public int getNPoints() {
		return nPoints;
	}

}
