package math;

public class Test {
	
	public static void main(String[] args) {
		//Complex c1 = new Complex(0, 1);
		//System.out.println( Complex.multiply(c1, c1).toString() );
		//Complex w = Complex.nthRootOfUnity(0,4);
		//System.out.println(w.toString());
		int N=8;
		Complex[] c = new Complex[N];
		double[] num = new double[N];
		for(int i=0; i<N; i++) {
			num[i] = i;
			c[i] = new Complex(num[i],0);
		}
		
		Complex[] x = FFT.forward(c);
		double[] x2 = DFT.forwardMagnitude(num);
		//x = FFT.inverse(x);
		
		for(int i=0; i<N; i++) {
			System.out.println( Complex.abs(x[i]) + " ... " + 8*x2[i]);
		}
		
		System.out.println("db: " + Math.log10(0.09*100+1));
	}

}