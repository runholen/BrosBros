package brosbros;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Door extends GameObject{

	BufferedImage alternateImage1;
	BufferedImage alternateImage2;
	
	public Door() throws Exception{
		image = ImageIO.read(getClass().getResourceAsStream("/resources/door.png"));
		image = GameFrame.scale(image);
		alternateImage1 = ImageIO.read(getClass().getResourceAsStream("/resources/axe.png"));
		alternateImage1 = GameFrame.scale(alternateImage1);
		alternateImage2 = ImageIO.read(getClass().getResourceAsStream("/resources/button.png"));
		alternateImage2 = GameFrame.scale(alternateImage2);
	}
}