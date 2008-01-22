package math;

import java.util.Vector;

public class PeakDetector {
	/*
	 * tum pointlerin uzerinden ucer ucer gecerek peak'lerin tepelerini tespit et.
	 */
	
	public static final Peak[] detectSpectralPeaks(double[] point, double threshold) {
		Vector<Peak> peaks = new Vector<Peak>();
		int N = point.length;
		
		for (int i = 0; i < point.length; i++) {
			point[i] = Tools.lin2dB(point[i]);
		}
		
		
		for(int i=1; i<N-1; i++) {
			if( point[i]>point[i-1] && point[i+1]<point[i]&& point[i]>threshold ){
				peaks.add(new Peak( (i*22050.0)/point.length, point[i]));
			}
		}
		
		//System.out.println(peaks.size());
		peaks.trimToSize();
		Peak[] ret = new Peak[peaks.size()];
		return peaks.toArray(ret);
	}
}
