package realtimesound;

import java.util.Vector;

import math.Complex;
import math.DFT;
import math.FFT;
import math.Peak;
import math.PeakDetector;
import math.Tools;

public class AudioAnalyser {
	
	private AudioBuffer audioBuffer;
	private RecordFundamental recordFundamental;
	
	private int windowSize;
	private int numberOfHops;
	private int windowType;
	
	private double[] windowedFrame;
	private double[] magnitude;
	private Peak[] peaks;
	private double fundamentalFreqency;
	
	private double peakThreshold;
	
	public static final int NO_PEAK = -1;
	
	public AudioAnalyser() {
		windowSize = 2048;
		numberOfHops = 4;
		windowType = DFT.BLACKMANN;
		peakThreshold = 5.0;
		fundamentalFreqency = 0.0;
		
		audioBuffer = new AudioBuffer(windowSize, numberOfHops, this);
		recordFundamental = new RecordFundamental();
	}
	
	protected void bufferReady() {
		windowedFrame = DFT.window( audioBuffer.getFrame(), windowType );
		magnitude = Complex.abs( FFT.forward( Tools.makeComplex(windowedFrame)) );
		peaks = PeakDetector.detectSpectralPeaks( magnitude, peakThreshold );
		fundamentalFreqency = twoWayMismatch( peaks );
//		recordFundamental.add(110*Math.pow(2, (int)(Math.random()*5)));
		recordFundamental.add( fundamentalFreqency );
		
//		for (int i = 0; i < peaks.length; i++) {
//			double peak = peaks[i].frequency;
//			System.out.print(peak+" ");			
//		}
//		System.out.println("");
//		System.out.println(fundamentalFreqency);
	}
	
	private double twoWayMismatch(Peak[] peaks) {
		if(peaks.length > 0) {
			Peak maxPeak = peaks[0];
			for (int i = 0; i < peaks.length; i++) {
				if( peaks[i].amplitude > maxPeak.amplitude ) {
					maxPeak = peaks[i];
				}				
			}
			return peaks[0].frequency;
//			return maxPeak.frequency;
		}
		else {
			return 55;
		}
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
}
