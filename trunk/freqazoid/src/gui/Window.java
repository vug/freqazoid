package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

	
@SuppressWarnings("serial")
public class Window extends JFrame {
	
	//private ResourceManager rm;
	
	public Window(ResourceManager rm) {
		super("Freqazoid");
		//this.rm = rm;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400,250);
		this.setLocation(50,50);
		this.setLayout(new BorderLayout());
	}
}
