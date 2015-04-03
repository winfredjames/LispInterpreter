package startinterpreter;

import java.util.HashSet;

class Exit_exception extends Exception {

	private static final long serialVersionUID = 1L;

	// Parameterless Constructor
	public Exit_exception() {
	}

	// Constructor that accepts a message
	public Exit_exception(String message) {
		super(message);
	}
}

class GlobalDList {
	public static Node dList = new Node("NIL");

	// getter setter
	public static Node getdList() {
		return dList;
	}

	public static void setdList(Node dList) {
		GlobalDList.dList = dList;
	}
}

public class Evaluator {
	public Node tree;

	public Evaluator() {
	}

	public Evaluator(Node tree) {
		this.tree = tree;
	}

	public Node InterpreterEngine(Node tree) throws Exit_exception {
		return eval(tree, new Node("NIL"), GlobalDList.getdList());
	}

	public Node eval(Node tree, Node aList, Node dList) throws Exit_exception {

		Node output = null;
		if (atom(tree)) { // If it is an atom.
			if (tree.data.equalsIgnoreCase("T") || isNull(tree) || isInt(tree)) {
				output = tree;
			} else if (bound(tree, aList)) {
				output = getval(tree, aList);
			} else {
				System.out.println("ERROR: Unbound variable.");
				throw new Exit_exception();
			}
		} else { // if it is a list
			int numofparams = numofparam(cdr(tree));
			if (car(tree).data.equalsIgnoreCase("QUOTE")) {
				if (numofparams != 1) {
					System.out.println("Error in QUOTE operation.");
					throw new Exit_exception();
				}
				output = car(cdr(tree));
			} else if (car(tree).data.equalsIgnoreCase("COND")) {
				output = evcon(cdr(tree), aList, dList);
			} else if (car(tree).data.equalsIgnoreCase("DEFUN")) {
				if (numofparams != 3) {
					System.out
							.println("Error in number of parameters in Defun");
					throw new Exit_exception();
				}
				if (userdefinedfunctions(cdr(tree))) {
					GlobalDList.setdList(cons(
							cons(car(cdr(tree)),
									cons(car(cdr(cdr(tree))),
											car(cdr(cdr(cdr(tree)))))),
							GlobalDList.getdList()));
					output = car(cdr(tree));
				} else {
					System.out.println("Error in user defined functions");
					throw new Exit_exception();
				}
			} else {
				output = apply(car(tree), evlist(cdr(tree), aList, dList),
						aList, dList);
			}
		}
		return output;
	}

	public Node evlist(Node tree, Node aList, Node dList) throws Exit_exception {
		if (isNull(tree)) {
			return new Node("NIL");
		} else {
			return cons(eval(car(tree), aList, dList),
					evlist(cdr(tree), aList, dList));
		}
	}

	public static Node car(Node tree) throws Exit_exception {
		Node output = tree;
		if (tree != null) {
			if (atom(tree)) {
				System.out.println("Error in performing car operation");
				throw new Exit_exception();
			} else {
				if (tree.left != null) {
					output = tree.left;
				} else {
					System.out.println("Error in performing car operation");
					throw new Exit_exception();
				}
			}
		}
		return output;
	}

	public static Node cons(Node left, Node right) throws Exit_exception {
		Node root = new Node("");
		if (left == null && right == null) {
			System.out.println("Error in performing cons operation");
			throw new Exit_exception();
		} else if (left == null) {
			root = right;
		} else if (right == null) {
			root = left;
		} else {
			root.left = left;
			root.right = right;
		}
		return root;
	}

	public static Node cdr(Node tree) throws Exit_exception {
		Node output = tree;
		if (tree != null) {
			if (atom(tree)) {
				System.out.println("Error in performing cdr operation ");
				throw new Exit_exception();
			} else {
				if (tree.right != null) {
					output = tree.right;
				} else {
					System.out.println("Error in performing cdr operation ");
					throw new Exit_exception();
				}
			}
		}
		return output;
	}

	public static boolean atom(Node tree) {
		boolean isAtom = false;
		if (tree != null && tree.data.matches("[+-]?\\d{1,10}$")
				|| tree.data.matches("[A-Z]{1,10}")) {
			isAtom = true;
		}
		return isAtom;
	}

