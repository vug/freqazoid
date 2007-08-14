package realtimesound;

import gui.ResourceManager;

public class AudioBuffer {
	
	private ResourceManager rm;
	private AudioAnalyser analyser;
	private int frameSize;
	private int hopSize;
	private int numberOfHops;
	private int[] buffer;
	private double[] frame;
	private int headWrite, headRead;
	
	public AudioBuffer(int frameSize, int numberOfHops, AudioAnalyser analyser) {
		this.analyser = analyser;
		headWrite = 0;
		headRead = 0;
		this.frameSize = frameSize;
		this.hopSize = frameSize/numberOfHops;
		buffer = new int[frameSize];
		frame = new double[frameSize];
	}
	
	public void addSamples(int[] newData) {
		int N = newData.length;
		
		for(int i=0; i<N; i++) {
			buffer[headWrite] = newData[i];
			headWrite++;
			if(headWrite==buffer.length) headWrite=0;
			
			
			// if the number of newly incoming samples equals to hopsize
			if(headWrite==headRead) {
//				System.out.println("esit");
				for(int n=0; n<frameSize; n++) {
					frame[n] = ((double)buffer[(headRead+n)%frameSize])/32768;		
				}
				headRead += hopSize;
				if(headRead==buffer.length) headRead=0;
				
//				for(int j=0; j<frame.length; j++) {
//					System.out.print(frame[j]+", ");
//				}
//				System.out.print("\n");
				//rm.getCanvas().setData( this.frame );
				analyser.bufferReady();
			}
		}
		
		/*
		System.out.print("r: "+headRead+", w: "+headWrite+", b: ");
		for(int i=0; i<buffer.length; i++) {
			System.out.print(buffer[i]+", ");
		}
		System.out.print("\n");
		
		System.out.print("send: ");
		for(int i=0; i<frame.length; i++) {
			System.out.print(frame[i]+", ");
		}
		System.out.print("\n");
		*/
	}

	public int getHopSize() {
		return hopSize;
	}

	public void setHopSize(int hopSize) {
		this.hopSize = hopSize;
	}

	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
		buffer = new int[frameSize];
		frame = new double[frameSize];
	}	
	
	public double[] getFrame() {
		return frame;
	}

	public int getNumberOfHops() {
		return numberOfHops;
	}

	public void setNumberOfHops(int numberOfHops) {
		this.numberOfHops = numberOfHops;
		hopSize = frameSize / numberOfHops;
	}

	public static void main(String[] args) {
		//AudioBuffer audioBuffer = new AudioBuffer(8,2);
		int[] samples = {1, 2, 3, 4, 5, 6, 7, 8};
		/*audioBuffer.addSamples(samples);
		int[] samples2 = {30, 31};
		audioBuffer.addSamples(samples2);
		audioBuffer.addSamples(samples2);
		audioBuffer.addSamples(samples2);
		audioBuffer.addSamples(samples2);
		int[] samples3 = { 66 };
		audioBuffer.addSamples(samples3);
		audioBuffer.addSamples(samples3);
		audioBuffer.addSamples(samples3);
		audioBuffer.addSamples(samples3);*/
	}
}
