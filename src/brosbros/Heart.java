package brosbros;

import javax.imageio.ImageIO;

public class Heart extends GameObject{

	public Heart() throws Exception{
		image = ImageIO.read(getClass().getResourceAsStream("/resources/hjerte.png"));
		image = GameFrame.scale(image);
	}
}
