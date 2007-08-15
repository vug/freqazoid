package realtimesound;

public class RecordFundamental {
	
	private double[] record;
	private double[] recordDebug;
	private int head;
	
	public RecordFundamental() {
		record = new double[1000];
		recordDebug = new double[1000];
		for (int i = 0; i < recordDebug.length; i++) {
//			recordDebug[i] = i*(440.0-55.0)/1000.0+55;
			recordDebug[i] = 110*Math.pow(2, Math.floor(24.0*i/1000.0)/12);
		}
		head = 0;
	}

	public void add(double freq) {
		record[head] = freq;
		head++;
		if(head == record.length) {
			head = 0;
		}
	}
	
	public double[] getRecord() {
		return record;
	}
}
