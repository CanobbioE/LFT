import java.io.*;

public class ParserEXT {
	private LexerEXT lex;
	private Token look;
	private CodeGenerator code = new CodeGenerator();
	private SymbolTable st = new SymbolTable();
	private BufferedReader pbr;

	public ParserEXT(LexerEXT l, BufferedReader br) {
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

	private void prog() {
		declist(0);
		stat();
		match(Tag.EOF);

		try {
			code.toJasmin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void declist(int start) {
		int address = start;

		if (look.tag == Tag.BOOLEAN || look.tag == Tag.INTEGER) {
			address = dec(start);
			match(';');
			declist(address);
		}
	}

	private int dec(int address) {
		Type id_type;
		String id_string;
		int ret_address = address;

		id_type = type();
		id_string = look.getLexeme();

		match(Tag.ID);

		st.insert(id_string, id_type, address);
		ret_address = idlist(id_type, address+1);

		return ret_address; //returns first free address
	}
	
	private int idlist(Type id_type, int address) {
		String id_string;
		int ret_address = address;

		switch(look.tag) {

			case ',':
				match(',');
				id_string = look.getLexeme();
				match(Tag.ID);

				st.insert(id_string, id_type, address);
				ret_address = idlist(id_type, ++address);
				break;

			default:
				ret_address = address;
				break;
		}
		return ret_address; //returns first free address 
	}

	private Type type() {
		Type id_type = null;

		switch (look.tag) {

			case Tag.INTEGER:
				id_type = Type.INTEGER;
				match(Tag.INTEGER);
				break;

			case Tag.BOOLEAN:
				id_type = Type.BOOLEAN;
				match(Tag.BOOLEAN);
				break;
			default:
				error("Invalid type declaration");
				break;
		}
		return id_type;
	}


	private void stat() {
		String id_string;
		Type id_type, exp_type;
		int id_address, lnext, ltrue;

		switch (look.tag) {
			case Tag.ID:
				id_string = look.getLexeme();
				id_type = st.lookupType(id_string);
				id_address = st.lookupAddress(id_string);

				match(Tag.ID);
				match(Tag.ASSIGN);
				exp_type = exp();
				if (exp_type != id_type) error("Inconsistent assigned type");

				code.emit(OpCode.istore, id_address);
				break;

			case Tag.PRINT:
				match(Tag.PRINT);
				match('(');
				exp_type = exp();
				match(')');
				
				if (exp_type == Type.BOOLEAN) code.emit(OpCode.invokestatic, 0);
				if (exp_type == Type.INTEGER) code.emit(OpCode.invokestatic, 1);
				break;

			case Tag.BEGIN:
				match(Tag.BEGIN);
				statlist();
				match(Tag.END);
				break;

			case Tag.WHILE:
				match(Tag.WHILE);

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emitLabel(ltrue);

				exp_type = exp();
				if (exp_type != Type.BOOLEAN) error("Invalid type for expression (while)");

				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.if_icmpeq, lnext);

				match(Tag.DO);

				stat();
				code.emit(OpCode.GOto, ltrue);
				code.emitLabel(lnext);
				break;

			case Tag.IF:
				match(Tag.IF);

				ltrue = code.newLabel();
				lnext = code.newLabel();

				exp_type = exp();
				if (exp_type != Type.BOOLEAN) error("Invalid type for expression (if)");
				
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.if_icmpeq, ltrue);

				match(Tag.THEN);
				stat();
				
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);

				if (look.tag == Tag.ELSE) {
					match(Tag.ELSE);

					stat();
					
				}
				code.emitLabel(lnext);
		}
	}

	private void statlist() {
		stat();
		statlist_p();
	}

	private void statlist_p() {

		switch (look.tag) {

			case ';':
				match(';');
				stat();
				statlist_p();
				break;
		}	
	}

