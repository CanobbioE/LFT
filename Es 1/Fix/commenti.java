public class sei{
	public static boolean scan(String s) {
		int i = 0;
		int state = 0;
		while(state >= 0 && i < s.length()) {
			final char c = s.charAt(i++);

			switch(state) {
				case 0:
				
					if (c == 'a' || c == 'k') return false;
					else if (c == '/') state = 1;
					else state = -1;
					break;
				case 1:
				
					if (c == 'a' || c == '/') return false;
					else if (c == 'k') state = 2;
					else state = -1;
					break;
				case 2:
				
					if (c == '/') return false;
					else if (c == 'a') state = 3;
					else if (c == 'k') state = 4;
					else state = -1;
					break;
				case 3:
				
					if (c == '/') return false;
					else if (c == 'a') state = 3;
					else if (c == 'k') state = 4;
					else state = -1;
					break;
				case 4:
				
					if (c == '/')state = 5;
					else if (c == 'a') state = 3;
					else if (c == 'k') state = 4;
					else state = -1;
					break;
				case 5:
					state = -1;
					break;
			}
		}
		return (state == 5);
	}

	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}