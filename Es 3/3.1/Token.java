public class Token {
    public final int tag;
    public Token(int t) { tag = t;  }
    public String toString() {return "<" + tag + ">";}
    public static final Token
            lpar = new Token('('),
            rpar = new Token(')'),
            plus = new Token('+'),
            minus = new Token('-'),
            mult = new Token('*'),
            div = new Token('/');
}