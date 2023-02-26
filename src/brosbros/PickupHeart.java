package brosbros;

import javax.imageio.ImageIO;

public class PickupHeart extends GameObject{

	public PickupHeart() throws Exception{
		image = ImageIO.read(getClass().getResourceAsStream("/resources/pick-up-heart.png"));
		image = GameFrame.scale(image);
		width = 20;
		height = 20;
	}
}
