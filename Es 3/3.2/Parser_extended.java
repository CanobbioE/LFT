import java.io.*;

public class Parser_extended {
	private Lexer_extended lex;
	private BufferedReader pbr;
	private Token look;

	public Parser_extended(Lexer_extended l, BufferedReader br) {
		lex = l;
		pbr = br;
		move();
	}

	void move() {
		look = lex.lexical_scan(pbr);
		System.err.println("token = " + look);
	}

	void error(String s) {
		throw new Error("near line " + lex.line + ": " + s);
	}

	void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF) move();
		} else error("syntax error ");
	}

	public void start() {
		expr();
		match(Tag.EOF);
	}

	private void expr() {
		term();
		exprp();
	}

	private void exprp() {
		switch (look.tag) {
			case '+':
				match('+');
				term();
				exprp();
				break;
			case '-':
				match('-');
				term();
				exprp();
				break;
			default:
				break;
		}
	}

	private void term() {
		fact();
		termp();
	}

	private void termp() {
		switch (look.tag) {
			case '*':
				match('*');
				fact();
				termp();
				break;
			case '/':
				match('/');
				fact();
				termp();
				break;
			default:
				break;
		}
	}

	private void fact() {
		switch (look.tag) {
			case '(':
				match('(');
				expr();
				match (')');
				break;
			case Tag.NUM:
				match(Tag.NUM);
				break;
			default:
				break;
		}
	}

	public static void main(String[] args) {
		Lexer_extended lex = new Lexer_extended();
		String path = "";
		parser.start();
		try {
			BufferedReader br = new BufferedReader (new FileReader(path));
			Parser_extended parser = new Parser_extended(lex, br);
			parser.start();
			br.close;
		} catch (IOException e) {e.printStackTrace();}
	}
}
