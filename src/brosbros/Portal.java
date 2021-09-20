package brosbros;

import javax.imageio.ImageIO;

public class Portal extends GameObject{
	boolean exit;
	int pairNr;
	
	public Portal(int pairNr,boolean exit) throws Exception{
		this.pairNr = pairNr;
		this.exit = exit;
		if (pairNr == 1){
			image = ImageIO.read(getClass().getResourceAsStream("/resources/portal.png"));
			image = GameFrame.scale(image);
		}
		else if (pairNr == 2){
			image = ImageIO.read(getClass().getResourceAsStream("/resources/portal2.png"));
			image = GameFrame.scale(image);
		}
		if (exit) flipped = GameFrame.flip(image); 
	}
}