	public static boolean isInt(Node tree) {
		boolean isInt = false;
		try {
			if (atom(tree)) {
				int t = Integer.parseInt(tree.data);
				isInt = true;
			}
		} catch (java.lang.NumberFormatException e) {
			isInt = false;
		}
		return isInt;
	}

	public static boolean isNull(Node tree) {
		boolean isNull = false;
		if (atom(tree) && tree.data.equalsIgnoreCase("NIL")) {
			isNull = true;
		}
		return isNull;
	}

	public static boolean bound(Node tree, Node aList) throws Exit_exception {
		boolean bound = false;
		if (atom(tree)) {
			if (isNull(aList)) {
				return false;
			} else if (eq(tree, car(car(aList)))) {
				return true;
			} else {
				return bound(tree, cdr(aList));
			}
		} else {
			System.out.println("Error in performing bound for the list ");
			throw new Exit_exception();
		}

	}

	public static boolean eq(Node left, Node right) throws Exit_exception {
		boolean equal = false;
		if (atom(left) && atom(right)) {

			if (isInt(left) && isInt(right)) {
				if (Integer.parseInt(left.data) == Integer.parseInt(right.data)) {
					equal = true;
				}
			} else if (left.data.equalsIgnoreCase(right.data)) {
				equal = true;
			}
		} else {
			System.out.println("Error: Illegal atoms for eq ");
			throw new Exit_exception();
		}
		return equal;
	}

	public static Node getval(Node tree, Node aList) throws Exit_exception {
		if (atom(tree)) {

			if (eq(tree, car(car(aList)))) {

				return cdr(car(aList));
			} else {
				return getval(tree, cdr(aList));
			}

		} else {
			System.out.println("Error: getval can be performed only on atoms ");
			throw new Exit_exception();
		}
	}

	public static int numofparam(Node tree) {
		int num = 0;
		if (atom(tree)) {
			return 0;
		}
		while (tree != null) {
			if (tree.left != null) {
				num++;
			}
			tree = tree.right;
		}
		return num;
	}

	public Node evcon(Node tree, Node aList, Node dList) throws Exit_exception {
		if (isNull(tree)) {
			System.out.println("Error: Expression cannot be null ");
			throw new Exit_exception();
		} else if (eval(car(car(tree)), aList, dList).data
				.equalsIgnoreCase("T")) {
			return eval(car(cdr(car(tree))), aList, dList);
		} else {
			return evcon(cdr(tree), aList, dList);
		}
	}

	public static boolean userdefinedfunctions(Node tree) throws Exit_exception {
		boolean output = true;
		if (atom(car(tree))) {
			if (isInt(car(tree))) {
				System.out.println("Error in function name ");
				throw new Exit_exception();
			}
			String functionName = car(tree).data;
			String[] builtInFunctions = new String[] { "CAR", "CDR", "CONS",
					"ATOM", "EQ", "NULL", "INT", "PLUS", "MINUS", "TIMES",
					"QUOTIENT", "REMAINDER", "LESS", "GREATER", "COND",
					"QUOTE", "DEFUN", };
			for (String builtInFunction : builtInFunctions) {
				if (functionName.equalsIgnoreCase(builtInFunction)) {
					System.out.println("Error in function name ");
					throw new Exit_exception();
				}
			}
			Node params = car(cdr(tree));
			Node tempParams = params;
			HashSet<String> paramList = new HashSet<String>();
			int count = 0;
			while (!isNull(tempParams)) {
				Node tmp = car(tempParams);
				if (atom(tmp)) {
					if (isInt(tmp)) {
						System.out
								.println("Error: parameter cannot be an integer ");
						throw new Exit_exception();
					}
					String paramName = tmp.data;
					if (paramName.equalsIgnoreCase("T")
							|| paramName.equalsIgnoreCase("NIL")) {
						System.out
								.println("Error: parameter cannot be T or NIL ");
						throw new Exit_exception();
					}
					paramList.add(paramName);
					count++;
				} else {
					System.out.println("Error: parameter cannot be list ");
					throw new Exit_exception();
				}
				tempParams = cdr(tempParams);
			}
			if (count != paramList.size()) {
				System.out
						.println("Error: parameter list contains duplicate value ");
				throw new Exit_exception();
			}
			Node body = car(cdr(cdr(tree)));

		} else {
			System.out.println("Error: function name cannot be list ");
			throw new Exit_exception();
		}
		return output;
	}

