package com.loovjo.conveyor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

	public static void main(String[] ar) throws Exception {

		HashMap<String, Object> args = ArgumentParser.parseArgs(ar, "fid");
		if (ar.length == 0) {
			System.out
					.println("Usage: java -jar file [-f] <Program> [-i] <Default input buffer> [-d] (debug true/false)");
			System.exit(0);
		}
		if (!(args.get("d") instanceof Boolean))
			args.put("d", false);

		BufferedReader br = new BufferedReader(new FileReader(new File(args
				.get("f").toString())));
		ArrayList<String> lines = new ArrayList<String>();

		int cc = 0;

		for (String line; (line = br.readLine()) != null; lines.add(line)) {
			cc += line.length() + 1;
		}
		System.out.println("Characters: " + cc);

		Vector pos = new Vector(0, 0);
		int currentDir = 2;
		int tryDir = 2;

		boolean stringParsing = false;
		ArrayList<Integer> stack = new ArrayList<Integer>();
		String output = "";
		String input = args.get("i").toString();
		boolean read = false;
		while (true) {

			int longest = 0;
			for (String line : lines)
				longest = Math.max(line.length(), longest);

			for (int l = 0; l < lines.size(); l++) {
				lines.set(l,
						lines.get(l)
								+ rep(" ", longest - lines.get(l).length()));
			}
			char currentCommand = lines.get((int) pos.getY()).charAt(
					(int) pos.getX());

			if (stringParsing && currentCommand != '"') {
				stack.add((int) currentCommand);
			} else {
				switch (currentCommand) {
				case '"':
					stringParsing = !stringParsing;
					break;
				case 'F':
					System.exit(0);
					break;
				case '.':
					char out = (char) (int) removeLast(stack);
					System.out.print(out);
					output += out;
					break;
				case ':':
					stack.add(stack.get(stack.size() - 1));
					break;
				case ',':
					String out2 = ("" + (int) removeLast(stack));
					System.out.print(out2);
					output += out2;
					break;
				case '+':
					stack.add(removeLast(stack) + removeLast(stack));
					break;
				case '*':
					stack.add(removeLast(stack) * removeLast(stack));
					break;
				case '-':
					int f = removeLast(stack);
					stack.add(removeLast(stack) - f);
					break;
				case '/':
					int l = removeLast(stack);
					stack.add(removeLast(stack) / l);
					break;
				case 'x':
					l = removeLast(stack);
					stack.add((int) Math.pow(l, removeLast(stack)));

				case '?':
					stack.add((int) (Math.random() * removeLast(stack)));
					break;
				case 'G':
					int a = removeLast(stack);
					stack.add(stack.get(a));
					stack.remove(a);
					break;
				case '\\':
					int first = removeLast(stack);
					int second = removeLast(stack);
					stack.add(first);
					stack.add(second);
					break;
				case '@':
					first = removeLast(stack);
					second = removeLast(stack);
					int third = removeLast(stack);
					stack.add(first);
					stack.add(second);
					stack.add(third);
					break;
				case 'P':
					removeLast(stack);
					break;
				case 'e':
					stack.add(removeLast(stack) == removeLast(stack) ? 1 : 0);
					break;
				case 'g':
					stack.add(removeLast(stack) < removeLast(stack) ? 1 : 0);
					break;
				case 'l':
					stack.add(removeLast(stack) > removeLast(stack) ? 1 : 0);
					break;
				case '(':
					stack.add(removeLast(stack) - 1);
					break;
				case ')':
					stack.add(removeLast(stack) + 1);
					break;
				case 'S':
					Thread.sleep(removeLast(stack));
					break;
				case 'p':
					int ascii = removeLast(stack);
					int y = removeLast(stack);
					int x = removeLast(stack);
					while (lines.size() <= y)
						lines.add("");
					StringBuilder sb = new StringBuilder(lines.get(y));
					sb.append(rep(" ", x - sb.length() + 1));
					sb.setCharAt(x, (char) ascii);
					lines.set(y, sb.toString());
					break;
				case 'a':
					y = removeLast(stack);
					x = removeLast(stack);
					stack.add((int) lines.get(y).charAt(x));
					break;
				case 'I':
					// Intentional fall-through
				case 'i':
					if (!read && input.length() == 0) {
						Scanner s = new Scanner(System.in);
						input += s.nextLine();
						s.close();
					}
					read = true;
					if (input.length() == 0) {
						stack.add(0);
					} else {
						sb = new StringBuilder(input);
						int idx = sb.length() - 1;
						if (currentCommand == 'I')
							idx = 0;
						stack.add((int) sb.charAt(idx));
						sb.deleteCharAt(idx);
						input = sb.toString();
					}
					break;
				default:
					break;
				}
				if (currentCommand >= '0' && currentCommand <= '9')
					stack.add(currentCommand - '0');
			}
			char currentGround = '=';
			if (pos.getY() + 1 < lines.size())
				currentGround = lines.get((int) pos.getY() + 1).charAt(
						(int) pos.getX());
			switch (currentGround) {
			case '>':
				currentDir = 2;
				break;
			case '<':
				currentDir = 6;
				break;
			case '=':
				currentDir = tryDir;
				break;
			case '#':

				if (getLast(stack) == 0)
					currentDir = 4;
				else
					currentDir = tryDir;
				break;
			case '^':
				currentDir = 0;
				break;
			case 'T':
				pos = pos.moveInDir(currentDir);
				break;
			default:
				currentDir = 4;
			}
			pos = pos.moveInDir(currentDir);
			if (currentDir != 4)
				tryDir = currentDir;

			if ((boolean) args.get("d")) {
				Thread.sleep(100);
				System.out.println();
				System.out.println("Stack:" + stack);
				System.out.print("(String version):");
				for (int element : stack)
					System.out.print((char) element);
				System.out.println();
				System.out.println("Direction: " + currentDir);
				System.out.println("Output: " + output);
				for (int y = 0; y < lines.size(); y++) {
					for (int x = 0; x < lines.get(y).length(); x++) {
						if (new Vector(x, y).getLengthTo(pos) == 0)
							System.out.print("O");
						else
							System.out.print(lines.get(y).charAt(x));
					}
					System.out.println();
				}
			}
		}
	}

	private static int getLast(ArrayList<Integer> stack) {
		int val = 0;
		if (stack.size() > 0)
			val = stack.get(stack.size() - 1);
		return val;
	}

	private static int removeLast(ArrayList<Integer> stack) {
		if (stack.size() == 0)
			return 0;
		int val = stack.get(stack.size() - 1);
		stack.remove((int) stack.size() - 1);
		return val;
	}

	public static String rep(String s, int times) {
		return new String(new char[Math.max(0, times)]).replace("\0", s);
	}
}
