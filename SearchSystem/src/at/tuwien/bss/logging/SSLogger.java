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
	private boolean consoleLogging = true;
	
	private SSLogger() {
		
		File logFileDir = new File("log");

		try {
			logFile = new File(logFileDir + "/log.txt");
			if (logFile.exists()) {
				logFile.delete();
			}
			logFileDir.mkdirs();
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
		
		if(consoleLogging)
			System.out.println(s);
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
		    out.println(s);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setConsoleLogging(boolean consoleLogging) {
		this.consoleLogging = consoleLogging;
	}
}
