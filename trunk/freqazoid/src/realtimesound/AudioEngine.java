/*
 * AudioEngine.java
 *
 * Created on March 24, 2007, 2:12 PM
 *
 */
package realtimesound;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AudioEngine implements Runnable {
	
    public static final int SAMPLE_RATE = 44100;
    public static final int NYQUIST_FREQUENCY = SAMPLE_RATE / 2;
    public static final int BIT_DEPTH = 16;
    public  static final int N_CHANNELS = 1;
    public static final boolean BIG_ENDIAN = false;
    private AudioFormat format;
    
    private int bufferSize = 4096;    
    private TargetDataLine inputLine;
    private SourceDataLine outputLine;  
    private Mixer.Info[] inputInfos;
    private Mixer.Info[] outputInfos;
    private String[] inputNames;
    private String[] outputNames;
    
    private int blockSize = 512;
    
    public static final int STARTING = 0, RUNNING = 1, 
    						PAUSED = 2, STOPPING = 3, STOPPED = 4;    
    private int engineStatus;    
    
    private AudioAnalyser audioAnalyser;
    
    private AudioInputStream inputFileStream;
    private AudioFileFormat inputFileFormat;
    private File inputFile;
    private int fileStatus;
    public static final int PLAYING = 0, ENDED = 1;
    
    private boolean muteMicrophone;
    public boolean muteFile;
    private boolean muteSpeaker;
    
    private Thread audioThread;
    
    private Mixer.Info[] mixerInfo;
    private DataLine.Info sourceInfo;
    private DataLine.Info targetInfo;
    
    private int cont = 1;
    
    public AudioEngine() {
    	
    	muteSpeaker = false;
        muteMicrophone = false;
        muteFile = true;
        
        engineStatus = STOPPED;
        fileStatus = ENDED;
        int frameSizeInBytes = BIT_DEPTH/8;
        int frameRate = SAMPLE_RATE;
        format = new  AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                SAMPLE_RATE, BIT_DEPTH, N_CHANNELS, frameSizeInBytes, frameRate, BIG_ENDIAN);
        AudioFormat format2 = new  AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                22050, BIT_DEPTH, N_CHANNELS, frameSizeInBytes, frameRate, BIG_ENDIAN);
        
