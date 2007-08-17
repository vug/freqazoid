/*
 * AudioEngine.java
 *
 * Created on March 24, 2007, 2:12 PM
 *
 */
package realtimesound;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioEngine implements Runnable {
	
    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_DEPTH = 16;
    private static final int N_CHANNELS = 1;
    private static final boolean BIG_ENDIAN = false;
    private AudioFormat format;
    
    private static final int BUFFER_SIZE = 4096*8;    
    private TargetDataLine inputLine;
    private SourceDataLine outputLine;  
    private Mixer.Info[] inputInfos;
    private Mixer.Info[] outputInfos;
    
    private static final int BLOCK_SIZE = 1024*4;
    
    public static final int STARTING = 0, RUNNING = 1, 
    						PAUSED = 2, STOPPING = 3, STOPPED = 4;    
    private int engineStatus;    
    
    private AudioAnalyser audioAnalyser;
    
    private AudioInputStream inputFileStream;
    private AudioFileFormat inputFileFormat;
    private File inputFile;
    
    public boolean muteMicrophone;
    public boolean muteFile;
    
    private Thread audioThread;
    
    private Mixer.Info[] mixerInfo;
    private DataLine.Info sourceInfo;
    private DataLine.Info targetInfo;
    
    public AudioEngine() {
        
        muteMicrophone = false;
        muteFile = true;
        
        engineStatus = STOPPED;
        int frameSizeInBytes = BIT_DEPTH/8;
        int frameRate = SAMPLE_RATE;
        format = new  AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                SAMPLE_RATE, BIT_DEPTH, N_CHANNELS, frameSizeInBytes, frameRate, BIG_ENDIAN);
        
        System.out.println("audio format: "+format.toString());
        
        // Find the Mixer's in the computer        
        mixerInfo = AudioSystem.getMixerInfo();
        
        Vector<Mixer.Info> inputInfosVector = new Vector<Mixer.Info>();
        Vector<Mixer.Info> outputInfosVector = new Vector<Mixer.Info>();
        
        for(int i=0; i<mixerInfo.length; i++) {
        	Mixer mixer = AudioSystem.getMixer(mixerInfo[i]);
        	if ( !mixerInfo[i].getName().substring(0, 4).equalsIgnoreCase("port") )
        	{
        		if( mixer.getTargetLineInfo().length > 0 ) {
        			inputInfosVector.add(mixerInfo[i]);
        		} 
        		if ( mixer.getSourceLineInfo().length > 0) {
        			outputInfosVector.add(mixerInfo[i]);
        		}
        	}        	
        }
        inputInfosVector.trimToSize();
        inputInfos = new Mixer.Info[inputInfosVector.size()];
        inputInfos = inputInfosVector.toArray(inputInfos);
        
        outputInfosVector.trimToSize();
        outputInfos = new Mixer.Info[outputInfosVector.size()];
        outputInfos = outputInfosVector.toArray(outputInfos);      
        
        targetInfo = new DataLine.Info(TargetDataLine.class, format);        
        sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        
        if(AudioSystem.isLineSupported(targetInfo)) {
//            System.out.println("input format is supported by the system");
            try {
//                System.out.println("trying to open an input line...");
                inputLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
//                inputLine = (TargetDataLine) AudioSystem.getMixer( AudioSystem.getMixerInfo()[3] ).getLine(targetInfo);
                System.out.println(inputLine.getLineInfo().toString());
                inputLine.open(format, BUFFER_SIZE);
//                System.out.println("Input line opened with a buffer size: "
//                        + inputLine.getBufferSize());
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
            }            
        }
        
        if(AudioSystem.isLineSupported(sourceInfo)) {
//            System.out.println("output format supported by the system");
            try {
//                System.out.println("trying to open an output line...");
                outputLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
//                outputLine = (SourceDataLine) AudioSystem.getMixer( AudioSystem.getMixerInfo()[0] ).getLine(sourceInfo);
                outputLine.open(format, BUFFER_SIZE);
//                System.out.println("Output line opened with a buffer size: "
//                        + inputLine.getBufferSize());
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
            }            
        }
        
        audioAnalyser = new AudioAnalyser();
        audioThread = new Thread(this);
        audioThread.start();
    }
    
    public void run()  {
        int numBytesRead;
        int numBytesWritten;        
        byte[] dataFromMic = new byte[BLOCK_SIZE*2];
        byte[] dataFromFile = new byte[BLOCK_SIZE*2];
        byte[] dataMasterOut = new byte[BLOCK_SIZE*2];
        
        inputLine.start();
        outputLine.start();
//        System.out.println("Engine started.");
        engineStatus = RUNNING;        
        
        while(true) {
            switch(engineStatus) {
            	case STARTING:
            		try {
						inputLine.open(format, BUFFER_SIZE);
						outputLine.open(format, BUFFER_SIZE);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					}
					
            		inputLine.start();
                    outputLine.start();
                    engineStatus = RUNNING;
//                    System.out.println("Engine started.");
                    break;
                case RUNNING:
                
                if( inputLine.available() > BLOCK_SIZE*2 )
                {
                	// Read the next chunk of data from the TargetDataLine.
                	numBytesRead =  inputLine.read(dataFromMic, 0, dataFromMic.length);
                	
                	if ( !muteFile && inputFile != null ) {
                		try {
							inputFileStream.read(dataFromFile);
                		} catch (IOException e) {
							e.printStackTrace();
						}
                	}              
                	
                	int[] masterOut = new int[BLOCK_SIZE];                	
                	
                	for(int i=0, j = 0; j<BLOCK_SIZE; i+=2, j++) {
                		masterOut[j] = 0;
                		if( !muteMicrophone ) {
                			masterOut[j] += ((dataFromMic[i] & 0xFF) | (dataFromMic[i+1]<<8));            	
                		}
                		if( !muteFile ) {
                			masterOut[j] += (dataFromFile[i] & 0xFF) | (dataFromFile[i+1]<<8);
                		}
                	}                	
                	audioAnalyser.addSamples(masterOut);    				
                	
                	for(int i=0, j=0; j<BLOCK_SIZE; i+=2, j++) {                		
                		dataMasterOut[i]   = (byte)(masterOut[j] &  0xFF);
                		dataMasterOut[i+1] = (byte)(masterOut[j] >> 8);
                	}     	
                	
                	numBytesWritten = outputLine.write(dataMasterOut, 0, dataMasterOut.length);
                }
            
                break;
                case PAUSED:
                    break;
                case STOPPING:
                    //inputLine.drain();
                	//outLine.drain();
                    System.out.println("stopping lines");
                    inputLine.stop();
                    outputLine.stop();
                    System.out.println("closing lines");
                    inputLine.close();
                    outputLine.close();
                    System.out.println("Engine stopped.");
                    engineStatus = STOPPED;
                    break;
                case STOPPED:
                	break;
            }    
        }
    }
    
    public void openFile(File file) {
    	inputFile = file;
//        System.out.println("can read the file? "+ file.canRead() );
        try {
			inputFileStream = AudioSystem.getAudioInputStream(file);
			inputFileFormat = AudioSystem.getAudioFileFormat(file);
			
//			System.out.println("mark supported? "+ inputFileStream.markSupported() );
		} catch (UnsupportedAudioFileException e) {
			System.out.println("unsupported file format");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(inputFileFormat.toString());
    }
    
    public void reopenFile() {
    	if(inputFile !=  null ) {
    		openFile(inputFile);
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
            //inputLine.close();
            engineStatus = PAUSED;
        }
        else if(engineStatus == PAUSED) {
            System.out.println("unpausing");
            engineStatus = RUNNING;
            inputLine.start();
        }        
    }
    
    public void changeInputAndOutputLine(int indexInput, int indexOutput) {
    	stopEngine();
        try {			
        	inputLine = (TargetDataLine) AudioSystem.getMixer( inputInfos[indexInput] ).getLine(targetInfo);
        	outputLine = (SourceDataLine) AudioSystem.getMixer( outputInfos[indexOutput] ).getLine(sourceInfo);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		/*
		 * Wait until audioEngine Thread come to STOPPED state.
		 */
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	startEngine();
    }
    
    public void stopEngine() {
        engineStatus = STOPPING;
    }
    
    public void startEngine() {
    	engineStatus = STARTING;
    }
    
    public Mixer.Info[] getInputMixerInfos() {
    	return inputInfos;
    }
    
    public Mixer.Info[] getOutputMixerInfos() {
    	return outputInfos;
    }

	public AudioAnalyser getAudioAnalyser() {
		return audioAnalyser;
	}

	public Mixer.Info[] getMixerInfo() {
		return mixerInfo;
	}
	
}