package realtimesound;

import math.Complex;
import math.DFT;
import math.FFT;
import math.Tools;

public class AudioAnalyser {
	
	private AudioBuffer audioBuffer;
	
	private int windowSize;
	private int numberOfHops;
	private int windowType;
	
	private double[] windowedFrame;
	private double[] magnitude;
	
	public AudioAnalyser() {
		windowSize = 2048;
		numberOfHops = 4;
		windowType = DFT.BLACKMANN;
		
		audioBuffer = new AudioBuffer(windowSize, numberOfHops, this);
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

	public int getNumberOfHops() {
		return numberOfHops;
	}

	public void setNumberOfHops(int numberOfHops) {
		this.numberOfHops= numberOfHops;
		audioBuffer = new AudioBuffer(windowSize, numberOfHops, this);
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
		audioBuffer = new AudioBuffer(windowSize, numberOfHops, this);
	}
	
	public void setWindowSizeAndNumberOfHops(int windowSize, int numberOfHops) {
		this.numberOfHops= numberOfHops;
		this.windowSize = windowSize;
		audioBuffer = new AudioBuffer(windowSize, numberOfHops, this);
	}

	public int getWindowType() {
		return windowType;
	}

	public void setWindowType(int windowType) {
		this.windowType = windowType;
	}
}
