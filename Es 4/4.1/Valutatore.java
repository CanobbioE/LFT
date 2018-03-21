import java.io.*;

public class Valutatore {
	private Lexer lex;
	private Token look;

	public Valutatore(Lexer l) {
		lex = l;
		move();
	}

	void move() {
		look = lex.lexical_scan();
		System.err.println("token = " + look);
	}
@SuppressWarnings("static")
	void error(String s) {
		throw new Error("near line " + lex.line + ": " + s);
	}

	void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF) move();
		} else error("syntax error ");
	}

	public void start() {
		int expr_val;

		expr_val = expr();
		match(Tag.EOF);

		System.out.println(expr_val);
	}

	private int expr() {
		int term_val, expr_val;

		term_val = term();
		expr_val = exprp(term_val);

		return expr_val;
	}

	private int exprp(int exprp_i) {
		int term_val, exprp_val = 0;

		switch (look.tag) {
			case '+':
				match('+');
				term_val = term();
				exprp_val = exprp(exprp_i + term_val);
				break;
			case '-':
				match('-');
				term_val = term();
				exprp_val = exprp(exprp_i - term_val);
				break;
			default:
				exprp_val = exprp_i;
				break;
		}
		return exprp_val;
	}

	private int term() {
		int fact_val, term_val;

		fact_val = fact();
		term_val = termp(fact_val);

		return term_val;
	}

	private int termp(int termp_i) {
		int fact_val, termp_val;

		switch (look.tag) {
			case '*':
				match('*');
				fact_val = fact();
				termp_val = termp(termp_i * fact_val);
				break;
			case '/':
				match('/');
				fact_val = fact();
				termp_val = termp(termp_i / fact_val);
				break;
			default:
				termp_val = termp_i;
				break;
		}
		return termp_val;
	}

	private int fact() {
		int fact_val = 0;

		switch (look.tag) {
			case Tag.NUM:
				fact_val = ((Number)look).val;
				match(Tag.NUM);
				break;
			case '(':
				match('(');
				expr();
				match (')');
				break;
			default:
				break;
		}
		return fact_val;
	}

	public static void main(String[] args) {
		Lexer lex = new Lexer();
		Valutatore valutatore = new Valutatore(lex);
		valutatore.start();
	}
}
