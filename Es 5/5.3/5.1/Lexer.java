import java.io.*; 
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';

    Hashtable<String,Word> words = new Hashtable<String,Word>();
    void reserve(Word w) { words.put(w.lexeme, w); }
    public Lexer() {
        reserve( new Word(Tag.TRUE, "true"));
        reserve( new Word(Tag.FALSE, "false"));
        reserve( new Word(Tag.PRINT, "print"));
    }
    
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
            case '&':
                readch();
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
            case '|':
                readch();
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            case '=':
                readch();
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after = : "  + peek );
                    return null;
                }
            case '<':
                readch();
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                }else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                }else if (Character.isDigit(peek) || peek == ' ') {
                    peek = ' ';
                    return Token.lt;
                }else {
                    System.err.println("Erroneous character"
                            + " after < : "  + peek );
                    return null;
                }
            case '>':
                readch();
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                }else if (Character.isDigit(peek) || peek == ' ') {
                    peek = ' ';
                    return Token.lt;
                }else {
                    System.err.println("Erroneous character"
                            + " after > : "  + peek );
                    return null;
                }
            default:
                if (Character.isLetter(peek) || peek == '_') {
                    String s = "";
                    if (peek == '_') {
                        s+=peek;
                        readch();
                        if (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
                            System.out.println("Invalid ID _");
                            return null;
                        }
                    }
                    do {
                        s+= peek;
                        readch();
                    } while (Character.isDigit(peek) || 
                            Character.isLetter(peek) ||
                            peek == '_');
                    if ((Word)words.get(s) != null) 
                        return (Word)words.get(s);
                    else {
                    Word w = new Word(Tag.ID,s);
                    words.put(s, w);
                    return w;  
                    }

                } else if (Character.isDigit(peek)) {
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