	private Type exp() {
		Type orE_type = orE();
		return orE_type;
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

				int lnext = code.newLabel();
				code.emit(OpCode.ldc, 1);
				code.emit(OpCode.if_icmpeq, lnext);

				andE_type = andE();

				int lskip = code.newLabel();
				code.emit(OpCode.GOto, lskip);
				code.emitLabel(lnext);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lskip);

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

				int lnext = code.newLabel();
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.if_icmpeq, lnext);

				relE_type = relE();
				
				int lskip = code.newLabel();
				code.emit(OpCode.GOto, lskip);
				code.emitLabel(lnext);
				code.emit(OpCode.ldc, 0);
				code.emitLabel(lskip);

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
		int ltrue, lnext;

		switch (look.tag) {
			case Tag.EQ:
				match(Tag.EQ);
				addE_type = addE();

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmpeq, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				//Type checking?
				if (relE_p_i != addE_type) error("Inconsistent types");
				relE_p_i = Type.BOOLEAN;
				break;

			case Tag.NE:
				match(Tag.NE);
				addE_type = addE();

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmpne, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				//Type checking?
				if (relE_p_i != addE_type) error("Inconsistent types");
				relE_p_i = Type.BOOLEAN;
				break;

			case Tag.LE:
				match(Tag.LE);
				addE_type = addE();

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmple, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				if (relE_p_i != Type.INTEGER) error("Invalid types");
				relE_p_i = Type.BOOLEAN;
				break;

			case Tag.GE:
				match(Tag.GE);
				addE_type = addE();

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmple, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				if (relE_p_i != Type.INTEGER) error("Invalid types");
				relE_p_i = Type.BOOLEAN;
				break;

			case '<':
				match('<');
				addE_type = addE();

				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmplt, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				if (relE_p_i != Type.INTEGER) error("Invalid types");
				relE_p_i = Type.BOOLEAN;
				break;

			case '>':
				match('>');
				addE_type = addE();
				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emit(OpCode.if_icmpgt, ltrue);
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);

				if (relE_p_i != Type.INTEGER) error("Invalid types");
				relE_p_i = Type.BOOLEAN;
				break;
			default:
				addE_type = relE_p_i;
				break;
		}
		return relE_p_i;
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

				multE_type = multE();
				code.emit(OpCode.iadd);

				if (multE_val != Type.INTEGER) error("Invalid types");
				break;
			case '-':
				match('-');

				multE_type = multE();
				code.emit(OpCode.isub);

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

				code.emit(OpCode.imul);

				if (fact_val_i != Type.INTEGER) error("Invalid types");
				break;

			case '/':
				match('/');
				fact_type = fact();

				code.emit(OpCode.idiv);

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
		Type id_type;
		int id_address;
		String id_string;

		switch (look.tag) {

			case '(':
				match('(');
				fact_val = exp();
				match(')');
				break;

			case Tag.ID:
				id_string = look.getLexeme();
				id_type = st.lookupType(id_string);
				id_address = st.lookupAddress(id_string);
				match(Tag.ID);


				code.emit(OpCode.iload, id_address);

				fact_val = id_type;
				break;

			case Tag.NUM:
				int tmp  = ((Number)look).val;
				match(Tag.NUM);
				fact_val = Type.INTEGER;

				
				code.emit (OpCode.ldc, tmp );
				break;

			case Tag.TRUE:
				match(Tag.TRUE);
				fact_val = Type.BOOLEAN;

  				
				code.emit (OpCode.ldc, 1 );
				break;

			case Tag.FALSE:
				match(Tag.FALSE);
				fact_val = Type.BOOLEAN;

  				
				code.emit (OpCode.ldc, 0 );
				break;

			default:
				break;
		}
		return fact_val;
	}

	public static void main(String[] args) {

		LexerEXT lex = new LexerEXT();
		String path = "./test.pas"; //path of input file
		try {
			BufferedReader br = new BufferedReader (new FileReader(path));
			ParserEXT parser = new ParserEXT(lex, br);
			parser.prog();
			br.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
