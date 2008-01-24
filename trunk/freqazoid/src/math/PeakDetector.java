package math;

import java.util.Vector;

public class PeakDetector {
	/*
	 * tum pointlerin uzerinden ucer ucer gecerek peak'lerin tepelerini tespit et.
	 */
	
	private static double peakThreshold = 0.5;
	
	private static double peakThresholdStiffness = 4.0;
	
	private static int nMaxPeaks = 10;
	
	public static final Peak[] detectSpectralPeaks(double[] point, double threshold) {
		Vector<Peak> peaks = new Vector<Peak>();
		int N = point.length;
		
		double[] pointDB = new double[N];
		
		
		for (int i = 0; i < point.length; i++) {
			pointDB[i] = Tools.lin2dB(point[i]);
		}
		
		
		for(int i=1; i<N-1; i++) {
			double freq = (i*22050.0)/point.length;
			if( pointDB[i]>pointDB[i-1] && pointDB[i+1]<pointDB[i]&& pointDB[i]>threshold(freq)
					&& peaks.size() < nMaxPeaks ){
				peaks.add(new Peak( freq, point[i]));
				
//				double p = 0.5*Math.log(point[i-1]*point[i+1])/Math.log(point[i-1]*point[i+1]/(point[i]*point[i]));
//				peaks.add(new Peak( (i+p)*22050.0/point.length ,point[i]));
			}
		}

		peaks.trimToSize();
		Peak[] ret = new Peak[peaks.size()];
		return peaks.toArray(ret);
	}
	
	public static double getPeakThreshold() {
		return peakThreshold;
	}

	public static void setPeakThreshold(double threshold) {
		peakThreshold = threshold;
	}
	
	public static double getPeakThresholdStiffness() {
		return peakThresholdStiffness;
	}

	public static void setPeakThresholdStiffness(double peakThresholdStiffness) {
		PeakDetector.peakThresholdStiffness = peakThresholdStiffness;
	}

	public static final double threshold(double frequency) {
		return peakThreshold*Math.exp(-peakThresholdStiffness*frequency/22050)+peakThreshold*0.1;
	}

	public static int getNMaxPeaks() {
		return nMaxPeaks;
	}

	public static void setNMaxPeaks(int maxPeaks) {
		nMaxPeaks = maxPeaks;
	}
}