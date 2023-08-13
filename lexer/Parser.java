import java.io.*;

import javax.sql.rowset.serial.SerialRef;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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

    public void start() {   // guida (S->E)= #, (
        switch(look.tag){
            case Tag.NUM, '(' -> {
                expr();
                match(Tag.EOF);
            }
            default -> {error("syntax error in start().");}
        }
        
    }

    private void expr() {   // guida(E->TE')= #, (
        switch(look.tag){
            case Tag.NUM, '(' -> {
                term();
                exprp();
            }
            default -> {error("syntax error in expr(): missing Tag.NUM or '('");}
        }
    }

    private void exprp() {      // guida(E'->+TE')= +
                                // guida (E'->-TE')= -
                                // guida (E' -> eps) = ), $
        switch (look.tag) {
            case '+','-' -> {
                if(look.tag == '+')
                    match(Token.plus.tag);
                else if(look.tag == '-')
                    match(Token.minus.tag);
                term();
                exprp(); 
            }
            case ')', Tag.EOF -> {}
            default  -> {
                error("syntax error in exprp(): missing '+' or '-'");
            }
        }
    }

    private void term() {      // guida(T -> FT') = #, (
        switch(look.tag){
            case Tag.NUM, '(' -> {
                fact();
                termp(); 
            }
            default -> {
                error("syntax error in term(): missing Tag.NUM or ')'");
            }
        }
    }

    private void termp() {      // guida(T'->*FT')= *
                                // guida (T'->/FT')= /
                                // guida (T' -> eps) = +, -, ), $
        switch (look.tag) {
            case '*','/' -> {
                if(look.tag == '*')
                    match(Token.mult.tag);
                else if(look.tag == '/')
                    match(Token.div.tag);
                fact();
                termp();       
            }
            case '+', '-', ')', Tag.EOF -> {}
            default  -> {
                error("syntax error in termp(): missing '*' or /");
            }
        }
    }

    private void fact() {       //guida(F->#)= #   ||| guida(F->(E)) = (
        switch (look.tag) {
            case Tag.NUM -> {
                match(Tag.NUM);
            }
            case '(' -> {
                match(Token.lpt.tag);
                expr(); 
                match(Token.rpt.tag);
            }
            default  -> {
                error("syntax error in fact(): missing Tag.NUM or ')'");
            }
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova_sintatticaV1.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}