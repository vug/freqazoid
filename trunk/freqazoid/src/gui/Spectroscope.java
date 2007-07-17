package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import math.DFT;

import realtimesound.ResourceManager;

public class Spectroscope extends JPanel {
	
	private ResourceManager rm;
    private double[] amplitude;
    private double[] magnitude;
    private int nPoints = 128;
    private int head = 0;
	
	public Spectroscope(ResourceManager rm) {
		super();
		this.rm = rm;
		
		amplitude = new double[nPoints];
		magnitude = new double[nPoints];
	}
	
    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(new Color(100,100,100));
        Rectangle2D.Double rect = new Rectangle2D.Double(0,0,getWidth(),getHeight());
        g2.fill(rect);
        
        double x, y0, l;
        for(int i=0; i<nPoints-1; i++) {
            //x=0.0;
            y0 = this.getHeight();
            l=(double)this.getWidth()/nPoints;
            Line2D.Double line = new Line2D.Double(l*i, -magnitude[i]+y0, l*(i+1), -magnitude[i+1]+y0);
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
        	magnitude = DFT.magnitude(amplitude);
            head = 0;
        }
    }
    
	public int getNPoints() {
		return nPoints;
	}

}
