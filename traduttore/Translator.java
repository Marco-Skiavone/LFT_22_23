import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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
            int lnext_prog = code.newLabel();
            statlist();
            code.emitLabel(lnext_prog);
            match(Tag.EOF);
            try {
                code.toJasmin();
            }
            catch(java.io.IOException e) {
                System.out.println("IO error\n");
            };
        } else error("syntax error in prog(): missing entry condition!");
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
            case '}', Tag.EOF -> {
                //code.emit(OpCode.GOto, label_end);
            }
            default -> {error("syntax error in statlistp(): missing ; or } or Tag.EOF");}
        }
    }

    /*  Guida(S-> ...)assign print read while conditional { */
    private void stat() {
        switch(look.tag) {
            case Tag.ASSIGN-> {
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(false);
            }
            case Tag.PRINT-> {
                match(Tag.PRINT);
                match('[');
                exprlist(OpCode.invokestatic);
                match(']');
            }
            case Tag.READ->{
                match(Tag.READ);
                match('[');
                code.emit(OpCode.invokestatic, 0);
	            idlist(true);
                match(']');
            }
            case Tag.WHILE-> {
                match(Tag.WHILE);
                match('(');
                int start_loop_label = code.newLabel();
                int end_loop_label = code.newLabel();
                code.emitLabel(start_loop_label);
	            bexpr(end_loop_label, false);
                match(')');
                stat();
                code.emit(OpCode.GOto, start_loop_label);
                code.emitLabel(end_loop_label);
            }
            case Tag.COND-> {
                match(Tag.COND);
                match('[');
                int jump_end_label = code.newLabel();
                optlist(jump_end_label);
                match(']');
                statcond(jump_end_label);
            }
            case '{' -> {
                match('{');
                statlist();
                match('}');
            }
            default -> {error("syntax error in stat(): missing entry point!");}
        }
    }

    // - > end {end}        || -> else S end { else }
    private void statcond(int end_label){
        switch(look.tag){
            case Tag.END -> {
                match(Tag.END);
                code.emitLabel(end_label);
            }
            case Tag.ELSE -> {
                match(Tag.ELSE);
                stat();
                match(Tag.END);
                code.emitLabel(end_label);
            }
            default -> {
                error("syntax error in statcond()!");
            }
        }
    }

    private void optlist(int end_label){  // guida = OPTION
        if(look.tag == Tag.OPTION){
            int next_label = code.newLabel();
            optitem(end_label, next_label);
            code.emitLabel(next_label);
            optlistp(end_label);
        } else error("syntax error in optlist(): missing OPTION token!");
    }

    /*  guida(->opti optl')= option
     *  guida(epsilon)= ]
     */
    private void optlistp(int end_label){
        switch(look.tag) {
            case Tag.OPTION -> {
                int next_label = code.newLabel();
                optitem(end_label, next_label);
                code.emitLabel(next_label);
                optlistp(end_label);
            }   
            case ']' -> {}
            default -> {
                error("syntax error in optlistp(): missing OPTION or ] token!");
            }
        }
    }

    private void optitem(int end_label, int next_label){  // guida = option
        if(look.tag == Tag.OPTION){
            match(Tag.OPTION);
            match('(');
            bexpr(next_label, false);
            match(')');
            match(Tag.DO);
            stat();
            code.emit(OpCode.GOto, end_label);
        } else error("syntax error in optitem(): missing OPTION token!");
    }

    // guida = ID
    private void idlist(boolean read) {
        switch(look.tag) {
            case Tag.ID -> {
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
                idlistp(read);
                code.emit(OpCode.istore, id_addr);
            }
            default -> {
                error("syntax error in idlist()!");
            }
    	}
    }

    /* guida(-> epsilon)= ; EOF ] end option }
     * guida(->, id idlp)= ,   
    */
    private void idlistp(boolean read){  
        switch(look.tag){
            case ',' -> {
                match(',');
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if(id_addr == -1){
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
                if(read)
                    code.emit(OpCode.invokestatic, 0);
                code.emit(OpCode.istore, id_addr);
                idlistp(read);
                if(!read)   /* caso dell'assign, va ricaricato il valore sullo stack */
                    code.emit(OpCode.iload, id_addr);
            }
            case ';', ']', '}', Tag.EOF, Tag.END, Tag.OPTION -> {}
            default -> {
                error("syntax error in idlistp(): found " + look);
            }
        }
    }

    private void bexpr(int jump_label, boolean negate){    //guida = RELOP
        if(look.tag == Tag.RELOP || look.tag == Tag.AND || look.tag == Tag.OR || look.tag == Word.not.tag){
            OpCode comparator_type = find_comparator(negate);
            switch(comparator_type){
                case iand ->{
                    match(Tag.AND);
                    bexpr(jump_label, false);
                    bexpr(jump_label, false);
                }
                case ior -> {
                    match(Tag.OR);
                    int second_expr = code.newLabel();
                    bexpr(second_expr, false);
                    code.emitLabel(second_expr);
                    bexpr(jump_label, false);
                }
                case ineg -> {
                    match(Token.not.tag);
                    bexpr(jump_label, true);
                }
                default -> {
                    match(Tag.RELOP);
                    expr();
                    expr();
                    code.emit(comparator_type, jump_label);
                }
            }
        } else error("syntax error in bexpr(): missing RELOP token!");
    }

    /* ritorna il giusto opcode: nb: per avere una condizione di 
     * salto di un blocco devo avere l'opposto della condizione 
     * del linguaggio di partenza */
    private OpCode find_comparator(boolean negate){
        if(look.tag == Token.not.tag)
            return OpCode.ineg;
        switch(((Word)look).lexeme){
            case "&&": 
                return OpCode.iand;
            case "||":         
                return OpCode.ior;
            case "<>":  
                return negate ? OpCode.if_icmpne : OpCode.if_icmpeq;
            case "<=":
                return negate ? OpCode.if_icmple : OpCode.if_icmpgt;
            case ">=":
                return negate ? OpCode.if_icmpge : OpCode.if_icmplt;
            case "<":
                return negate ? OpCode.if_icmplt : OpCode.if_icmpge;
            case ">":
                return negate ? OpCode.if_icmpgt : OpCode.if_icmple;
            case "==":
                return negate ? OpCode.if_icmpeq : OpCode.if_icmpne;
            default:
                error("error in find_comparator() method!");
                break;
        }
        return null;
    }

    /*  guida(-> +(EL))= +          guida(-> *(EL))= *
     *  guida(-> -EE)= -            guida(-> /EE)= /
     *  guida(-> NUM)= NUM          guida(-> ID)= ID
     */
    private void expr() {
        switch(look.tag) {
            case '+' ->{
                match('+');
                match('(');
                exprlist(OpCode.iadd);
                match(')');
            }
            case '*' ->{
                match('*');
                match('(');
                exprlist(OpCode.imul);
                match(')');
            }
            case '-' -> {
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
            }
            case '/' -> {
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
            }
            case Tag.NUM -> {
                int value = ((NumberTok)look).number;
                match(Tag.NUM);
                code.emit(OpCode.ldc, value);
            }
            case Tag.ID -> {
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if(id_addr == -1){
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                } 
                match(Tag.ID);
                code.emit(OpCode.iload, id_addr);
            }
            default ->{
                error("syntax error in expr() method!");
            }
        }
    }

    private void exprlist(OpCode instruction){ // guida = + - * / NUM ID
        switch(look.tag){
            case '+', '-', '*', '/', Tag.NUM, Tag.ID -> {
                expr();
                if(instruction.equals(OpCode.invokestatic))
                    code.emit(instruction, 1);
                exprlistp(instruction);
            }
            default -> {
                error("syntax error in exprlist(): missing entry condition!");
            }
        }
    }

    /*  guida(-> ,EEL')= ,
     *  guida(-> epsilon)= ] )
     */
    private void exprlistp(OpCode instruction){
        switch(look.tag){
            case ',' -> {
                match(',');
                expr();
                if(instruction.equals(OpCode.invokestatic))
                    code.emit(instruction, 1);
                else
                    code.emit(instruction);
                exprlistp(instruction);
            }
            case ']', ')' -> {}
            default -> {
                error("syntax error in exprlistp(): missing ',' or ] or } token! Found: " + look);
            }
        }
    }
    public static void main(String[] args){
        String path = args.length > 0 ? args[0] : "prova.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator t = new Translator(new Lexer(), br);
            t.prog();
            System.out.println("Output generated!");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