	public Node apply(Node tree, Node params, Node aList, Node dList)
			throws Exit_exception {

		if (atom(tree)) {
			int numofparams = numofparam(params);
			if (tree.data.equalsIgnoreCase("CAR")) {
				if (numofparams != 1) {
					System.out
							.println("ERROR: Only one parameter is expected for CAR operation. ");
					throw new Exit_exception();
				}
				return car(car(params));
			} else if (tree.data.equalsIgnoreCase("CDR")) {
				if (numofparams != 1) {
					System.out
							.println("ERROR: Only one parameter is expected for CDR operation. ");
					throw new Exit_exception();
				}
				return cdr(car(params));
			} else if (tree.data.equalsIgnoreCase("CONS")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters required for CONS operation. ");
					throw new Exit_exception();
				}
				return cons(car(params), car(cdr(params)));
			} else if (tree.data.equalsIgnoreCase("ATOM")) {
				if (numofparams != 1) {
					System.out
							.println("ERROR: Only one parameter is expected for ATOM operation. ");
					throw new Exit_exception();
				}
			} else if (tree.data.equalsIgnoreCase("EQ")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters required for EQ operation. ");
					throw new Exit_exception();
				}
				if (eq(car(params), car(cdr(params)))) {
					return new Node("T");
				} else {
					return new Node("NIL");
				}
			} else if (tree.data.equalsIgnoreCase("PLUS")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters required for PLUS operation. ");
					throw new Exit_exception();
				}
				return plus(car(params), car(cdr(params)));
			} else if (tree.data.equalsIgnoreCase("MINUS")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters required for MINUS operation. ");
					throw new Exit_exception();
				}
				return minus(car(params), car(cdr(params)));
			} else if (tree.data.equalsIgnoreCase("NULL")) {
				if (numofparams != 1) {
					System.out
							.println("ERROR: Only one parameter is required for NULL operation. ");
					throw new Exit_exception();
				}
				if (isNull(car(params))) {
					return new Node("T");
				} else {
					return new Node("NIL");
				}
			} else if (tree.data.equalsIgnoreCase("INT")) {
				if (numofparams != 1) {
					System.out
							.println("ERROR: Only one parameter is required for INT operation.");
					throw new Exit_exception();
				}
				if (isInt(car(params))) {
					return new Node("T");
				} else {
					return new Node("NIL");
				}
			} else if (tree.data.equalsIgnoreCase("TIMES")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters are required for TIMES operation. ");
					throw new Exit_exception();
				}
				return times(car(params), car(cdr(params)));
			} else if (tree.data.equalsIgnoreCase("QUOTIENT")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters are required for QUOTIENT operation.");
					throw new Exit_exception();
				}
				return quotient(car(params), car(cdr(params)));
			} else if (tree.data.equalsIgnoreCase("REMAINDER")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters are required for REMAINDER operation. ");
					throw new Exit_exception();
				}
				return remainder(car(params), car(cdr(params)));
			} else if (tree.data.equalsIgnoreCase("LESS")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters are required for LESS operation. ");
					throw new Exit_exception();
				}
				return less(car(params), car(cdr(params)));
			} else if (tree.data.equalsIgnoreCase("GREATER")) {
				if (numofparams != 2) {
					System.out
							.println("ERROR: Two parameters are required for GREATER operation. ");
					throw new Exit_exception();
				}
				return greater(car(params), car(cdr(params)));
			} else {
				// User defined functions
				int numParamsUserDef = numofparam(car(getval(tree, dList)));
				if (numofparams != numParamsUserDef) {
					System.out
							.println("ERROR: Number of parameters do no match. ");
					throw new Exit_exception();
				}
				// System.out.println("Applying user defined Functions...");
				return eval(cdr(getval(tree, dList)),
						addPairs(car(getval(tree, dList)), params, aList),
						dList);
			}
		} else {
			System.out.println("ERROR: Error in applying function.");
			throw new Exit_exception();
		}
		return null;
	}

	public static Node addPairs(Node varList, Node valueList, Node oldList)
			throws Exit_exception {

		if (isNull(varList)) {
			return oldList;
		} else {
			return (cons(cons(car(varList), car(valueList)),
					addPairs(cdr(varList), cdr(valueList), oldList)));
		}
	}

	public static Node plus(Node left, Node right) throws Exit_exception {
		Node result = new Node("");
		if (atom(left) && atom(right)) {
			if (isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.data);
				int rightVal = Integer.parseInt(right.data);
				int res = leftVal + rightVal;
				result.data = Integer.toString(res);
			} else {
				System.out
						.println("ERROR: Cannot perform PLUS operation on literal atoms. ");
				throw new Exit_exception();
			}
		} else {
			System.out
					.println("ERROR: Cannot perform PLUS operation on Non atoms. ");
			throw new Exit_exception();
		}
		return result;
	}

	public static Node minus(Node left, Node right) throws Exit_exception {
		Node result = new Node("");
		if (atom(left) && atom(right)) {
			if (isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.data);
				int rightVal = Integer.parseInt(right.data);
				int res = leftVal - rightVal;
				result.data = Integer.toString(res);
			} else {
				System.out
						.println("ERROR: Cannot perform MINUS operation on literal atoms. ");
				throw new Exit_exception();
			}
		} else {
			System.out
					.println("ERROR: Cannot perform MINUS operation on Non atoms. ");
			throw new Exit_exception();
		}
		return result;
	}

	public static Node times(Node left, Node right) throws Exit_exception {
		Node result = new Node("");
		if (atom(left) && atom(right)) {
			if (isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.data);
				int rightVal = Integer.parseInt(right.data);
				int res = leftVal * rightVal;
				result.data = Integer.toString(res);
			} else {
				System.out
						.println("ERROR: Cannot perform TIMES operation on literal atoms. ");
				throw new Exit_exception();
			}
		} else {
			System.out
					.println("ERROR: Cannot perform TIMES operation on Non atoms. ");
			throw new Exit_exception();
		}
		return result;
	}

	public static Node quotient(Node left, Node right) throws Exit_exception {
		Node result = new Node("");
		if (atom(left) && atom(right)) {
			if (isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.data);
				int rightVal = Integer.parseInt(right.data);
				int res;
				if (rightVal == 0) {
					res = 0;
				} else {
					res = leftVal / rightVal;
				}
				result.data = Integer.toString(res);
			} else {
				System.out
						.println("ERROR: Cannot perform QUOTIENT operation on literal atoms. ");
				throw new Exit_exception();
			}
		} else {
			System.out
					.println("ERROR: Cannot perform QUOTIENT operation on Non atoms. ");
			throw new Exit_exception();
		}
		return result;
	}

	public static Node remainder(Node left, Node right) throws Exit_exception {
		Node result = new Node("");
		if (atom(left) && atom(right)) {
			if (isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.data);
				int rightVal = Integer.parseInt(right.data);
				int res = leftVal % rightVal;
				result.data = Integer.toString(res);
			} else {
				System.out
						.println("ERROR: Cannot perform REMAINDER operation on literal atoms. ");
				throw new Exit_exception();
			}
		} else {
			System.out
					.println("ERROR: Cannot perform REMAINDER operation on Non atoms. ");
			throw new Exit_exception();
		}
		return result;
	}

	public static Node less(Node left, Node right) throws Exit_exception {
		Node result = new Node("");
		if (atom(left) && atom(right)) {
			if (isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.data);
				int rightVal = Integer.parseInt(right.data);
				if (leftVal < rightVal) {
					result.data = "T";
				} else {
					result.data = "NIL";
				}
			} else {
				System.out
						.println("ERROR: Cannot perform TIMES operation on literal atoms. ");
				throw new Exit_exception();
			}
		} else {
			System.out
					.println("ERROR: Cannot perform TIMES operation on Non atoms. ");
			throw new Exit_exception();
		}
		return result;
	}

	public static Node greater(Node left, Node right) throws Exit_exception {
		Node result = new Node("");
		if (atom(left) && atom(right)) {
			if (isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.data);
				int rightVal = Integer.parseInt(right.data);
				if (leftVal > rightVal) {
					result.data = "T";
				} else {
					result.data = "NIL";
				}
			} else {
				System.out
						.println("ERROR: Cannot perform TIMES operation on literal atoms. ");
				throw new Exit_exception();
			}
		} else {
			System.out
					.println("ERROR: Cannot perform TIMES operation on Non atoms. ");
			throw new Exit_exception();
		}
		return result;
	}
}
