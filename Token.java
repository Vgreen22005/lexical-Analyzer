
public class Token {

	private int tokenCode;
	private String tokenName;
	private char opChar;
	
	 Token() {
		 this.tokenName = "";
		 this.tokenCode = 0;
	 }
	 Token(String name,  int code){
		 tokenName = name;
		 tokenCode = code;
	 }
	 Token(String name, int code, char opChar){
		 this.tokenName = name;
		 this.tokenCode = code;
		 this.opChar = opChar;
	 }
	 
	 public String getName(){
		 return this.tokenName;
	 }
	 
	 public int getCode() {
		 return this.tokenCode;
	 }
	 
	 public char getOpChar() {
		 return this.opChar;
	 }
	
}
