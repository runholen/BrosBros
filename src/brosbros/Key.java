package brosbros;

import javax.imageio.ImageIO;

public class Key extends GameObject{

	public Key(boolean black) throws Exception{
		if (black) image = ImageIO.read(getClass().getResourceAsStream("/resources/key2.png"));
		else image = ImageIO.read(getClass().getResourceAsStream("/resources/key.png"));
		image = GameFrame.scale(image);
	}
}
