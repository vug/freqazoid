/*
 * AudioEngine.java
 *
 * Created on March 24, 2007, 2:12 PM
 *
 */
package realtimesound;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author HAL
 */
public class AudioEngine implements Runnable {
    
    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_DEPTH = 16;
    private static final int N_CHANNELS = 1;
    private static final boolean LITTLE_ENDIAN = false;
    private static final int BUFFER_SIZE = 4096;
    private TargetDataLine inputLine;
    private SourceDataLine outputLine;
    private boolean stopped = false;    
    private ByteArrayOutputStream outStream;
    
    public static final int RUNNING = 0, PAUSED = 1, STOPPED = 2;
    private ResourceManager rm;
    private int engineStatus;
    private String[] inputInfos;
    private String[] outputInfos;
    
    
    /** Creates a new instance of AudioEngine */
    public AudioEngine(ResourceManager rm) {
        this.rm = rm;
        
        engineStatus = STOPPED;
        int frameSizeInBytes = BIT_DEPTH/8;
        int frameRate = SAMPLE_RATE;
        AudioFormat format = new  AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                SAMPLE_RATE, BIT_DEPTH, N_CHANNELS, frameSizeInBytes, frameRate, LITTLE_ENDIAN);
        
        System.out.println("audio format: "+format.toString());
        
        /*
         * These latencies can be determined by the difference  
between getLongFramePosition() and the number of frames you have read from  
/ written to the line.
 * 
 * I use a trvial control loop which notes how many frames have been written  
and how many frames have actually got to the hardware  
(getLongFramePosition()). The difference in these frame counts is the  
number of frames output latency which can easily be converted to  
milliseconds output latency. Once the control loop is stable the overall  
input/output latency remains constant, I minimise input latency by crudely  
flushing the TargetDataLine once the loop is stable. Rather than blocking  
I return silence if the TargetDataLine doesn't have enough data so it then  
stablises at a minimum latency.
         */
        
        /*
         * mixer'lere bak
         */
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        Line.Info[] lineInfo = AudioSystem.getSourceLineInfo(new Line.Info(SourceDataLine.class));
        
        for(int i=0; i<lineInfo.length; i++) {
        	//AudioSystem.getSourceDataLine(arg0, arg1)
        	System.out.println(lineInfo[i]);
        }
        
        inputInfos = new String[info.length];
        outputInfos = new String[info.length];
        int n=0;
        int m=0;
        for(int i=0; i<info.length; i++) {
        	if(AudioSystem.getMixer(info[i]).getSourceLineInfo().length > 0) {        		
        		inputInfos[n] = n+": "+info[i].getName() +": "+info[i].getDescription();
        		n++;
        	}
        	if(AudioSystem.getMixer(info[i]).getTargetLineInfo().length > 0) {
        		outputInfos[m] = m+": "+info[i].getName() +": "+info[i].getDescription();
        		m++;
        	}        	
        	//System.out.println(info[i].getName()+"::"+info[i].getDescription());
        	//System.out.println(mixerInfo[i]);
        }
        
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);        
        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        
        if(AudioSystem.isLineSupported(targetInfo)) {
            System.out.println("input format supported by the system");
            try {
                System.out.println("trying to open an input line...");
                inputLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
                inputLine.open(format, BUFFER_SIZE);
                System.out.println("Input line opened with a buffer size: "
                        + inputLine.getBufferSize());
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
            }            
        }
        
        if(AudioSystem.isLineSupported(sourceInfo)) {
            System.out.println("output format supported by the system");
            try {
                System.out.println("trying to open an output line...");
                outputLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
                outputLine.open(format, BUFFER_SIZE);
                System.out.println("Output line opened with a buffer size: "
                        + inputLine.getBufferSize());
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
            }            
        }
        
        outStream = new ByteArrayOutputStream();
    }
    
    public void run()  {
        int numBytesRead;
        int numBytesWritten;
        byte[] dataIn = new byte[64/*inputLine.getBufferSize() / 5*/];
        byte[] dataSynthesis = new byte[64];
        int n=0;
        
        inputLine.start();
        outputLine.start();
        System.out.println("Engine started.");
        engineStatus = RUNNING;
        
        long counter = 0;
        
        while(true) {
            switch(engineStatus) {        
                case RUNNING:
                // Read the next chunk of data from the TargetDataLine.
                
                numBytesRead =  inputLine.read(dataIn, 0, dataIn.length);            
                // Save this chunk of data.
                
                /* Synthesize simple sinusoid */
                for(int i=0; i<64; i+=2) {
                    n++;
                    double x = Math.sin(220*2*Math.PI*n/SAMPLE_RATE);
                    int sample = (int)(x*5000);
                    dataSynthesis[i]   = (byte)( sample     & 0xFF);
                    dataSynthesis[i+1] = (byte)((sample>>8) & 0xFF);                                    
                }
                
                //System.out.println("Number of Read Bytes: " + numBytesRead);
                //outStream.write(dataIn, 0, numBytesRead);
                
                /* plot graph */
                for(int i=0; i<numBytesRead; i+=2) {           
                    int x = dataIn[i] | (dataIn[i+1]<<8);
                    counter++;
                    if(counter%10==0) {
                        rm.getCanvas().setData(x);
                        rm.getCanvas().repaint();
                    }
                }          
                
                
                //System.out.println("Available:" + outputLine.available());
                numBytesWritten = outputLine.write(dataIn, 0, numBytesRead);
            
                //numBytestoRead= outputLine.available();
            
                break;
                case PAUSED:
                    break;
                case STOPPED:
                    //inputLine.drain();
                    System.out.println("stoppin input line");
                    inputLine.stop();
                    System.out.println("closing input line");
                    inputLine.close();
                    System.out.println("Engine stopped.");
                    return;
            }    
        }
    }
    
    public int getEngineStatus() {
        return engineStatus;
    }
    
    public void setEngineStatus(int s) {
        engineStatus = s;
    }
    
    public void pauseEngine() {
        if(engineStatus == RUNNING) {
            engineStatus = PAUSED;
            // Tikkayt, drain read edilmemisse beele bekliyor...            
            //inputLine.drain();
            System.out.println("pausing");
            inputLine.stop();
            //System.out.println("patlican");
            //inputLine.close();
            engineStatus = PAUSED;
        }
        else if(engineStatus == PAUSED) {
            System.out.println("unpausing");
            engineStatus = RUNNING;
            inputLine.start();
        }        
    }
    
    public void stopEngine() {
        engineStatus = STOPPED;
    }
    
    public String[] getInputInfos() {
    	return inputInfos;
    }
    
    public String[] getOutputInfos() {
    	return outputInfos;
    }
}