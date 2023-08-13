import java.io.*; 

public class Lexer {    // 2.2 e 2.3

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
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r' || peek == '/') {
            if (peek == '\n') 
                line++;
            if(peek == '/'){
                readch(br);
                switch(peek) {
                    case '*' -> {
                        int state = 0;
                        do {
                            readch(br);
                            //System.out.println("Char letto: " + ((int)peek));
                            if(peek == (char)-1){
                                System.err.println("#00\tUnclosed Multiline Comment!");
                                return null;
                            }
                            if(peek == '*')
                                state = 1;
                            else if(peek == '/' && state == 1)
                                state = 2;
                        } while(state != 2);
                        peek = ' ';
                    }
                    case '/' -> {
                        while(peek != '\n')
                            readch(br);
                    }
                    default -> {return Token.div;}
                }
            } else
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
            // non piu' il caso '/'
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
                    System.err.println("#03\tErroneous character"
                            + " after & : "  + peek );
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("#04\tErroneous character"
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
                    System.err.println("#05\tErroneous character"
                            + " after = : "  + peek );
                    return null;
                }
        /* --- FATTO ---
        ... gestire i casi di || < > <= >= == <> ... 
        */      
            case (char)-1:
                return new Token(Tag.EOF);
            default:
                if (Character.isLetter(peek) || peek == '_') {
	                // ---FATTO--- ... gestire il caso degli identificatori e delle parole chiave //
                    String s = "" + peek;
                    do {
                        readch(br);
                        if(Character.isLetterOrDigit(peek) || peek == '_')
                            s += peek;
                    } while(Character.isLetterOrDigit(peek) || peek == '_');
                    if(identifier(s)) {
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
                    } else {
                        System.err.println("#06\tErroneous sequence: \"" + s + peek + "\"");
                        return null;
                    }
                } else if (Character.isDigit(peek)) {
                    // ... gestire il caso dei numeri ... --- FATTO ---//
                    return new NumberTok(getNumber(br, peek-'0'));    // (trova il numero ricorsivamente, il primo va aggiunto)
                } else {
                        System.err.println("#07\tErroneous character: " + peek);
                        return null;
                }
        }
    }

    public int getNumber(BufferedReader br, int n){  // restituisce il numero ma manca il primo passo (il primo digit letto)
        readch(br);
        if(!Character.isDigit(peek))
            return n;
        else {
            n = n*10+(peek-'0');
            return getNumber(br, n);
        }
    }

    public static boolean identifier(String s){
        if(s==null || s.length() == 0)
            System.err.println("Errore! Stringa nulla o vuota.");
        int i = 0, state = 0;
        while(s != null && s.length() != 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {state = (ch == '_') ? 1 : Character.isLetter(ch) ? 2 : -1;}
                case 1 -> {state = (Character.isLetterOrDigit(ch)) ? 2 : (ch == '_') ? 1 : -1;}
                case 2 -> {state = (Character.isLetterOrDigit(ch) || ch == '_') ? 2 : -1;}
                default -> {state = -1;}
            }
        }
        return state == 2;
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
