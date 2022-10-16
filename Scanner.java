
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner {
	
	//Global Variables
	private static String lexeme = "";
	private static int state = 0;
	private static FileReader sclFile;
	private static char currentChar = ' ';
	private static ArrayList <Token> keywordToken = new ArrayList<Token>();
	private static Token[] opToken = new Token[9];
	
	//////////////////////////////////////////////////////////////////////////////
	//TOKEN DATABASE
	public static void buildTokenBD() {
		
		//Operator tokens
		opToken[0] = new Token("ADD_OP", 21, '+');
		opToken[1] = new Token("SUB_OP", 22, '-');
		opToken[2] = new Token("MULT_OP", 23, '*');
		opToken[3] = new Token("DIV_OP", 24, '/');
		opToken[4] = new Token("RIGHT_BRACKET", 40,'>');
		opToken[5] = new Token("LEFT_BRACKET", 41, '<');
		opToken[6] = new Token("ASSIGN_OP", 43, '=');
		opToken[7] = new Token("QUOTE", 45, '"');
		opToken[8] = new Token("COMMA", 46, ',');
		
		//Keyword table
		keywordToken.add(new Token("description", 25));
		keywordToken.add(new Token("import", 26));
		keywordToken.add(new Token("input", 42));
		keywordToken.add(new Token("implementations", 27));
		keywordToken.add(new Token("functions", 28));
		keywordToken.add(new Token("is", 29));
		keywordToken.add(new Token("variables", 30));
		keywordToken.add(new Token("define", 31));
		keywordToken.add(new Token("of", 32));
		keywordToken.add(new Token("type", 33));
		keywordToken.add(new Token("double", 34));
		keywordToken.add(new Token("begin", 35));
		keywordToken.add(new Token("input", 36));
		keywordToken.add(new Token("set", 37));
		keywordToken.add(new Token("display", 38));
		keywordToken.add(new Token("endfun", 39));
	}
	
	////////////////////////////////////////////////////////////////////////////
	//MAIN DRIVER
	
	public static void main(String[] args) {
		
		while (currentChar != (char)-1) {
			if (keywordToken.isEmpty() || opToken.length == 0) {
				buildTokenBD();
				getChar();
				lex();
			} else
				
				lex();
		}
		System.out.println("End of file.");
	}

	///////////////////////////////////////////////////////////////////////////
	//LEX FUNCTION
	//actions taken based on current state
	
	public static void lex(){		
		
		//local variables
		int s = state;
		char ch = currentChar;
		
		switch(s) {
		
		//starting state
		case 0:
		
			if (ch == ' ' || ch =='\n' || ch == '\r' || ch == '\t') {
				nextState (0);
			} else if (ch == -1) {
				return;
			} else if (Character.isAlphabetic(ch)) {
				nextStateStoreLexeme(1);
			} else if (ch == '/')  {
				nextStateStoreLexeme(2);
			} else if (Character.isDigit(ch)) {
				nextStateStoreLexeme(3);
			} else if (ch == '<') {
				printOperator(ch);
				nextState(4);
			} else if (ch == '*') {
				printOperator(ch);
				getChar();
			} else if (ch == '+') {
				printOperator(ch);
				getChar();
			} else if (ch == '-') {
				printOperator(ch);
				getChar();
			} else if (ch == '=') {
				printOperator(ch);
				getChar();
			} else if (ch == ',') {
				printOperator(ch);
				getChar();
			} else if (ch == '"') {
				printOperator(ch);
				nextState(5);
			} else {
				System.out.println ("error: invalid token");
				currentChar = (char)-1;
				return;
			}
			break;
		
		// state for alphabetic characters
		case 1:
			if (Character.isAlphabetic(ch)) {
				nextStateStoreLexeme(1);
				break;
			} else if (lexeme.equals("description")){
				printKeyword();
				lexeme = "";
				nextState(6);
				break;
			}  else {
				for (int i = 0; i < keywordToken.size(); i++) {
					if (lexeme.equals(keywordToken.get(i).getName())) {
						printKeyword();
						lexeme = "";
						nextState(0);
						return;
					}
				}
				printIdentifier();
				lexeme = "";
				nextState(0);
			}
		break;
		
		//state for determining comment or '/' operator
		case 2:
			if (ch == '*') {
				lexeme = "";
				nextState(6);
			} else if (ch == '/') {
				lexeme = "";
				nextState(8);
			} else {
				printOperator('/');
				lexeme = "";
				nextState(0);
				
			}
		break;
			
		//state for numbers
		case 3:
			if (Character.isDigit(ch)) {
				nextStateStoreLexeme(3);
			} else {
				printIdentifier();
				lexeme = "";
				nextState(0);
			}
		break;
			
		//state for import file name 
		case 4:
			if (ch == '>'){
				try {
					if (lexeme == "") {
						throw new SyntaxException("Syntax error:");
					}
				}
				
				catch (SyntaxException e) {
					System.out.print(e.getMessage() + "\nMissing file name\n");
				}
				printString();
				lexeme = "";
				printOperator('>');
				nextState(0);
			} else
				nextStateStoreLexeme(4);
		break;
			
		//state for strings
		case 5:
			if (ch != '"') {
				nextStateStoreLexeme(5);
				break;
			} else 	{
				printString();
				lexeme = "";
				printOperator('"');
				nextState(0);
			}
		break;
			
		//state for dealing with block comment
		case 6:
			if (ch == '*') {
				nextState(7);
				break;
			}  else
				nextState(6);
			break;
			
		//state for deciding end of comment
		case 7:
			if (ch == '/') {
				nextState (0);
			} else
				nextState(6);
		break;
			
		//state for ending single line comment
		case 8:
			 if ( ch == '\n' || ch == '\r') {
				nextState(0);
			} else
				nextState(8);
		 break;
			
		default:
			System.out.println("lex error");
			currentChar = (char) -1;
			return;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//GETCHAR FUNCTION
	//reads the next character from the file
	
	public static void getChar() {
		
		try {
			if (sclFile == null) {
				sclFile = new FileReader("add.scl");
			}
			currentChar = (char)sclFile.read();
			if (currentChar == -1) {
				sclFile.close();
			}
		}
		catch (IOException e) {
			System.out.println("file handling error: " + e);
			currentChar = (char)-1;
			return;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//TRANSITIONTO
	//input: integer for next state
	//transitions to the next state
	
	public static void nextState(int n) {
		
		state = n;
		getChar();
	}
	
	///////////////////////////////////////////////////////////////////////////
	//TANSITION TO SAME STATE
	//input: integer for next state
	//transitions to the same state and concatenates next character to lexeme
	
	public static void nextStateStoreLexeme(int n) {
		
		state = n;
		lexeme +=currentChar;
		getChar();
	}

	///////////////////////////////////////////////////////////////////////////
	//PRINT KEYWORDS
	//print associated keyword token
	
	public static void printKeyword() {
		
		for (int i = 0; i < keywordToken.size(); i++) {
			if (lexeme.equals(keywordToken.get(i).getName())) {
				System.out.println("Next Token is: " + keywordToken.get(i).getCode() 
						+ "\tNext Lexeme is: " + lexeme);
				break;
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//PRINT IDENTIFIER
	//print associated identifier token
	
	public static void printIdentifier() {
		System.out.println("Next Token is: " + 1 + "\tNext Lexeme is: " + lexeme);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//PRINT STRING 
	//print associated string token
	
	public static void printString() {
		System.out.println("Next Token is: " + 2 + "\tNext Lexeme is: " + lexeme);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//PRINT OPERATOR
	//print associated operator token
	public static void printOperator(char n) {
		
		for (int i = 0; i <opToken.length; i++) {
			if (n == opToken[i].getOpChar()) {
				System.out.println("Next Token is: " + opToken[i].getCode() 
						+ "\tNext Lexeme is: " + n);
				break;
			}
		}
	}

}
	
