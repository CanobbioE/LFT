public class Token {
    public final int tag;
    public Token(int t) { tag = t;  }
    public String toString() {return "<" + tag + ">";}
    public int  getTagValue() {return tag ;}
    public String  getLexeme() {return "" + tag + "" ;}
    public static final Token

            lpar = new Token('('),
            rpar = new Token(')'),
            plus = new Token('+'),
            minus = new Token('-'),
            mult = new Token('*'),
            div = new Token('/'),
            lt = new Token('<'),
            gt = new Token('>'),
            comma = new Token(','),
            colon = new Token(':'),
            semicolon = new Token(';');


}