package realtimesound;

import math.Complex;
import math.DFT;
import math.FFT;
import math.Peak;
import math.PeakDetector;
import math.Tools;
import math.TwoWayMismatch;

public class AudioAnalyser {
	
	public static final int TWM = 0;
	public static final int LEFT_MOST_PEAK = 1;
	public static final int HIGHEST_PEAK = 2;
	private int method;
	
	private AudioBuffer audioBuffer;
	private RecordFundamental recordFundamental;
	
	private int windowSize;
	private int numberOfHops;
	private int windowType;
	
	private double[] windowedFrame;
	private double[] magnitude;
	private Peak[] peaks;
	private double f0Min;
	private double f0Max;
	private double fundamentalFreqency;
	
	private double peakThreshold;
	
	public static final int NO_PEAK = -1;
	
	public AudioAnalyser() {
		windowSize = 2048;
		numberOfHops = 2;
		windowType = DFT.BLACKMANN;
		peakThreshold = 5.0;
		fundamentalFreqency = 0.0;
		f0Min = 220;
		f0Max = 880;		
		
		audioBuffer = new AudioBuffer(windowSize, numberOfHops, this);
		recordFundamental = new RecordFundamental();
	}
	
	protected void bufferReady() {
		windowedFrame = DFT.window( audioBuffer.getFrame(), windowType );
		magnitude = Complex.abs( FFT.forward( Tools.makeComplex(windowedFrame)) );
		peaks = PeakDetector.detectSpectralPeaks( magnitude, peakThreshold );
		
		if( peaks.length > 0) {			
			switch (method) {		
			case TWM:
				fundamentalFreqency = TwoWayMismatch.calculateFundamentalFrequency(f0Min, f0Max, peaks);
				break;
			case LEFT_MOST_PEAK:
				fundamentalFreqency = peaks[0].frequency;
				break;
			case HIGHEST_PEAK:
				Peak maxPeak = peaks[0];
				for (int i = 0; i < peaks.length; i++) {
					if( peaks[i].amplitude > maxPeak.amplitude ) {
						
						maxPeak = peaks[i];
					}				
				}
				fundamentalFreqency = maxPeak.frequency;
				break;
			}
		} else {
			fundamentalFreqency = -1;
		}
		
		recordFundamental.add( fundamentalFreqency );
		
//		for (int i = 0; i < peaks.length; i++) {
//			double peak = peaks[i].frequency;
//			System.out.print(peak+" ");			
//		}
//		System.out.println("");
//		System.out.println(fundamentalFreqency);
	}
	
	public double getFundamentalFrequency() {
		return fundamentalFreqency;
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
	
	public Peak[] getPeaks() {
		return peaks;
	}

	public double getPeakThreshold() {
		return peakThreshold;
	}

	public void setPeakThreshold(double peakThreshold) {
		this.peakThreshold = peakThreshold;
	}

	public RecordFundamental getRecordFundamental() {
		return recordFundamental;
	}

	public double getF0Max() {
		return f0Max;
	}

	public void setF0Max(double f0max) {
		this.f0Max = f0max;
	}

	public double getF0Min() {
		return f0Min;
	}

	public void setF0Min(double f0min) {
		this.f0Min = f0min;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}
	
}
