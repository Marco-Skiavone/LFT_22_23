public class IsFloatingPoint {  // ES: 1.8
    public static void main(String[] args) {
        System.out.println(isAFinale(args[0]));
    }

    public static boolean isAFinale(String s){
        if(s == null || s.length() == 0)
            System.out.println("Errore! Stringa nulla o vuota.");
        int i = 0, state = 0;
        while(s != null && s.length() > 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {
                    if(!Character.isDigit(ch))
                        state = (ch == '.') ? 2 : (ch == '+' || ch == '-') ? 1 : -1;
                    else
                        state = 3;
                }
                case 1 -> {state = (ch == '.') ? 2 : (Character.isDigit(ch)) ? 3 : -1;}
                case 2 -> {state = (Character.isDigit(ch)) ? 4 : -1;}
                case 3 -> {
                    if(!Character.isDigit(ch))
                        state = (ch == '.') ? 2 : (ch == 'e') ? 5 : -1;
                    else
                        state = 3;
                }
                case 4 -> {state = (Character.isDigit(ch)) ? 4 : (ch == 'e') ? 5 : -1;}
                case 5 -> {
                    if(!Character.isDigit(ch))
                        state = (ch == '.') ? 7 : (ch == '+' || ch == '-') ? 6 : -1;
                    else
                        state = 8;
                }
                case 6 -> {state = (ch == '.') ? 7 : (Character.isDigit(ch)) ? 8 : -1;}
                case 7 -> {state = (Character.isDigit(ch)) ? 9 : -1;}
                case 8 -> {state = (Character.isDigit(ch)) ? 8 : (ch == '.') ? 7 : -1;}
                case 9 -> {state = (Character.isDigit(ch)) ? 9 : -1;}
            }
        }
        return state == 3 || state == 4 || state == 8 || state == 9;
    }
}