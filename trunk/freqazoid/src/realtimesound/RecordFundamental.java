package realtimesound;

public class RecordFundamental {
	
	private double[] record;
	private int head;
	
	public RecordFundamental() {
		record = new double[1000];
		head = 0;
	}

	public void add(double freq) {
		record[head] = freq;
		head++;
		if(head == record.length) {
			head = 0;
			record = new double[1000];
		}
	}
	
	public double[] getRecord() {
		return record;
	}
}
