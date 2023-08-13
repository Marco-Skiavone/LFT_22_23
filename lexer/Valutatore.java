import java.io.*; 

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
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

    public void start() {    // guida (S->E)= #, (
	    int expr_val;
        switch(look.tag){
            case Tag.NUM, '(' -> {
                expr_val = expr();
                match(Tag.EOF);
                System.out.println(expr_val); /* TRADUZIONE */
            }
            default -> {
                error("syntax error in start().");
            }
        }
    	
    }

    private int expr() {   // guida(E->TE')= #, (
        int term_val, exprp_val;
        switch(look.tag){
            case Tag.NUM, '(' -> {
                term_val = term();
                exprp_val = exprp(term_val);
                return exprp_val;
            }
            default -> {
                error("syntax error in expr().");
            }
        }
        return -1;
    }

    // guida(E'->+TE')= +
    // guida (E'->-TE')= -
    // guida (E' -> eps) = ), $
    private int exprp(int exprp_i) { 
        int term_val, exprp_val;
        switch (look.tag) {
            case '+' -> {
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                return exprp_val;
            }
            case '-' -> {
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                return exprp_val;
            }
            case ')', Tag.EOF -> {
                return exprp_i;
            }
            default -> {
                error("syntax error in exprp().");
            }
        }
        return -1;
    }

    private int term() {    // guida(T -> FT') = #, (
        int fact_val, termp_val;
        switch(look.tag){
            case Tag.NUM, '(' -> {
                fact_val = fact();
                termp_val = termp(fact_val);
                return termp_val;
            }
            default -> {
                error("syntax error in term().");
            }
        }
        return -1;
    }
    
    // guida(T'->*FT')= *
    // guida (T'->/FT')= /
    // guida (T' -> eps) = +, -, ), $
    private int termp(int termp_i) { 
        int fact_val, termp_val;
        switch(look.tag){
            case '*' -> {
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                return termp_val;
            }
            case '/' -> {
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                return termp_val;
            }
            case '+', '-', ')', Tag.EOF -> {
                return termp_i;
            }
            default -> {
                error("syntax error in termp().");
            }
        }
        return -1;
    }
    
    private int fact() {    //guida(F->#)= #   |  guida(F->(E)) = (
        int num_value, expr_val;
        switch(look.tag){
            case Tag.NUM -> {
                num_value = ((NumberTok)look).number;
                match(Tag.NUM);
                return num_value;
            }
            case '(' -> {
                match(Token.lpt.tag);
                expr_val = expr(); 
                match(Token.rpt.tag);
                return expr_val;
            }
            default -> {
                error("syntax error in fact().");
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "...path..."; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
