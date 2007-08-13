package realtimesound;

public class AudioBuffer {
	
	private int frameSize;
	private int hopSize;
	private int[] buffer, frame;
	private int headWrite, headRead;
	
	public AudioBuffer(int frameSize, int hopSize) {
		headWrite = 0;
		headRead = 0;
		this.frameSize = frameSize;
		this.hopSize = hopSize;
		buffer = new int[frameSize];
		frame = new int[frameSize];
	}
	
	public void addSamples(/*int[]*/short[] newData) {
		int N = newData.length;
		
		for(int i=0; i<N; i++) {
			buffer[headWrite] = newData[i];
			headWrite++;
			if(headWrite==buffer.length) headWrite=0;
			
			
			// if the number of incoming samples equals to hopsize
			if(headWrite==headRead) {
				//System.out.println("esit");
				for(int n=0; n<frameSize; n++) {
					frame[n] = buffer[(headRead+n)%frameSize];		
				}
				headRead += hopSize;
				if(headRead==buffer.length) headRead=0;
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
		frame = new int[frameSize];
	}
	
	public static void main(String[] args) {
		AudioBuffer audioBuffer = new AudioBuffer(8,2);
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
