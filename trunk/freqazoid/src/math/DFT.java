package math;

public class DFT {
	
	public static final double[] magnitude(double[] input) {
		int N = input.length;
		double[] mag = new double[N];
		double[] c = new double[N];
		double[] s = new double[N];
		double twoPi = 2*Math.PI;
		
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				c[i] += input[j]*Math.cos(i*j*twoPi/N);
				s[i] -= input[j]*Math.sin(i*j*twoPi/N);
			}
			c[i]/=N;
			s[i]/=N;
			
			mag[i]=Math.sqrt(c[i]*c[i]+s[i]*s[i]);
		}
		
		return mag;
	}
	
	public static final double[] window(double[] input) {
		int N = input.length;
		double[] windowed = new double[N];
		
		for(int i=0; i<N; i++) {
			windowed[i] = 0.5*(1-Math.cos(2*Math.PI*i/N)) * input[i];
		}
		return windowed;
	}
	
	public static void main(String[] args) {
		int N=16;
		double[] x = new double[N];		
		
		for(int i=0; i<N; i++) {
			x[i]+=Math.sin(2*Math.PI*i/N);
			x[i]+=Math.sin(0*2*Math.PI*i/N);
			x[i]+=Math.cos(0*2*Math.PI*i/N);
		}
		
		double mag[] = magnitude(x); 
		
		for(int i=0; i<N; i++) {
			System.out.println(mag[i]);
		}
				
		  
	}
	
	

}
