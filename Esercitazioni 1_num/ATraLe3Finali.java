public class ATraLe3Finali {    // ES: 1.6
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
                    state = (ch == 'a') ? 1 : (ch == 'b') ? 0 : -1;
                }
                case 1 -> {
                    state = (ch == 'a') ? 1 : (ch == 'b') ? 2 : -1;
                }
                case 2 -> {
                    state = (ch == 'a') ? 1 : (ch == 'b') ? 3 : -1;
                }
                case 3 -> {
                    state = (ch == 'a') ? 1 : (ch == 'b') ? 0 : -1;
                }
                default -> {
                    state = -1;
                }
            }
        }
        return state >= 1;
    }
}