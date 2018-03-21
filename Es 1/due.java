/*DFA che riconosce il linguaggio delle costanti numeriche in virgola mobile*/

public class due {

	public static boolean scan(String s) {
		int i  = 0;
		int state = 0;

		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			
			switch (state) {

				//input precedenti: nulla input ammissibili: num + - .
				case 0:
				
					if (ch == 'e') return false;
					else if (ch == '+' || ch == '-') state = 1;
					else if (ch == '.') state = 2;
					else if (ch >= '0' && ch <= '9') state = 3;
					else state = -1;
					break;
				// input prec: + - input ammissibili: num .
				case 1:
				
					if (ch == 'e' || ch == '+' || ch == '-') return false;
					else if (ch == '.') state = state = 2;
					else if (ch >= '0' && ch <= '9') state = 3;
					else state = -1;
					break;
				//input prec: . input ammissibili: num e
				case 2:
				
					if (ch >= '0' && ch <= '9') state = 4;
					else if (ch == '+' || ch == '-' || ch == '.') return false;
					else if (ch == 'e') state = 5;
					else state = -1;
					break;
				//input prec: num input ammissibili: num . e
				case 3: 
				
					if (ch >= '0' && ch <= '9') state = 3;
					else if (ch == 'e') state = 5;
					else if (ch == '+' || ch == '-') return false;
					else if (ch == '.') state = 2;
					break;
				//input prec: .num input amm: num e
				case 4:
				
					if (ch >= '0' && ch <= '9') state = 4;
					else if (ch == 'e') state = 5;
					else if (ch == '+' || ch == '-' || ch == '.') return false;
					else state = -1;
					break;
				//input prec: nume input amm: + - num 
				case 5:
				
					if(ch >= '0' && ch <= '9') state = 6;
					else if (ch == '+' || ch == '-') state = 6;
					else if (ch == 'e' || ch == '.') return false;
					else state = -1;
					break;
				//input prec: + - num input amm: num
				case 6:
				
					if(ch >= '0' && ch <= '9') state = 6;
					else if (ch == 'e' || ch == '.' || ch == '+' || ch == '-') return false;
					else state = -1;
					break;			
			}
		}
		return state != -1;

	}

	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}