/*
 * Canvas.java
 *
 * Created on March 25, 2007, 5:44 AM
 */

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 * @author HAL
 */
public class Oscilloscope extends JPanel {
    
    private double[] amplitude;
    private int nPoints = 128;
    private int head = 0;
    
    /** Creates a new instance of Canvas */
    public Oscilloscope() {
        //Dimension dim = new Dimension(200, 200);
        //this.setSize(dim);
        //this.setMinimumSize(dim);
        this.setBackground(new Color(100,100,100));
        
        amplitude = new double[nPoints];
    }
    
    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(new Color(100,100,100));
        Rectangle2D.Double rect = new Rectangle2D.Double(0,0,getWidth(),getHeight());
        g2.fill(rect);
        
        double x, y0, l;
        for(int i=0; i<nPoints-1; i++) {
            //x=0.0;
            y0 = this.getHeight()/2;
            l=(double)this.getWidth()/nPoints;
            Line2D.Double line = new Line2D.Double(l*i, amplitude[i]+y0, l*(i+1), amplitude[i+1]+y0);
            g2.setColor(new Color(0,255,0));
            g2.draw(line);
        }        
        g2.dispose();
    }
    
    public void setData(int newPoint) {
        double x = (this.getHeight()/2)*((double)newPoint/10000);
        //System.out.println(head);
        amplitude[head] = x;
        head++;
        if(head == nPoints) {
            head = 0;
        }
    }

	public int getNPoints() {
		return nPoints;
	}
}
