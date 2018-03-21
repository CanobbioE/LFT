import java.io.*;

public class Parser {
	private Lexer lex;
	private Token look;

	public Parser(Lexer l) {
		lex = l;
		move();
	}

	void move() {
		look = lex.lexical_scan();
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
		Lexer lex = new Lexer();
		Parser parser = new Parser(lex);
		parser.start();
	}
}
