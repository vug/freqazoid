package math;

public class Tools {
	
	public static final double TWO_PI = 2*Math.PI;
	
	public static final double LOG_OF_2_BASE_10 = 1/Math.log10(2);
	
	public static final String pitchName(int n) {
		String pitch = "O";
		switch(n) {
		case 0: pitch="C"; break;
		case 1: pitch="C#"; break;
		case 2: pitch="D"; break;
		case 3: pitch="D#"; break;
		case 4: pitch="E"; break;
		case 5: pitch="F"; break;
		case 6: pitch="F#"; break;
		case 7: pitch="G"; break;
		case 8: pitch="G#"; break;
		case 9: pitch="A"; break;
		case 10: pitch="A#"; break;
		case 11: pitch="B"; break;
		}
		return pitch;
	}
	
	public static double log2(double x) {
		return Math.log10(x)/Math.log10(2.0);
	}	

	public static final double lin2dB2(double lin) {
		return 20*Math.log10(lin*1000);
	}
	
	public static final double lin2dB(double lin) {
		return Math.log10(lin+1);
	}
	
	public static double yScale = 100;
	public static final double m2vMagnitude(double mag) {
		return yScale*lin2dB(mag);
	}
	
	public static final double[] lowpass(double[] signal, int nPoints) {
		int N = signal.length;
		double[] ret = new double[N];
		
		for(int i=0; i<nPoints/2; i++) {
			ret[i] = signal[i];
		}
		for(int i=nPoints/2; i<N-nPoints/2; i++) {
			for(int j=0; j<nPoints; j++) {
				ret[i]=0;
				ret[i]+=signal[i-nPoints/2+j];
				ret[i]/=nPoints;
			}	
		}
		for(int i=N-nPoints/2; i<N; i++) {
			ret[i]=signal[i];
		}
		
		return ret;
	}
	
	public static final double[] addArrays(double[] x, double[] y) {
		double[] sum = new double[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = x[i] + y[i];
		}
		
		return sum;
	}
	
	public static final Complex[] addArrays(Complex[] x, Complex[] y) {
		Complex[] sum = new Complex[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = Complex.add(x[i], y[i]);
		}
		
		return sum;
	}
	
	public static final Complex[] substractArrays(Complex[] x, Complex[] y) {
		Complex[] sum = new Complex[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = Complex.substract(x[i], y[i]);
		}
		
		return sum;
	}
	
	public static final double[] dotProduct(double[] x, double[] y) {
		double[] sum = new double[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = x[i] * y[i];
		}
		
		return sum;
	}
	
	public static final Complex[] dotProduct(Complex[] x, Complex[] y) {
		Complex[] sum = new Complex[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = Complex.multiply(x[i], y[i]);
		}
		
		return sum;
	}
	
	public static Complex[] makeComplex(double[] x) {
		int N = x.length;
		Complex[] c = new Complex[N];
		for(int i=0; i<N; i++) {
			c[i] = new Complex(x[i],0);
		}
		return c;
	}
	
	public static void printArray(double[] arr) {
		for (double d : arr) {
			System.out.format("%.4f ", d);
		}
		System.out.println();
	}
}
