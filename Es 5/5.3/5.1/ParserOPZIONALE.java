import java.io.*;

public class ParserOPZIONALE {
	private Lexer lex;
	private Token look;
	private CodeGenerator code = new CodeGenerator();

	public ParserOPZIONALE(Lexer l) {
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

	private void prog() {
		Type orE;

		match(Tag.PRINT);
		match('(');
		orE = orE();
		match(')');

		code.emit(OpCode.invokestatic, 1);
		match(Tag.EOF);

		try {
			code.toJasmin();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(orE);


	}

	private Type orE() {
		Type andE_val, orEp_val;

		andE_val = andE();
		orEp_val = orE_p(andE_val);

		return orEp_val;
	}

	private Type orE_p(Type orE_p_i) {
		Type andE_type = null;

		switch (look.tag) {
			case Tag.OR:
				match(Tag.OR);
				//int orE_p_val = code.newLabel();
				andE_type = andE();
				code.emit(OpCode.ior);
				//code.emitLabel(orE_p_val);

				if (orE_p_i != Type.BOOLEAN) error("Invalid types");

			default:
				andE_type = orE_p_i;
				break;
		}
		return andE_type;
	}

	private Type andE(){
		Type relE_val, andE_p_val;
		relE_val = relE();
		andE_p_val = andE_p(relE_val);
		return andE_p_val;
	}

	private Type andE_p(Type relE_val) {
		Type relE_type = null;

		switch (look.tag) {
			case Tag.AND:
				match(Tag.AND);
				//int andE_p_val = code.newLabel();
				relE_type = relE();
				code.emit(OpCode.iand);
				//code.emitLabel(andE_p_val );

				if (relE_val != Type.BOOLEAN) error("Invalid types");
			default:
				relE_type = relE_val;
				break;
		}
		return relE_type;
	}

	private Type relE() {
		Type addE_val, relE_p_val;

		addE_val = addE();
		relE_p_val = relE_p(addE_val);

		return relE_p_val;
	}

	private Type relE_p(Type relE_p_i) {
		Type addE_type = null;
		Type oprel_type = null;

		oprel_type = oprel(relE_p_i);
		addE_type = addE();

		if (oprel_type != addE_type) error("Inconsistent types");
		relE_p_i = Type.BOOLEAN;
		return relE_p_i;
	}

	private oprel(Type oprel_val) {
		int ltrue, lnext;

		switch (look.tag) {
			case Tag.EQ:
				match(Tag.EQ);

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmpeq, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				break;

			case Tag.NE:
				match(Tag.NE);

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmpne, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				break;

			case Tag.LE:
				match(Tag.LE);

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmple, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				if (oprel_val != Type.INTEGER) error("Invalid types");
				break;

			case Tag.GE:
				match(Tag.GE);

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmple, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				if (oprel_val != Type.INTEGER) error("Invalid types");
				break;

			case '<':
				match('<');

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmplt, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				if (oprel_val != Type.INTEGER) error("Invalid types");
				break;

			case '>':
				match('>');

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmpgt, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				if (oprel_val != Type.INTEGER) error("Invalid types");
				break;
			default:
				break;
		}
		return oprel_val;
	}
	private Type addE() {
		Type multE_val, addE_p_val;

		multE_val = multE();
		addE_p_val = addE_p(multE_val);

		return addE_p_val;
	}

	private Type addE_p(Type multE_val) {
		Type multE_type = null;
		int addE_val;

		switch(look.tag) {
			case '+':
				match('+');

				//addE_val = code.newLabel();
				multE_type = multE();
				code.emit(OpCode.iadd);
				//code.emitLabel(addE_val);

				if (multE_val != Type.INTEGER) error("Invalid types");
				break;
			case '-':
				match('-');

				//addE_val = code.newLabel();
				multE_type = multE();
				code.emit(OpCode.isub);
				//code.emitLabel(addE_val);

				if (multE_val != Type.INTEGER) error("Invalid types");
				break;
			default:
				multE_type = multE_val;
				break;

		}
		return multE_type;
	}

	private Type multE() {
		Type fact_val, multE_p_val;

		fact_val = fact();
		multE_p_val = multE_p(fact_val);

		return multE_p_val;
	}

	private Type multE_p(Type fact_val_i) {
		Type fact_type = null;
		int mult_val;

		switch (look.tag) {
			case '*':
				match('*');
				fact_type = fact();

				//mult_val = code.newLabel();
				code.emit(OpCode.imul);
				//code.emitLabel(mult_val );

				if (fact_val_i != Type.INTEGER) error("Invalid types");
				break;

			case '/':
				match('/');
				fact_type = fact();

				//mult_val = code.newLabel();
				code.emit(OpCode.idiv);
				//code.emitLabel(mult_val );

				if (fact_val_i != Type.INTEGER) error("Invalid types");
				break;
			default:
				fact_type = fact_val_i;
				break;
		}
		return fact_type;
	}

	private Type fact() {
		Type fact_val = null;

		switch (look.tag) {
			case '(':
				match('(');
				fact_val = orE();
				match(')');
				break;

			case Tag.NUM:
				int tmp  = ((Number)look).val;
				match(Tag.NUM);
				fact_val = Type.INTEGER;

				//int ldc_val = code.newLabel();
				code.emit (OpCode.ldc, tmp );
				//code.emitLabel(ldc_val);
				break;

			case Tag.TRUE:
				match(Tag.TRUE);
				fact_val = Type.BOOLEAN;

  				//int true_val = code.newLabel();
				code.emit (OpCode.ldc, 1 );
				//code.emitLabel(true_val);
				break;

			case Tag.FALSE:
				match(Tag.FALSE);
				fact_val = Type.BOOLEAN;

  				//int false_val = code.newLabel();
				code.emit (OpCode.ldc, 0 );
				//code.emitLabel(false_val);
				break;

			default:
				break;
		}
		return fact_val;
	}


	public static void main(String[] args) {
		Lexer lex = new Lexer();
		ParserOPZIONALE parser = new ParserOPZIONALE(lex);
		parser.prog();
	}
}