//        System.out.println("audio format: "+format.toString());
//        System.out.println("conversion supported: "+AudioSystem.isConversionSupported(format, format2));
        
        // Find the Mixer's in the computer        
        mixerInfo = AudioSystem.getMixerInfo();
        
        Vector<Mixer.Info> inputInfosVector = new Vector<Mixer.Info>();
        Vector<Mixer.Info> outputInfosVector = new Vector<Mixer.Info>();
        
        for(int i=0; i<mixerInfo.length; i++) {
        	Mixer mixer = AudioSystem.getMixer(mixerInfo[i]);
        	if ( mixerInfo[i].getName()!=null &&
        			mixerInfo[i].getName().length()>3 &&
        			!mixerInfo[i].getName().substring(0, 4).equalsIgnoreCase("port") )
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
        inputNames = new String[inputInfos.length+1];
    	for (int i = 0; i < inputInfos.length; i++) {
			inputNames[i] = inputInfos[i].getName();
		}
    	inputNames[inputNames.length-1] = "No Input";
        
        outputInfosVector.trimToSize();
        outputInfos = new Mixer.Info[outputInfosVector.size()];
        outputInfos = outputInfosVector.toArray(outputInfos);
        outputNames = new String[outputInfos.length+1];
    	for (int i = 0; i < outputInfos.length; i++) {
			outputNames[i] = outputInfos[i].getName();
		}
    	outputNames[outputNames.length-1] = "No Output";
    	
    	targetInfo = new DataLine.Info(TargetDataLine.class, format);        
        sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        
        selectInputAndOutputLine();
        if(cont == 0) {
        	System.exit(0);
        }
        
        try {
			inputLine.open(format, bufferSize);
			outputLine.open(format, bufferSize);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
        
        audioAnalyser = new AudioAnalyser();
        audioThread = new Thread(this);
        audioThread.start();
    }
    
    public void run()  {
//        int numBytesRead;
//        int numBytesWritten;        
        byte[] dataFromMic = new byte[blockSize*2];
        byte[] dataFromFile = new byte[blockSize*2];
        byte[] dataMasterOut = new byte[blockSize*2];
        
        inputLine.start();
        outputLine.start();
//        System.out.println("Engine started.");
        engineStatus = RUNNING;        
        
        while(true) {
            switch(engineStatus) {
            	case STARTING:
            		try {
						inputLine.open(format, bufferSize);
						outputLine.open(format, bufferSize);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					}
					
            		inputLine.start();
                    outputLine.start();
                    engineStatus = RUNNING;
//                    System.out.println("Engine started.");
                    break;
                case RUNNING:
                
                if( inputLine.available() > blockSize*2 )
                {
                	// Read the next chunk of data from the TargetDataLine.
//                	numBytesRead =  inputLine.read(dataFromMic, 0, dataFromMic.length);
                	inputLine.read(dataFromMic, 0, dataFromMic.length);
                	
                	if ( fileStatus == PLAYING && inputFile != null ) {
                		try {
							int position = inputFileStream.read(dataFromFile);
							if( position == -1 ) {
								inputFileStream.close();
//								inputFile = null;
								fileStatus = ENDED;
//								System.out.println("end of file");
								dataFromFile = new byte[blockSize*2];
							}
                		} catch (IOException e) {
							e.printStackTrace();
						}
                	}              
                	
                	int[] masterOut = new int[blockSize];                	
                	
                	for(int i=0, j = 0; j<blockSize; i+=2, j++) {
                		masterOut[j] = 0;
                		if( !muteMicrophone ) {
                			masterOut[j] += ((dataFromMic[i] & 0xFF) | (dataFromMic[i+1]<<8));            	
                		}
                		if( !muteFile ) {
                			masterOut[j] += (dataFromFile[i] & 0xFF) | (dataFromFile[i+1]<<8);
                		}
                	}                	
                	audioAnalyser.addSamples(masterOut);    				
                	
                	for(int i=0, j=0; j<blockSize; i+=2, j++) {
                		if( !muteSpeaker ) {
                			dataMasterOut[i]   = (byte)(masterOut[j] &  0xFF);
                    		dataMasterOut[i+1] = (byte)(masterOut[j] >> 8);
                		} else {
                			dataMasterOut[i] = 0;
                			dataMasterOut[i+1] = 0;
                		}
                		
                	}     	
                	
//                	numBytesWritten = outputLine.write(dataMasterOut, 0, dataMasterOut.length);
                	outputLine.write(dataMasterOut, 0, dataMasterOut.length);
                }
            
                break;
                case PAUSED:
                    break;
                case STOPPING:
                    //inputLine.drain();
                	//outLine.drain();
                	if(inputLine != null) {
                		inputLine.stop();
                		inputLine.close();
                	}
                    if(outputLine != null) {
                    	outputLine.stop();
                    	outputLine.close();
                    }              
                    
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
			// for conversion...
//			inputFileStream = AudioSystem.getAudioInputStream(format, inputFileStream);
			inputFileFormat = AudioSystem.getAudioFileFormat(file);
//			System.out.println("duration: "+ inputFileFormat.getFrameLength()/inputFileFormat.getFormat().getSampleRate() );
//			System.out.println("sample rate: "+inputFileFormat.getFormat().getSampleRate());
//			System.out.println("type: "+inputFileFormat.getType());
			
//			System.out.println("mark supported? "+ inputFileStream.markSupported() );
		} catch (UnsupportedAudioFileException e) {
//			System.out.println("unsupported file format");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileStatus = PLAYING;
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
//            System.out.println("pausing");
            inputLine.stop();
            //inputLine.close();
            engineStatus = PAUSED;
        }
        else if(engineStatus == PAUSED) {
//            System.out.println("unpausing");
            engineStatus = RUNNING;
            inputLine.start();
        }        
    }
    
    public void changeInputAndOutputLine(int indexInput, int indexOutput) {
    	stopEngine();
        try {
        	if(indexInput == inputNames.length-1) {
        		
        	} else { 
        		inputLine = (TargetDataLine) AudioSystem.getMixer( inputInfos[indexInput] ).getLine(targetInfo);
        	}
        	
        	if(indexOutput == outputNames.length-1) {
        		
        	} else {
        		outputLine = (SourceDataLine) AudioSystem.getMixer( outputInfos[indexOutput] ).getLine(sourceInfo);
        	}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		/*
		 * Wait until audioEngine Thread come to STOPPED state.
		 */
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	startEngine();
    }
    
    public void selectInputAndOutputLine() {
    	JFrame selectDevicesFrame = new JFrame("Select Audio Devices");
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    	
    	final JDialog dialog = new JDialog(selectDevicesFrame, "Select Audio Devices", true);
    	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    	dialog.setLocation(100, 100);
    	
    	JPanel upperPanel = new JPanel();
    	JLabel label = new JLabel("input: ");
    	upperPanel.add(label);
    	
    	final ComboBoxModel comboBoxInputDeviceModel = new DefaultComboBoxModel(inputNames);
		final JComboBox comboBoxInputDevice = new JComboBox();
		comboBoxInputDevice.setModel(comboBoxInputDeviceModel);
		upperPanel.add(comboBoxInputDevice);
		
		JPanel lowerPanel = new JPanel();
		JLabel label2 = new JLabel("output: ");
    	lowerPanel.add(label2);
    	
    	ComboBoxModel comboBoxOutputDevicesModel = new DefaultComboBoxModel(outputNames);
		final JComboBox comboBoxOutputDevices = new JComboBox();
		comboBoxOutputDevices.setModel(comboBoxOutputDevicesModel);
		lowerPanel.add(comboBoxOutputDevices);
		
		JPanel lowestPanel = new JPanel();
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeInputAndOutputLine(comboBoxInputDevice.getSelectedIndex(), comboBoxOutputDevices.getSelectedIndex());
				dialog.setVisible(false);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				cont = 0;
				dialog.setVisible(false);
			}	
		});
		
		lowestPanel.add(ok);
		lowestPanel.add(cancel);
		
		panel.add(upperPanel);
		panel.add(lowerPanel);
		panel.add(lowestPanel);

    	dialog.setContentPane(panel);
    	dialog.pack();
    	dialog.setVisible(true);
    }
    
    
    public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int block_size) {
		this.blockSize = block_size;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int buffer_size) {
		this.bufferSize = buffer_size;
		stopEngine();
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

	public boolean isMuteMicrophone() {
		return muteMicrophone;
	}

	public void setMuteMicrophone(boolean muteMicrophone) {
		this.muteMicrophone = muteMicrophone;
	}

	public boolean isMuteSpeaker() {
		return muteSpeaker;
	}

	public void setMuteSpeaker(boolean muteSpeaker) {
		this.muteSpeaker = muteSpeaker;
	}	
	
}