package gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import sun.awt.WindowClosingListener;

	
@SuppressWarnings("serial")
public class Window extends JFrame implements WindowListener{
	
	private ResourceManager rm;
	
	public Window(ResourceManager rm) {
		super("Freqazoid");
		this.rm = rm;
		
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500,300);
		this.setLocation(50,50);
		this.setLayout(new BorderLayout());
		this.addWindowListener(this);
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent arg0) {		
		rm.exitProgram();		
	}

	public void windowClosing(WindowEvent arg0) {
		this.setVisible(false);
		this.dispose();
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
