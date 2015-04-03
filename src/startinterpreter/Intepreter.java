package startinterpreter;

//read me:
// i have included all characters to be included in atomic ccharacteers
//can not have dot infront character since we can't b sure wheether it s user intended or not

//import Node;
/*
 * Author: Winfred Jebasingh
 */
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Intepreter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "";
		String input = "";
		boolean breakflag = false;
		System.out.println("Starting Lisp...... ");
		Scanner a = new Scanner(System.in);
		while (true) {
			while (true) {
				s = a.nextLine();
				if (s.trim().equals("$$")) {
					breakflag = true;
					break;
				}
				if (s.trim().equals("$")) {
					s = "";
					System.out.print("");
					scannerfunction(input.trim());
					System.out.println();
					input = "";
					break;
				}
				input = input + s;
			}
			if (breakflag) {
				scannerfunction(input.trim());
				break;
			}
		}
		System.out.println(" Exiting application... ");

	}

	public static void scannerfunction(String input) {
		// TODO Auto-generated method stub
		if (input.length() == 0) {
			System.out.println(" No input found ");
			return;
		}

		String[] resultOnetemp = splitIt(input);

		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < resultOnetemp.length; i++) {
			if (resultOnetemp[i].substring(resultOnetemp[i].length() - 1)
					.equals(".") && resultOnetemp[i].length() > 1) {
				temp.add(resultOnetemp[i].substring(0,
						resultOnetemp[i].length() - 1));
				temp.add(".");
			} else {
				temp.add(resultOnetemp[i]);
			}

		}
		String[] resultOne = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			resultOne[i] = temp.get(i);
		}

		boolean check = true;

		if (resultOne.length == 1) {
			check = isAtomic(resultOne[0]);
		}
		boolean check1 = true;
		for (int i = 0; i < resultOne.length; i++) {
			if (!validate(resultOne[i])) {
				check1 = false;
				break;
			}
		}
		boolean check2 = errorchecker(resultOne);

		InterpretIt inter = new InterpretIt();
		if (check && check1 && check2) {
			inter.mainengine(resultOne);
			resultOne = null;
			return;
		} else {
			resultOne = null;
			return;
		}
	}

	public static boolean isAtomic(String input) {
		input = input.toUpperCase();
		if (input.matches("[+-]?\\d{1,10}$") || input.matches("[A-Z0-9]{1,10}")
				|| input.matches("[A-Z]{1,10}")) {
			return true;
		}
		{
			System.out.print("Error: Non Atomic code found ");
			return false;
		}
	}

	public static boolean errorchecker(String[] resultOne) {
		int counter = 0;
		for (int i = 0; i < resultOne.length; i++) {
			if (resultOne[i].equals("(")) {
				counter = counter + 1;
			}
			if (resultOne[i].equals(")")) {
				counter = counter - 1;
			}
			if (resultOne[i].substring(0, 1).equals(".")) {
				if (resultOne[i].length() > 1) {
					if (resultOne[i].substring(1, 2).equals(".")) {
						System.out.println("Error: continuous Dots are found ");
						return false;
					}
				}
			}
		}
		if (counter > 0) {
			System.out.println("Error: Left paran is more than right paran ");
			return false;
		} else if (counter < 0) {
			System.out.println("Error: There are more closing paran ");
			return false;
		}

		int dots = 0;
		Stack<String> s = new Stack<String>();
		for (int i = 0; i < resultOne.length; i++) {
			if (resultOne[i].equals("(") || resultOne[i].equals(".")) {
				s.push(resultOne[i]);
			}
			if (s.isEmpty() && resultOne[i].equals(")")) {
				System.out.println("Error: wrong set of parans ");
				return false;
			}
			if (resultOne[i].equals(")")) {
				while (true) {
					if (s.peek().equals("(")) {
						s.pop();
						break;
					} else if (s.peek().equals(".")) {
						s.pop();
						dots = dots + 1;
					} else {
						System.out.println("Error: wrong paranthesis ");
						return false;
					}
				}
				if (dots > 1) {
					System.out.println("Error: in DOT notation ");
					return false;
				} else
					dots = 0;
			}
		}
		return true;
	}

	public static boolean validate(String input) {
		if (input.matches("[^A-Z0-9.()]*")) {
			System.out.println("Error: Invalid Character  " + input);
			return false;
		}

		if (input.matches("[+-]?\\d{1,10}$") || input.matches("[A-Z0-9]{1,10}")
				|| input.matches("[A-Z]{1,10}") || input.equals(".")
				|| input.equals("(") || input.equals(")")) {
			return true;
		}
		{
			System.out.print("Error: Invalid character found ");
			return false;
		}
	}

	public static String[] splitIt(String input) {
		ArrayList<String> finalresult = new ArrayList<String>();
		String[] result = input.split("\\s+");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim();
		}
		for (int i = 0; i < result.length; i++) {
			if (result[i].contains("(") || result[i].contains(")")) {
				char[] s = result[i].toCharArray();
				int ptr = 0;
				String buffer = "";
				for (int j = 0; j < s.length; j++) {
					if (s[j] == '(' || s[j] == ')') {
						if (!buffer.isEmpty()) {
							finalresult.add(buffer);
							buffer = "";
						}
						if (s[j] == '(')
							finalresult.add("(");
						if (s[j] == ')')
							finalresult.add(")");
					} else {
						buffer = buffer + s[j];
					}
				}
				if (!buffer.isEmpty()) {
					finalresult.add(buffer);
				}
			}

			else {
				finalresult.add(result[i]);
			}
		}
		String[] output = new String[finalresult.size()];
		for (int i = 0; i < finalresult.size(); i++) {
			output[i] = finalresult.get(i).toUpperCase();
		}
		return output;
	}

}

