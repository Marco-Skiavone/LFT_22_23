public class StudenteT2oT3{ // ES 1.3
    public static void main(String[] args) {
        System.out.println(isT3orT4(args[0]));
    }
    
/* NOTA: 
 * considero solo le lettere maiuscole come prime lettere del cognome!! 
 * Ho scelto arbitrariamente perche' l'esercizio non dice nulla a riguardo 
*/
    public static boolean isT3orT4(String s){
        if(s==null || s.length() == 0)
            System.err.println("Errore di input stringa");
        int i = 0;
        int state = 0;
        while(s != null && s.length() != 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {state = !Character.isDigit(ch) ? -1  : (ch-'0')%2 == 0 ? 2 : 1;}
                case 1 -> { // caso n dispari, aspetto l-z
                    if(Character.isDigit(ch))
                        state = (ch - '0') % 2 == 0 ? 2 : 1;
                    else
                        state = (ch >= 'L' && ch <= 'Z') ? 3 : -1; // ho segnato solo L-Z maiuscoli arbitrariamente
                }
                case 2 -> { // caso n pari, aspetto a-k
                    if(Character.isDigit(ch))
                        state = (ch - '0') % 2 == 0 ? 2 : 1;
                    else
                        state = (ch >= 'A' && ch <= 'K') ? 3 : -1; // ho segnato solo A-K maiuscoli arbitrariamente
                }
                case 3 -> {state = (Character.isLetter(ch)) ? 3 : -1;}
                default -> {state = -1;}
            }
        }
        return state == 3;
    }
}