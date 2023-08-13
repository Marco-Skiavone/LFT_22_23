public class Commento {  // ES: 1.9
    public static void main(String[] args) {
        System.out.println(isCommento(args[0]));
    }

    public static boolean isCommento(String s){
        if(s == null || s.length() == 0)
            System.out.println("Errore! Stringa nulla o vuota.");
        int i = 0, state = 0;
        while(s != null && s.length() > 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {state = (ch == '/') ? 1 : -1;}
                case 1 -> {state = (ch == '*') ? 2 : -1;}
                case 2 -> {state = (ch == '*') ? 3 : (ch == 'a' || ch == '/') ? 2 : -1;}
                case 3 -> {
                    if(ch == '*')
                        state = 3;
                    else
                        state = (ch == '/') ? 4 : (ch == 'a') ? 2 : -1;
                }
                case 4 -> {state = -1;}
                default -> {state = -1;}
            }
        }
        return state == 4;
    }
}