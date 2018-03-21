import java.io.*; 
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';

    Hashtable<String,Word> words = new Hashtable<String,Word>();
    void reserve(Word w) { words.put(w.lexeme, w); }
	
    public Lexer(){}   
	
    private void readch() {
        try {
            peek = (char) System.in.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan() {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch();
        }
        
        switch (peek) {
            case '(':
                peek = ' ';
                return Token.lpar;
            case ')':
                peek = ' ';
                return Token.rpar;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':
                peek = ' ';
                return Token.div;
                 
            default:
                if (Character.isDigit(peek)) {
                    String s = "";
                    do {
                        s+= peek;
                        readch();
                    }while (Character.isDigit(peek));
                    Number n = new Number(Tag.NUM, Integer.parseInt(s));
                    return n;
                }else{
                    if (peek == '$') {
                        return new Token(Tag.EOF);
                        } else {
                            System.err.println("Erroneous character: " + peek );
                            return null;
                        }
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        
        Token tok;
        do {
            tok = lex.lexical_scan();
            System.out.println("Scan: " + tok);
        } while (tok.tag != Tag.EOF);
    }

}
