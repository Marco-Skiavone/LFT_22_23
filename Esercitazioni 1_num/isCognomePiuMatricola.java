public class isCognomePiuMatricola {
    public static void main(String[] args) {
        System.out.println(isCognomeEMatricola(args[0]));
    }

    public static boolean isCognomeEMatricola(String s){
        if(s == null || s.length() == 0)
            System.out.println("Errore! Stringa nulla o vuota.");
        int state = 0, i = 0;
        while(s != null && s.length() > 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {state = (ch >= 'A' && ch <= 'K') ? 1 : (ch >= 'L' && ch <= 'Z') ? 2 : -1;}
                case 1 -> {
                    if(Character.isDigit(ch)) 
                        state = ((ch-'0') % 2 == 0) ? 3 : 5;
                    else
                        state = (Character.isLetter(ch)) ? 1 : -1;
                }
                case 2 -> {
                    if(Character.isDigit(ch)) 
                        state = ((ch-'0') % 2 == 0) ? 4 : 6;
                    else
                        state = (Character.isLetter(ch)) ? 1 : -1;
                }
                case 3 -> {state = (!Character.isDigit(ch)) ? -1 : ((ch-'0') % 2 == 0) ? 3 : 5;}    // caso pari
                case 4 -> {state = (!Character.isDigit(ch)) ? -1 : ((ch-'0') % 2 == 0) ? 6 : 4;}    // caso dispari
            }
        }
        return state == 3 || state == 4;
    }
}