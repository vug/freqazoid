package gui;

import java.awt.Color;

public class Colors {
	
	public static final Color BACKGROUND =  new Color(100, 100, 100);
	public static final Color GREEN =  new Color(0, 255, 0);
	public static final Color RED =  new Color(255, 0, 0);
	
	private ResourceManager rm;
	
	public static Color[] colorGradient;
	private int nColors;
	
	public Colors(ResourceManager rm) {
		this.rm = rm;
		nColors = 100;
		
		colorGradient = new Color[128];
		for(int i=0; i<nColors; i++) {
			int g = 256*i/nColors;
			colorGradient[i] = new Color(i, i ,i);
		}
	}

}
