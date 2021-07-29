package brosbros;

import javax.imageio.ImageIO;

public class Key extends GameObject{

	public Key() throws Exception{
		image = ImageIO.read(getClass().getResourceAsStream("/resources/key.png"));
		image = GameFrame.scale(image);
	}
}