class InterpretIt {

	public static int ptr = 0;
	public static boolean flag = true;
	public static final int max_allowed = 100000;

	public void mainengine(String[] result) {
		// TODO Auto-generated method stub
		// String ip ="( ( a b c d) ( d e f ) ( ( g h ) ( i j ) ) )";
		// String ip="( ( ( ) ) )";
		// String[] result = ip.split("\\s+");
		// Validation validate = new Validation();
		int n = result.length;
		Node root = new Node("");
		boolean stop = true;
		stop = ParseE(root, result, n - 1, true);

		try {
			if (stop) {
				Evaluator evaluator = new Evaluator();
				Node result1;
				result1 = evaluator.InterpreterEngine(root);
				if (result1 == null) {
					System.out.println("NIL");
				} else {
					// System.out.println();
					print(result1);
					System.out.println("");
				}
			}
		} catch (Exit_exception e) {
			// TODO Auto-generated catch block

		}

		ptr = 0;
		result = null;
		return;
	}

	public static boolean ParseE(Node curr, String[] input, int n, boolean stop) {

		if (ptr > n) {
			flag = false;
		}

		if (flag) {
			if (input[ptr].equals("(")) {
				curr.data = "";
				ptr++;
				stop = ParseX(curr, input, n, stop);

			} else if (input[ptr].matches("[+-]?\\d{1,10}$")
					|| input[ptr].matches("[A-Z0-9]{1,10}")
					|| input[ptr].matches("[A-Z]{1,10}")) {
				curr.data = input[ptr];
				ptr++;
			} else {
				System.out.println("syntax error ");
				ptr = n + max_allowed;
				stop = false;
				return stop;
			}
		}
		return stop;

	}

	public static boolean ParseX(Node curr, String[] input, int n, boolean stop) {

		if (ptr > n) {
			flag = false;
		}
		if (flag) {
			if (input[ptr].equals(")")) {
				curr.data = "NIL";
				ptr++;

			} else {
				if (ptr > n) {
					flag = false;
				}

				if (flag) {
					curr.left = new Node("");
					stop = ParseE(curr.left, input, n, stop);
				}

				if (ptr > n) {
					flag = false;
				}

				if (flag) {
					curr.right = new Node("");
					stop = ParseY(curr.right, input, n, stop);
				}
			}
		}
		return stop;

	}

