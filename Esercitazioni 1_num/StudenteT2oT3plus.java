public class StudenteT2oT3plus {    // ES 1.4
    public static void main(String[] args) {
        System.out.println(isT3orT4(args[0]));
    }
    
    public static boolean isT3orT4(String s){
        if(s==null || s.length() == 0)
            System.err.println("Errore! Stringa nulla o vuota.");
        int i = 0, state = 0;
        while(s != null && s.length() > 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {
                    if(ch == ' ')
                        state = 0;
                    else
                        state = !Character.isDigit(ch) ? -1  : (ch-'0')%2 == 0 ? 2 : 1;
                }
                case 1 -> { // caso n dispari
                    if(((Character)ch).compareTo(' ') == 0)
                        state = 3;
                    else if(Character.isDigit(ch))
                        state = (ch - '0') % 2 == 0 ? 2 : 1;
                    else
                        state = (ch >= 'L' && ch <= 'Z') ? 5 : -1;
                }
                case 2 -> { // caso n pari
                    if(((Character)ch).compareTo(' ') == 0)
                        state = 4;
                    else if(Character.isDigit(ch))
                            state = (ch - '0') % 2 == 0 ? 2 : 1;
                        else
                            state = (ch >= 'A' && ch <= 'K') ? 5 : -1;
                }
                case 3 -> { 
                    state = (ch == ' ') ? 3 : (ch >= 'L' && ch <= 'Z') ? 5 : -1;}
                case 4 -> { 
                    state = (ch == ' ') ? 4 : (ch >= 'A' && ch <= 'K') ? 5 : -1;}
                case 5 -> {state = (ch == ' ') ? 6 : (Character.isLetter(ch) && Character.isLowerCase(ch)) ? 5 : -1;}
                case 6 -> {state = (ch == ' ') ? 6 : (Character.isUpperCase(ch)) ? 5 : -1;}
                default -> {state = -1;}
            }
        }
        return state == 6 || state == 5;
    }
}