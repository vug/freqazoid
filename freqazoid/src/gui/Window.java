package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import realtimesound.ResourceManager;
	
public class Window extends JFrame {
	
	private ResourceManager rm;
	
	public Window(ResourceManager rm) {
		super("Freqazoid");
		this.rm = rm;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500,300);
		this.setLocation(100,100);
		this.setLayout(new BorderLayout());
	}
}
