public class NumberTok extends Token {
	public int number;
	public NumberTok(int n){super(Tag.NUM); number = n;}
	public String toString(){return "<" + Tag.NUM + ", " + number + ">";}
}