	public static boolean ParseY(Node curr, String[] input, int n, boolean stop) {

		if (ptr > n) {
			flag = false;
		}
		if (flag) {
			if (input[ptr].equals(".")) {
				ptr++;
				stop = ParseE(curr, input, n, stop);
				if (ptr > n) {
					flag = false;
				}
				if (flag) {
					if (input[ptr].equals(")")) {
						ptr++;

					} else {
						System.out.println("Syntax error ");
						ptr = n + max_allowed;
						stop = false;
						return stop;
					}
				}
			} else {

				stop = ParseR(curr, input, n, stop);
				if (ptr > n) {
					flag = false;
				}
				if (flag) {
					if (input[ptr].equals(")")) {
						ptr++;

					} else {
						System.out.println("Syntax error ");
						ptr = n + max_allowed;
						stop = false;
						return stop;
					}
				}
			}
		}
		return stop;

	}

	public static boolean ParseR(Node curr, String[] input, int n, boolean stop) {

		if (ptr > n) {
			flag = false;
		}
		if (flag) {
			if ((input[ptr].matches("[+-]?\\d{1,10}$")
					|| input[ptr].matches("[A-Z0-9]{1,10}") || input[ptr]
						.matches("[A-Z]{1,10}")) || input[ptr].equals("(")) {
				if (ptr > n) {
					flag = false;
				}
				if (flag) {
					curr.left = new Node("");
					stop = ParseE(curr.left, input, n, stop);
				}
				if (ptr > n) {
					flag = false;
				}
				if (flag) {
					curr.right = new Node("");
					stop = ParseR(curr.right, input, n, stop);
				}
			} else {
				curr.data = "NIL";

			}
			
		}
		return stop;

	}

	public static void print(Node curr) {

		if (curr.left == null && curr.right == null) {
			if (curr.data != "") {
				if (!curr.data.equals("NIL")) {
					System.out.print(curr.data);
					// System.out.print(". ");

				} else {
					System.out.print(curr.data);
				}

			}
			return;
		}
		if (curr.data == "") {
			System.out.print("( ");
		}

		print(curr.left);

		System.out.print(" . ");

		if (curr.left.data == "" && curr.right.data == ""
				&& curr.right.left == null && curr.right.left == null) {
			System.out.print(" NIL");
		} else if (curr.left.data != "" && curr.right.data == ""
				&& curr.right.left == null && curr.right.left == null) {
			System.out.print(" NIL");
		}

		print(curr.right);

		System.out.print(" )");
	}

	public static boolean printextended(Node curr, boolean left) {

		if (left) {
			if (curr.data.equals("$") && curr.left == null
					&& curr.right == null) {
				System.out.print("NIL");

			}
		}

		if (curr.left == null && curr.right == null) {
			if (curr.data != "") {
				if (!curr.data.equals("NIL")) {
					System.out.print(curr.data);
					System.out.print(". ");

				} else {
					System.out.print(curr.data);
				}

			}
			return left;
		}
		if (curr.data == "") {
			System.out.print("(");
		}

		left = printextended(curr.left, true);

		if (curr.left.data == "" && curr.right.data == ""
				&& curr.right.left == null && curr.right.left == null) {
			System.out.print(". NIL");
		} else if (curr.left.data != "" && curr.right.data == ""
				&& curr.right.left == null && curr.right.left == null) {
			System.out.print(" NIL");
		}

		left = printextended(curr.right, false);

		System.out.print(")");
		return left;
	}

}

class Node {

	String data;
	Node right;
	Node left;
	public boolean isList;

	public Node(String inp) {
		this.data = inp;
		right = null;
		left = null;
	}
}
