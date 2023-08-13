public class Identificatori {   //ES 1.2
    public static void main(String[] args) {
        System.out.println(identifier(args[0]));
    }

    public static boolean identifier(String s){
        if(s==null || s.length() == 0)
            System.err.println("Errore! Stringa nulla o vuota.");
        int i = 0, state = 0;
        while(s != null && s.length() != 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {state = (ch == '_') ? 1 : Character.isLetter(ch) ? 2 : -1;}
                case 1 -> {state = (Character.isLetter(ch) || Character.isDigit(ch)) ? 2 : (ch == '_') ? 1 : -1;}
                case 2 -> {state = (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') ? 2 : -1;}
                default -> {state = -1;}
            }
        }
        return state == 2;
    }
}