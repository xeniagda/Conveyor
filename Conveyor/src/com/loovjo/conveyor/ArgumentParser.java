package com.loovjo.conveyor;

import java.util.Arrays;
import java.util.HashMap;

public class ArgumentParser {

	public static HashMap<String, Object> parseArgs(String args[],
			String defaultOrder) {
		HashMap<String, Object> parsed = new HashMap<String, Object>();
		for (char c : defaultOrder.toCharArray())
			parsed.put(""+c, "");
		String option = null;
		boolean parsedNonDefault = false;
		for (String arg : args) {
			if (arg.matches("-.")) {
				option = arg.substring(1, 2);
			} else {
				boolean nulledOption = false;
				if (option == null) {
					if (parsedNonDefault)
						throw new IllegalArgumentException(
								"Default argument after - sign!");
					option = defaultOrder.charAt(Arrays.asList(args).indexOf(
							arg))
							+ "";
					nulledOption = true;
				}
				Object a = arg;
				try {
					a = Float.parseFloat(arg);
				} catch (NumberFormatException e) {
				}
				try {
					a = Integer.parseInt(arg);
				} catch (NumberFormatException e) {
				}
				if (arg.equals("true") || arg.equals("false"))
					a = new Boolean(arg.equals("true"));
				parsed.put(option, a);
				if (nulledOption)
					option = null;
			}
		}

		return parsed;
	}
}
