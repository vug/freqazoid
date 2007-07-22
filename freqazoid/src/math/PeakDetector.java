package math;

import java.util.Vector;

public class PeakDetector {
	
	public static final Vector<Peak> detect(double[] signal) {
		Vector<Peak> peaks= new Vector<Peak>();
		int N = signal.length;
		
		for(int i=1; i<N-1; i++) {
			if( signal[i]>signal[i-1] && signal[i+1]<signal[i] ){
				peaks.add(new Peak(i, signal[i]));
			}
		}
		
		//System.out.println(peaks.size());
		return peaks;
	}
}
