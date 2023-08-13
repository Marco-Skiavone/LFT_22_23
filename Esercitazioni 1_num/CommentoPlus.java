public class CommentoPlus {  // ES: 1.10
    public static void main(String[] args) {
        System.out.println(isCommentoPlus(args[0]));
    }

    public static boolean isCommentoPlus(String s){
        if(s == null || s.length() == 0)
            System.out.println("Errore! Stringa nulla o vuota.");
        int i = 0, state = 0;
        while(s != null && s.length() > 0 && state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0 -> {state = (ch == '/') ? 1 : (ch == 'a' || ch == '*') ? 0 : -1;}
                case 1 -> {
                    if(ch == '/')
                        state = 1;
                    else
                        state = (ch == '*') ? 2 : (ch == 'a') ? 0 : -1;
                }
                case 2 -> {state = (ch == '/' || ch == 'a') ? 2 : (ch == '*') ? 3 : -1;}
                case 3 -> {
                    if(ch == '*')
                        state = 3;
                    else
                        state = (ch == '/') ? 4 : (ch == 'a') ? 2 : -1;
                }
                case 4 -> {state = (ch == 'a' || ch == '*') ? 4 : (ch == '/') ? 1 : -1;}
                default -> {state = -1;}
            }
        }
        return state == 0 || state == 1 || state == 4;
    }
}