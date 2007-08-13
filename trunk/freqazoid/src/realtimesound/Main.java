/*
 * Main.java
 *
 * Created on March 24, 2007, 2:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package realtimesound;

import java.io.File;

import gui.ResourceManager;

/**
 *
 * @author HAL
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        ResourceManager rm = new ResourceManager();
        
        File file = new File("c:\\java\\projects\\CELLO2.wav");
        //rm.getAudioEngine().openFile(file);
        /*byte b1 = 0;
        byte b2 = 2;
        
        int x;
        x = (int)b1 + (((int)b2)<<8);
        System.out.println(x);*/
    }
    
}