package math;

/*
 * http://www.cs.princeton.edu/introcs/97data/FFT.java.html
 */
public class FFT {
	
	/*	  
	 private void computeRootArray(int N) {
		Complex[] w = new Complex[N/2];
		
		for(int i=0; i<N/2; i++) {
			w[i] = Complex.nthRootOfUnity(i, N);
		}
	}
	*/
	
	public static Complex[] forward(Complex[] x) {
		int N = x.length;		
		
		if( N == 1 ) {
			return new Complex[] { x[0] };
		} else {			
			Complex[] even = new Complex[N/2];
			Complex[] odd = new Complex[N/2];
			
			for(int i=0; i<N/2; i++) {
				even[i]=x[2*i];
				odd[i]=x[2*i+1];
			}
			
			Complex[] left = forward(even);
			Complex[] right = forward(odd);
			
			Complex[] c = new Complex[N];
			for(int n=0; n<N/2; n++) {
				double nth = -2*n*Math.PI/N;
				Complex wn = new Complex(Math.cos(nth), Math.sin(nth));
				c[n] = Complex.add(left[n], Complex.multiply(wn, right[n]));
				c[n+N/2] = Complex.substract(left[n], Complex.multiply(wn, right[n]));				
			}
			return c;			
		}
	}
	
	public static Complex[] inverse(Complex[] c) {
		int N = c.length;
		Complex[] x = new Complex[N];
		
		for(int i=0; i<N; i++) {
			x[i] = Complex.conjugate(c[i]);
		}
		
		x = forward(x);
		
		for(int i=0; i<N; i++) {
			x[i] = Complex.conjugate(x[i]);
			x[i] = Complex.scale(x[i], 1.0/N);
		}
		
		return x;		
	}
}
