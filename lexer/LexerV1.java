import java.io.*; 
import java.util.*;

public class LexerV1 {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') 
                line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '[':
                peek = ' ';
                return Token.lpq;
            case ']':
                peek = ' ';
                return Token.rpq;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
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
            case ';':
                peek = ' ';
                return Token.semicolon;
            case ',':
                peek = ' ';
                return Token.comma;
        /* ---FATTO---
        ... gestire i casi di ( ) [ ] { } + - * / ; , ... 
        */
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
            case '<':
                readch(br);
                if (peek == '=') { // caso <=
                    peek = ' ';
                    return Word.le;
                } else if(peek == '>'){ // caso <>
                    peek = ' ';
                    return Word.ne;
                } else                    // caso <
                    return Word.lt;
            case '>':
                readch(br);
                if (peek == '=') {  // caso >=
                    peek = ' ';
                    return Word.ge;
                } else
                    return Word.gt;
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
        /* --- FATTO ---
        ... gestire i casi di || < > <= >= == <> ... 
        */      
            case (char)-1:
                return new Token(Tag.EOF);
            default:
                if (Character.isLetter(peek)) {
	                // ... gestire il caso degli identificatori e delle parole chiave //
                    String s = "" + peek;
                    do {
                        readch(br);
                        if(Character.isDigit(peek) || Character.isLetter(peek))
                            s += peek;
                    } while(Character.isDigit(peek) || Character.isLetter(peek));
                    switch(s){
                        case "assign"       -> {return Word.assign;}
                        case "to"           -> {return Word.to;}
                        case "conditional"  -> {return Word.conditional;}
                        case "option"       -> {return Word.option;}
                        case "do"           -> {return Word.dotok;}
                        case "else"         -> {return Word.elsetok;}
                        case "while"        -> {return Word.whiletok;}
                        case "begin"        -> {return Word.begin;}
                        case "end"          -> {return Word.end;}
                        case "print"        -> {return Word.print;}
                        case "read"         -> {return Word.read;}
                        default             -> {return new Word(Tag.ID, s);}    //  identificatore
                    }
                } else if (Character.isDigit(peek)) {
                    // ... gestire il caso dei numeri ... --- FATTO ---//
                    return new NumberTok(getNumber(br));    // (trova il numero ricorsivamente, il primo va aggiunto)
                } else {
                        System.err.println("Erroneous character: " + peek);
                        return null;
                }
        }
    }

    public int getNumber(BufferedReader br){  // restituisce il numero ma manca il primo passo (il primo digit letto)
        int i = peek-'0';
        readch(br);
        return Character.isDigit(peek) ? (i*10 + getNumber(br)) : i;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = args[0]; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }
}
