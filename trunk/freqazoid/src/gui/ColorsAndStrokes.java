package gui;

import java.awt.BasicStroke;
import java.awt.Color;

public class ColorsAndStrokes {
	
	public static final Color BACKGROUND =  new Color(100, 100, 100);
	public static final Color GRAY =  new Color(150, 150, 150);
	public static final Color GREEN =  new Color(0, 255, 0);
	public static final Color RED =  new Color(255, 0, 0);
	public static final Color YELLOW = new Color(255,255,0);
	public static final Color BLUE = new Color(0,255,255);
	
	private final static float dash1[] = {2.0f, 4.0f};
	public static final BasicStroke DASHED = new BasicStroke(1.0f, 
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER,
			10.0f, dash1, 0.0f);
	public static final BasicStroke NORMAL = new BasicStroke();
	
	public static Color[] colorGradient;
	private int nColors;
	
	public ColorsAndStrokes(ResourceManager rm) {
		nColors = 100;
		
		colorGradient = new Color[128];
		for(int i=0; i<nColors; i++) {
			colorGradient[i] = new Color(i, i ,i);
		}
	}

}
