package realtimesound;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public class RecordFundamental {
	
	private double[] record;
	private int head;
	private File analysisFile;
	private PrintWriter out;
	private double freqPrev;
	private double t,ti, tf;
	private AudioAnalyser audioAnalyser;
	private int nPoints = 500;
	
	public RecordFundamental(AudioAnalyser aa) {
		record = new double[nPoints];
		head = 0;
		this.audioAnalyser = aa;
		
		freqPrev=AudioAnalyser.NO_FUNDAMENTAL;
		t  = 0.0;
		ti = 0.0;
		tf = 0.0;
		
		Calendar cal = Calendar.getInstance();
		String date = cal.getTime().toString();
		
		analysisFile = new File("analysis.txt");
		try {
			out = new PrintWriter(new FileWriter(analysisFile));
			out.println(date);
			out.println();
			out.println("'Note Start' and 'Note End' values are in seconds");
			out.println("'F_0' is in Hz");
			out.println();
			out.format("%10s %10s %10s %n","note start","note end","F_0");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public void add(double freq) {
		/*
		 * Control whether a note is started or ended
		 * case i) Fprev was NO_FUND Fnow is a freq
		 *         which means a new note is started.
		 */
		if( freqPrev == AudioAnalyser.NO_FUNDAMENTAL && freq != AudioAnalyser.NO_FUNDAMENTAL ) {
			ti = t;
			freqPrev = freq;
		}
		/*
		 * case ii) Fnow is not so different than Fprev that their ratio is less than one semitone
		 *          which means Note is continuing: do nothing
		 *          
		 * case iii) The ratio of Fnow and Fprev is more than one semitone
		 *           (and Fnow is not NO_FUND)
		 *           which means:
		 *           Fprev is ended and a new note is started.
		 */
		if( 
			freq != AudioAnalyser.NO_FUNDAMENTAL && 
			( freq>freqPrev*Math.pow(2.0, 1.0/12.0) || freq<freqPrev*Math.pow(2.0, -1.0/12.0) )
		   ) {
				tf = t;
				
				out.format("%10.3f %10.3f %10.1f %n",ti,tf,freq);
//				System.out.println(ti + " - " + tf + ": " + freq);
//				System.out.format("%10.3f %10.3f %10.1f %n",ti,tf,freq);
				
				freqPrev = freq;
				ti=tf;		
		} 
		/*
		 * case iv) Fprev was not NO_FUND but Fnow is NO_FUND
		 *          which means
		 *          the note is ended
		 */
		if ( freqPrev != AudioAnalyser.NO_FUNDAMENTAL && freq == AudioAnalyser.NO_FUNDAMENTAL ) {
			tf = t;
			out.format("%10.3f %10.3f %10.1f %n",ti,tf,freqPrev);
//			System.out.println(ti + " - " + tf + ": " + freqPrev);
//			System.out.format("%10.3f %10.3f %10.1f %n",ti,tf,freqPrev);
			freqPrev = AudioAnalyser.NO_FUNDAMENTAL;
		}
		
		// increase the time by one hop size in seconds		
		t += (double)audioAnalyser.getWindowSize()/44100/audioAnalyser.getNumberOfHops();
		
		record[head] = freq;
		head++;
		if(head == record.length) {
			reset();
		}
	}
	
	public double[] getRecord() {
		return record;
	}
	
	public void closeFile() {
		if(out!=null) out.close();
	}
	
	public int getHeadPoisition() {
		return head;
	}
	
	public void setDuration(double duration) {
		nPoints = (int)(duration/((double)audioAnalyser.getWindowSize()/44100/audioAnalyser.getNumberOfHops()));		
		reset();
	}
	
	public void reset() {
		record = new double[nPoints];
		head = 0;
	}
	
	public double getDuration() {
		return nPoints*((double)audioAnalyser.getWindowSize()/44100)/audioAnalyser.getNumberOfHops();
	}
}
