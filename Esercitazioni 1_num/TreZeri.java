public class TreZeri{   // ES 1.1
    public static void main(String[] args) {
        System.out.println((scan(args[0]) ? "OK" : "NOPE"));
    }

    public static boolean scan(String s){
        if(s == null || s.length() == 0)
            System.out.println("Errore! Stringa nulla o vuota.");
        int state = 0;
        int i = 0;
        while(state >= 0 && i < s.length()){
            char ch = s.charAt(i++);
            switch(state){
                case 0:
                    state = (ch == '0') ? 1 : (ch == '1') ? 0 : -1;
                    break;
                case 1:
                    state = (ch == '0') ? 2 : (ch == '1') ? 0 : -1;
                    break;
                case 2:
                    state = (ch == '0') ? 3 : (ch == '1') ? 0 : -1;
                    break;
                case 3: 
                    state = (ch == '0' || ch == '1') ? 3 : -1;
                    break;
                case 4: 
                    break;
                default: 
                    state = -1;
                    break;
            }
        }
        return state == 3;
    }
}