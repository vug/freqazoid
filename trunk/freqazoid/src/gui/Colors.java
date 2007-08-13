package gui;

import java.awt.Color;


public class Colors {
	
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
