package math;

public class TwoWayMismatch {
	
	private static double pmP = 0.5;
	private static double pmQ = 1.4;
	private static double pmR = 0.5;
	private static double mpP = 0.5;
	private static double mpQ = 1.4;
	private static double mpR = 0.5;
	private static double rho = 0.33;
	
	private static double[] errors1;
	private static double[] ftrials1;
	
	private static double[] errors2;
	private static double[] ftrials2;
	
	public static double calculateFundamentalFrequency(double f0Min, double f0Max, Peak[] peaks) {
		
		// divide the frequency range to equal tempered semitone steps.
		double log102 = 1/Math.log10(2);		
		int N = (int)Math.floor(12*log102*Math.log10(f0Max/f0Min));
		
		errors1 = new double[N];
		ftrials1 = new double[N];
		
		for (int i = 0; i < N; i++) {
			ftrials1[i] = f0Min*Math.pow(2, (double)i/12);
			errors1[i] = calculateTotalError(ftrials1[i], peaks);
		}
		
		// find the one with minimum error
		double minError = Double.MAX_VALUE;		
		int index = 0;
		for (int i = 0; i < N; i++) {
			if(errors1[i] < minError) {
				minError = errors1[i];
				index = i;
			}
		}
		
		
		/*
		 * second time
		 */
		{
			f0Min = ftrials1[index]*Math.pow(2.0, -1.0/12.0);
			f0Max = ftrials1[index]*Math.pow(2.0, 1.0/12.0);
		
			log102 = 1/Math.log10(2);
//			N = (int)Math.floor(10*log102*Math.log10(f0Max/f0Min));
			N = 20;
		
			errors2 = new double[N];
			ftrials2 = new double[N];
		
			for (int i = 0; i < N; i++) {
//				ftrials[i] = f0Min*Math.pow(2, (double)i/10);
				ftrials2[i] = i*(f0Max - f0Min)/N + f0Min;
				errors2[i] = calculateTotalError(ftrials2[i], peaks);
//				System.out.println(ftrials[i]+ ": " + errors[i] );
			}
		
			minError = Double.MAX_VALUE;		
			index = 0;
			for (int i = 0; i < N; i++) {
				if(errors2[i] < minError) {
					minError = errors2[i];
					index = i;
				}
			}
		}
		
//		System.out.println(index + ", " + ftrials[index]);
		return ftrials2[index];
	}
	
	public static double[] getErrors1() {
		return errors1;
	}
	
	public static double[] getFTrials1() {
		return ftrials1;
	}

	private static double calculateTotalError(double f0Trial, Peak[] peaks) {
		double predictedToMeasuredError = 0.0;
		double measuredToPredictedError = 0.0;
		double totalError = 0.0;
		
		double[] measuredPartials = new double[peaks.length];
		double[] measuredAmplitudes = new double[peaks.length];
		
		// determine maximum measured amplitude and partial frequency
		double ampMax = 0.0;
		double freqMax = 0.0;
		for (int i = 0; i < peaks.length; i++) {
			measuredPartials[i] = peaks[i].frequency;
			measuredAmplitudes[i] = peaks[i].amplitude;
			
			if( measuredAmplitudes[i] > ampMax ) {
				ampMax = measuredPartials[i];
			}
			
			if( measuredPartials[i] > freqMax ) {
				freqMax = measuredPartials[i];
			}
		}		
		
		// calculate predicted frequencies of harmonics
		int nPredictedHarmonics = (int)Math.ceil(freqMax/f0Trial);
		double[] predictedHarmonics = new double[nPredictedHarmonics];
		double[] predictedAmplitudes = new double[nPredictedHarmonics];
		for (int n = 0; n < predictedHarmonics.length; n++) {
			predictedHarmonics[n] = f0Trial*(n+1);	
		}
		
		// predicted to measured
		double[] differences = new double[nPredictedHarmonics];
		for (int n = 0; n < differences.length; n++) {
			differences[n] = Double.MAX_VALUE;
			for (int k = 0; k < measuredPartials.length; k++) {
				double diff = Math.abs( predictedHarmonics[n]-measuredPartials[k]);
				if( diff < differences[n] ) {
					differences[n] = diff;
					predictedAmplitudes[n] = measuredAmplitudes[k];
				}
			}
			
//			System.out.print("pe: ");
//			System.out.print( predictedToMeasuredError(differences[n], predictedHarmonics[n], predictedAmplitudes[n], ampMax)+ "  ");
			predictedToMeasuredError += predictedToMeasuredError(differences[n], predictedHarmonics[n], predictedAmplitudes[n], ampMax);
		}
//		System.out.println( predictedToMeasuredError );
		
		// measured to predicted
		differences = new double[peaks.length];
		for (int k = 0; k < differences.length; k++) {
			differences[k] = Double.MAX_VALUE;
			for (int n = 0; n < predictedHarmonics.length; n++) {
				double diff = Math.abs( predictedHarmonics[n]-peaks[k].frequency );
				if( diff < differences[k] ) {
					differences[k] = diff;					
				}
			}
			measuredToPredictedError += measuredToPredictedError(differences[k], peaks[k].frequency, peaks[k].amplitude, ampMax);
		}
		
		totalError = predictedToMeasuredError/predictedHarmonics.length + rho*measuredToPredictedError/peaks.length;
		
//		System.out.println(predictedToMeasuredError/predictedHarmonics.length + ", " + rho*measuredToPredictedError/peaks.length);
//		System.out.println(totalError);
		return totalError;
	}
	
	private static double predictedToMeasuredError(double deltaFreqN, double freqN, double ampN, double ampMax ) {		
		double error = deltaFreqN*Math.pow(freqN, -pmP)+(ampN/ampMax)*(pmQ*deltaFreqN*Math.pow(freqN, -pmP)-pmR);
		// freqN 0 oldugunda error Infinity oluyor.
//		System.out.println(deltaFreqN + ", " + freqN + ", " + ampN + ", " + ampMax);
//		System.out.println(error);
		return error;
	}
	
	private static double measuredToPredictedError(double deltaFreqK, double freqK, double ampK, double ampMax) {
		double error = deltaFreqK*Math.pow(freqK, -mpP)+(ampK/ampMax)*(mpQ*deltaFreqK*Math.pow(freqK, -mpP)-mpR);
		return error;
	}

	public static double getMpP() {
		return mpP;
	}

	public static void setMpP(double mpP) {
		TwoWayMismatch.mpP = mpP;
	}

	public static double getMpQ() {
		return mpQ;
	}

	public static void setMpQ(double mpQ) {
		TwoWayMismatch.mpQ = mpQ;
	}

	public static double getMpR() {
		return mpR;
	}

	public static void setMpR(double mpR) {
		TwoWayMismatch.mpR = mpR;
	}

	public static double getPmP() {
		return pmP;
	}

	public static void setPmP(double pmP) {
		TwoWayMismatch.pmP = pmP;
	}

	public static double getPmQ() {
		return pmQ;
	}

	public static void setPmQ(double pmQ) {
		TwoWayMismatch.pmQ = pmQ;
	}

	public static double getPmR() {
		return pmR;
	}

	public static void setPmR(double pmR) {
		TwoWayMismatch.pmR = pmR;
	}

	public static double getRho() {
		return rho;
	}

	public static void setRho(double rho) {
		TwoWayMismatch.rho = rho;
	}	
}
