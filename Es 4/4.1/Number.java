public class Number extends Token {
	public final int val;
	public Number(int tag, int v) { super(tag); val = v; }
	public String toString() { return "<" + tag + ", "  + val + ">"; }

}