/*DFA che riconosce il linguaggio degli identificatori in un linguaggio in 
  stile java*/
public class quattro {
	public static boolean scan(String s) {
		int i = 0;
		int state = 0;
		while(state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);

			switch(state) {
				case 0:
					if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) state  = 1;
					else if (ch == '_') state = 2;
					else state = -1;
					break;
				case 1:
					if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch == '_') || (ch >= 0 && ch <= 9)) state  = 1;
					else state = -1;
					break;
				case 2:
					if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch == '_')) state  = 1;
					else state = -1;
					break;
			}
		}

		return state == 1;
	}

	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}