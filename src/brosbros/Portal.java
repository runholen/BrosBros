package brosbros;

import javax.imageio.ImageIO;

public class Portal extends GameObject{
	boolean exit;

	public Portal(boolean exit) throws Exception{
		this.exit = exit;
		image = ImageIO.read(getClass().getResourceAsStream("/resources/portal.png"));
		image = GameFrame.scale(image);
		if (exit) flipped = GameFrame.flip(image); 
	}
}
