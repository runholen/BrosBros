package brosbros;

public class Collition {

	boolean noCollition = true;
	boolean ees = false;
	boolean ladder = false;
	boolean bottom = false;
	boolean top = false;
	boolean left = false;
	boolean right = false;
	boolean middle = false;
	
	int x,y;
	
	public Collition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public boolean noCollitionIgnoreLadder(){
		return noCollition || (!bottom && !top && !left && !right && !middle);
	}
	public boolean someCollitionIgnoreLadder(){
		return noCollitionIgnoreLadder() == false;
	}
	
	public boolean onlyLadder(){
		return noCollition == false && ladder && !bottom && !top && !left && !right && !middle;
	}
	
	public void validate(){
		if (ees || ladder || bottom || top || left || right || middle){
			noCollition = false;
		}
	}
}
;