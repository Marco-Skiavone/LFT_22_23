import java.io.*;
import java.util.logging.LogRecord;

public class ParserV2 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public ParserV2(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	    throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) 
                move();
        } else 
            error("syntax error: expected token : " + t);
    }

    private void prog(){    //  assign, print, read, while, conditional, {
        if(look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || 
            look.tag == Tag.WHILE || look.tag == Tag.COND || look.tag == '{'){
                statlist();
                match(Tag.EOF);
            }
        else error("syntax error in prog(): missing entry condition!");
    }

    /*  s sl' -> assign print read while conditional { */
    private void statlist(){
        if(look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || 
            look.tag == Tag.WHILE || look.tag == Tag.COND || look.tag == '{'){
                stat();
                statlistp();
            }
        else error("syntax error in statlist(): missing entry condition!");
    }

    /*  ;s sl'     ;
     *  epsilon     $ }
     */
    private void statlistp(){
        switch(look.tag){
            case ';' -> {
                match(';');
                stat();
                statlistp();
            }
            case '}', Tag.EOF -> {/* epsilon */}
            default -> {error("syntax error in statlistp(): missing ; or } or Tag.EOF");}
        }
    }

    /*  Guida(S-> ...)assign print ready while conditional { */
    private void stat(){
        switch(look.tag){
            case Tag.ASSIGN -> {
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist();
            }
            case Tag.PRINT -> {
                match(Tag.PRINT);
                match('[');
                exprlist();
                match(']');
            }
            case Tag.READ -> {
                match(Tag.READ);
                match('[');
                idlist();
                match(']');
            }
            case Tag.WHILE -> {
                match(Tag.WHILE);
                match('(');
                bexpr();
                match(')');
                stat();
            }
            case Tag.COND -> {
                match(Tag.COND);
                match('[');
                optlist();
                match(']');
                if(look.tag != Tag.ELSE && look.tag != Tag.END){
                    error("syntax error in stat(): missing ELSE or END token in COND case.");
                } else if(look.tag == Tag.ELSE){
                    match(Tag.ELSE);
                    stat();
                }
                match(Tag.END);
            }
            case '{' -> {
                match('{');
                statlist();
                match('}');
            }
            default -> {
                error("syntax error in stat(): missing entry condition!");
            }
        }
    }

    private void idlist(){   // guida = ID
        if(look.tag == Tag.ID){
            match(Tag.ID);
            idlistp();
        } else error("syntax error in idlist(): missing ID entry condition!");
    }

    /* guida(-> epsilon)= ; EOF ] end option }
     * guida(->, id idlp)= ,   
    */
    private void idlistp(){  
        switch(look.tag){
            case ',' -> {
                match(',');
                match(Tag.ID);
                idlistp();
            }
            case ';', ']', '}', Tag.EOF, Tag.END, Tag.OPTION -> {/* epsilon */}
            default -> {
                error("syntax error in idlistp(): missing ID or , token!");
            }
        }
    }

    private void optlist(){  // guida = OPTION
        if(look.tag == Tag.OPTION){
            optitem();
            optlistp();
        } else error("syntax error in optlist(): missing OPTION token!");
    }

    /*  guida(->opti optl')= option
     *  guida(epsilon)= ]
     */
    private void optlistp(){
        switch(look.tag) {
            case Tag.OPTION ->{
                optitem();
                optlistp();
            }   
            case ']' -> {/* epsilon */}
            default -> {
                error("syntax error in optlistp(): missing OPTION or ] token!");
            }
        }
    }

    private void optitem(){  // guida = option
        if(look.tag == Tag.OPTION){
            match(Tag.OPTION);
            match('(');
            bexpr();
            match(')');
            match(Tag.DO);
            stat();
        } else error("syntax error in optitem(): missing OPTION token!");
    }

    private void bexpr(){    //guida = RELOP
        if(look.tag == Tag.RELOP){
            match(Tag.RELOP);
            expr();
            expr();
        } else error("syntax error in bexpr(): missing RELOP token!");
    }

    /*  guida(-> +(EL))= +          guida(-> *(EL))= *
     *  guida(-> -EE)= -            guida(-> /EE)= /
     *  guida(-> NUM)= NUM          guida(-> ID)= ID
     */
    private void expr(){
        switch(look.tag){
            case '+' -> {
                match('+');
                match('(');
                exprlist();
                match(')');
            }
            case '*' -> {
                match('*');
                match('(');
                exprlist();
                match(')');
            }
            case '-' -> {
                match('-');
                expr();
                expr();
            }
            case '/' -> {
                match('/');
                expr();
                expr();
            }
            case Tag.NUM -> {
                match(Tag.NUM);
            }
            case Tag.ID -> {
                match(Tag.ID);
            }
            default -> {
                error("syntax error in expr(): missing entry condition! Found: " + look);
            }
        }
    }
    
    private void exprlist(){ // guida = + - * / NUM ID
        switch(look.tag){
            case '+', '-', '*', '/', Tag.NUM, Tag.ID -> {
                expr();
                exprlistp();
            }
            default -> {
                error("syntax error in exprlist(): missing entry condition!");
            }
        }
    }

    /*  guida(-> ,EEL')= ,
     *  guida(-> epsilon)= ] }*   -> )
     */
    private void exprlistp(){
        switch(look.tag){
            case ',' -> {
                match(',');
                expr();
                exprlistp();
            }
            case ']', ')' -> {/* epsilon */}
            default -> {
                error("syntax error in exprlistp(): missing ',' or ] or } token! Found: " + look);
            }
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = args != null && args.length > 0 ? args[0] : "../parser3_2/esempio_semplice.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ParserV2 parser = new ParserV2(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}