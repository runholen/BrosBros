package brosbros;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Vector;

import javax.imageio.ImageIO;

public class Level {

	
	int levelNr;
	int startx, starty;
	int doorx, doory;
	int moreUp = 0;
	int portal1x, portal1y;
	int portal2x, portal2y;
	BufferedImage background = null;
	byte[][] levelData;
	boolean portalIsShowing = false;
	boolean isUnderWater = false;
	Color backgroundColor = Color.white;
	public static int maxLevels = 12;
	
	public Level(GameLoop gameLoop, int level) throws Exception{
		this.levelNr = level;
		levelData = new byte[608][472];
		String fileName1 = null;
		String fileName2 = null;
		portalIsShowing = false;
		isUnderWater = false;
		if (level == 0){
			startx = -100;
			starty = -100;
			doorx = -100;
			doory = -100;
		}
		if (level == 1){
			startx = 380;
			starty = 750;
			doorx = 380;
			doory = 8;
			fileName1 = "1-1.png";
			fileName2 = "1-1.data";
		}
		if (level == 2){
			startx = 260;
			starty = 800;
			doorx = 270;
			doory = 35;
			fileName1 = "1-2.png";
			fileName2 = "1-2.data";
		}
		if (level == 3){
			startx = 10;
			starty = GameFrame.height-200;
			doorx = 10;
			doory = GameFrame.height-980;
			fileName1 = "1-3.png";
			fileName2 = "1-3.data";
		}
		if (level == 4){
			startx = 10;
			starty = GameFrame.height-125;
			moreUp = 5;
			doorx = 10;
			doory = GameFrame.height-960;
			fileName1 = "1-4.png";
			fileName2 = "1-4.data";
		}
		if (level == 5){
			startx = 10;
			starty = GameFrame.height-170;
			moreUp = -8;
			doorx = 1150;
			doory = GameFrame.height-100;
			fileName1 = "1-5.png";
			fileName2 = "1-5.data";
		}
		if (level == 6){
			startx = 500;
			starty = GameFrame.height-130;
			doorx = 462;
			doory = GameFrame.height-910;
			fileName1 = "2-1.png";
			fileName2 = "2-1.data";
		}
		if (level == 7){
			startx = 500;
			starty = GameFrame.height-330;
			doorx = 920;
			doory = GameFrame.height-670;
			portal1x = 280;
			portal1y = 340;
			portal2x = 1040;
			portal2y = 620;
			portalIsShowing = true;
			fileName1 = "2-2.png";
			fileName2 = "2-2.data";
		}
		if (level == 8){
			startx = 500;
			starty = GameFrame.height-330;
			doorx = 1170;
			doory = 600;
			portalIsShowing = false;
			fileName1 = "2-3.png";
			fileName2 = "2-3.data";
		}
		if (level == 9){
			startx = 130;
			starty = 620;
			doorx = 1100;
			doory = 100;
			portalIsShowing = false;
			fileName1 = "2-4.png";
			fileName2 = "2-4.data";
		}
		if (level == 10){
			startx = 130;
			starty = 70;
			doorx = 1100;
			doory = 800;
			portalIsShowing = false;
			fileName1 = "2-5.png";
			fileName2 = "2-5.data";
		}
		if (level == 11){
			startx = 950;
			starty = 20;
			doorx = 530;
			doory = 800;
			portalIsShowing = false;
			fileName1 = "3-1.png";
			fileName2 = "3-1.data";
		}
		if (level == 12){
			startx = 80;
			starty = 90;
			doorx = 950;
			doory = 530;
			portalIsShowing = false;
			isUnderWater  = true;
			fileName1 = "3-2.png";
			fileName2 = "3-2.data";
		}
		if (fileName1 != null){
			//Original is 608x472
			background = ImageIO.read(getClass().getResourceAsStream("/resources/"+fileName1));
			//Now it becomes 1216x944
			background = GameFrame.scale(background);
		}
		if (fileName2 != null){
			InputStream in = getClass().getResourceAsStream("/resources/"+fileName2);
			byte[] b = new byte[286976]; //608x472
			int t = -1;
			while (true){
				int i = in.read();
				if (i == -1) break;
				b[++t] = (byte)i;
			}
			System.out.println("Level data: "+b.length);
			int i = -1;
			for (int y = 0; y < 472; y++){
				for (int x = 0; x < 608; x++){
					levelData[x][y] = b[++i];
				}
			}
		}
		for (PlayerObject player : gameLoop.players){
			player.x = getX(player.playerNr);
			player.y = getY(player.playerNr);
		}
		gameLoop.door.x = doorx;
		gameLoop.door.y = doory;
		gameLoop.portal1.x = portal1x;
		gameLoop.portal1.y = portal1y;
		gameLoop.portal2.x = portal2x;
		gameLoop.portal2.y = portal2y;
		
	}
	public int getX(int playerNr){
		int x = startx;
		if (playerNr == 2) x += 50;
		if (playerNr == 3) x += 90;
		if (levelNr == 11 && playerNr == 2) x -= 35;
		if (levelNr == 11 && playerNr == 3) x -= 105;		
		return x;
	}
	public int getY(int playerNr){
		int y = starty;
		if (playerNr == 2) y += moreUp;
		if (playerNr == 3) y += moreUp;
		return y;
	}
}
