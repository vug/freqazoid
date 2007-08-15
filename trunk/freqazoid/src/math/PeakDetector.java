package math;

import java.util.Vector;

public class PeakDetector {
	
	public static final Peak[] detectSpectralPeaks(double[] point, double threshold) {
		Vector<Peak> peaks = new Vector<Peak>();
		int N = point.length;
		
		
		for(int i=1; i<N-1; i++) {
//			System.out.print(point[i]+" ");
			if( point[i]>point[i-1] && point[i+1]<point[i] && point[i]>threshold){
				peaks.add(new Peak( (i*44100.0)/point.length, point[i]));
			}
		}
//		System.out.print("\n");
		
		//System.out.println(peaks.size());
		peaks.trimToSize();
		Peak[] ret = new Peak[peaks.size()];
		return peaks.toArray(ret);
	}
}
