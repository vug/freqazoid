package realtimesound;

import math.Complex;
import math.DFT;
import math.FFT;
import math.Tools;

public class AudioAnalyser {
	
	private AudioBuffer audioBuffer;
	
	private int windowSize;
	private int hopSize;
	private int windowType;
	
	private double[] windowedFrame;
	private double[] magnitude;
	
	public AudioAnalyser() {
		windowSize = 2048;
		hopSize = 512;
		windowType = DFT.BLACKMANN;
		
		audioBuffer = new AudioBuffer(windowSize, hopSize, this);
	}
	
	protected void bufferReady() {
		windowedFrame = DFT.window( audioBuffer.getFrame(), windowType );
		magnitude = Complex.abs( FFT.forward( Tools.makeComplex(windowedFrame)) );		
	}
	
	public void addSamples(int[] newSamples) {
		audioBuffer.addSamples(newSamples);
	}
	
	public double[] getMagnitudeSpectrum() {
		return magnitude;
	}
	
	public double[] getCurrentFrame() {
		return audioBuffer.getFrame();
	}

	public int getHopSize() {
		return hopSize;
	}

	public void setHopSize(int hopSize) {
		this.hopSize = hopSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public int getWindowType() {
		return windowType;
	}

	public void setWindowType(int windowType) {
		this.windowType = windowType;
	}
	
	

}
