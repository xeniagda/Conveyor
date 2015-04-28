package com.loovjo.conveyor;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

public class Logger {
	private String name;

	public Logger(String name) {

		this.name = name;
	}

	public void log(Level level, Object message) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		PrintStream p = System.out;
		if (level == Level.WARNING) {
			p = System.err;
		}
		p.println("[" + dateFormat.format(cal.getTime())
				+ ":" + System.currentTimeMillis() % 1000 + (level == Level.ALL ? "" : " " + level.getName()) + " {" + name
				+ "}]: " + message.toString());
	}

	public void log(Object message) {
		log(Level.ALL, message);
	}
}
