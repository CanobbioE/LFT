import java.io.*; 
import java.util.*;

public class LexerEXT {

    public static int line = 1;
    private char peek = ' ';

    Hashtable<String,Word> words = new Hashtable<String,Word>();

    void reserve(Word w) { words.put(w.lexeme, w); }

    public LexerEXT() {
        reserve( new Word(Tag.VAR, "var"));
        reserve( new Word(Tag.NOT, "not"));
        reserve( new Word(Tag.INTEGER, "integer"));
        reserve( new Word(Tag.BOOLEAN, "boolean"));
        reserve( new Word(Tag.TRUE, "true"));
        reserve( new Word(Tag.FALSE, "false"));
        reserve( new Word(Tag.PRINT, "print"));
        reserve( new Word(Tag.BEGIN, "begin"));
        reserve( new Word(Tag.END, "end"));
        reserve( new Word(Tag.WHILE, "while"));
        reserve( new Word(Tag.DO, "do"));
        reserve( new Word(Tag.IF, "if"));
        reserve( new Word(Tag.THEN, "then"));
        reserve( new Word(Tag.ELSE, "else"));
    }
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {

            case ';':
                peek = ' ';
                return Token.semicolon;
            case ',':
                peek = ' ';
                return Token.comma;
            case ':':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.assign;
                }else return Token.colon;
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
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after = : "  + peek );
                    return null;
                }
            case '<':
                readch(br);
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
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                }else if (Character.isDigit(peek) || peek == ' ') {
                    peek = ' ';
                    return Token.gt;
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
                        readch(br);
                        if (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
                            System.out.println("Invalid ID _");
                            return null;
                        }
                    }
                    do {
                        s+= peek;
                        readch(br);
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
                        readch(br);
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

/*		
########CLASSE DI TEST PER LEXEREXT########

    public static void main(String[] args) {
        LexerEXT lex = new LexerEXT();
        String path = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));        
            Token tok;
            do {
                tok = lex.lexical_scan(); 
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        }catch (IOException e) {e.printStackTrace();}
    }
*/

}
