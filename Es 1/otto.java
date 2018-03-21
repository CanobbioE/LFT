public class otto {
		public static boolean scan(String s) {
		int i = 0;
		int state = 0;
		while(state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);

			switch(state) {
				case 0:
					if (ch == 'a') state = 1;
					else if (ch == 'b') state = 3;
					else state = -1;
					break;
				case 1:
					if (ch == 'a') state = 1;
					else if (ch == 'b') state = 2;
					else state = -1;
					break;
				case 2:
					state = -1;
					break;
				case 3:
					if (ch == 'a') state = 4;
					else state = -1;
					break;
				case 4:
					if (ch == 'a') state = 4;
					else state = -1;
			}
		}

		return ((state == 4) || (state == 2));
	}

	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}