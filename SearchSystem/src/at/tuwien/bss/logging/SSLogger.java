package at.tuwien.bss.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SSLogger {
	
	private File logFile;
	
	private SSLogger() {

		try {
			logFile = new File("log/log.txt");
			if (logFile.exists()) {
				logFile.delete();
			}
			logFile.createNewFile();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static SSLogger instance;
	public static SSLogger getLogger() {
		
		if(instance == null) {
			instance = new SSLogger();
		}
		
		return instance;
	}
	

	public void log(String message) {
		write(message);
	}
	
	private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
	public void logTime(String message) {
		write(df.format(new Date().getTime())+": "+ message);
	}
	
	private void write(String s) {
		System.out.println(s);
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
		    out.println(s);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
