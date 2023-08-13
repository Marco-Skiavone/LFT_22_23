public class RiconosceMarco {   // ES: 1.7
    public static void main(String[] args) {    
        System.out.println(riconosceMarco(args[0]));
    }

    // Le stringhe da accettare sono le mutazioni di un carattere (o 0) della stringa "Marco"
    public static boolean riconosceMarco(String s){
        if(s == null || s.length() == 0)
            System.out.println("Errore! Stringa nulla o vuota.");
        int i = 0, state = 0;
        while(s != null && s.length() > 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {state = (ch == 'M') ? 1 : 6;}
                case 1 -> {state = (ch == 'a') ? 2 : 7;}
                case 2 -> {state = (ch == 'r') ? 3 : 8;}
                case 3 -> {state = (ch == 'c') ? 4 : 9;}
                case 4 -> {state = 5;}
                case 5 -> {state = -1;}
                case 6 -> {state = (ch == 'a') ? 7 : -1;}
                case 7 -> {state = (ch == 'r') ? 8 : -1;}
                case 8 -> {state = (ch == 'c') ? 9 : -1;}
                case 9 -> {state = (ch == 'o') ? 5 : -1;}
                default -> {state = -1;}
            }
        }
        return state == 5;
    }
}