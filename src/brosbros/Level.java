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
	int keyx, keyy;
	int moreUp = 0;
	int portal1x, portal1y;
	int portal2x, portal2y;
	int portal3x, portal3y;
	int portal4x, portal4y;
	int pickupHeartX, pickupHeartY;
	BufferedImage background = null;
	byte[][] levelData;
	boolean portalPair1IsShowing = false;
	boolean portalPair2IsShowing = false;
	boolean isUnderWater = false;
	int numberofkeys = 0;
	int bossx = 0; int bossy = 0;
	Color backgroundColor = Color.white;
	boolean lowGravity = false;
	boolean maySwapGravity = false;
	boolean gravitySwapped = false;
	int gravitySwapCounter = 0;
	public static int maxLevels = 35;
	
	public Level(GameLoop gameLoop, int level) throws Exception{
		this.levelNr = level;
		levelData = new byte[608][472];
		String fileName1 = null;
		String fileName2 = null;
		portalPair1IsShowing = false;
		portalPair2IsShowing = false;
		isUnderWater = false;
		numberofkeys = 0;
		maySwapGravity = false;
		gameLoop.bosses.clear();
		gameLoop.bullets.clear();
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
			pickupHeartX = 480;
			pickupHeartY = 700;
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
			pickupHeartX = 730;
			pickupHeartY = 700;
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
			portalPair1IsShowing = true;
			fileName1 = "2-2.png";
			fileName2 = "2-2.data";
		}
		if (level == 8){
			startx = 500;
			starty = GameFrame.height-330;
			doorx = 1170;
			doory = 600;
			fileName1 = "2-3.png";
			fileName2 = "2-3.data";
		}
		if (level == 9){
			startx = 130;
			starty = 620;
			doorx = 1100;
			doory = 100;
			fileName1 = "2-4.png";
			fileName2 = "2-4.data";
		}
		if (level == 10){
			startx = 130;
			starty = 70;
			doorx = 1100;
			doory = 800;
			fileName1 = "2-5.png";
			fileName2 = "2-5.data";
		}
		if (level == 11){
			startx = 950;
			starty = 20;
			doorx = 530;
			doory = 800;
			fileName1 = "3-1.png";
			fileName2 = "3-1.data";
			pickupHeartX = 480;
			pickupHeartY = 700;
		}
		if (level == 12){
			startx = 80;
			starty = 90;
			doorx = 950;
			doory = 530;
			isUnderWater  = true;
			fileName1 = "3-2.png";
			fileName2 = "3-2.data";
		}
		if (level == 13){
			startx = 155;
			starty = 590;
			doorx = 1008;
			doory = 460;
			fileName1 = "3-3.png";
			fileName2 = "3-3.data";
		}
		if (level == 14){
			startx = 755;
			starty = 80;
			doorx = 50;
			doory = 340;
			keyx = 50;
			keyy = 620;
			fileName1 = "3-4.png";
			fileName2 = "3-4.data";
			numberofkeys = 1;
		}
		if (level == 15){
			startx = 35;
			starty = 80;
			doorx = 30;
			doory = 865;
			keyx = 500;
			keyy = 750;
			numberofkeys = 1;
			fileName1 = "3-5.png";
			fileName2 = "3-5.data";
			gameLoop.addBoss(1,40,doory-55); //Type, x, y
			//bossx = 40;
			//bossy = doory-55;
			//numberofBosses = 1;
		}
		if (level == 16){
			startx = 35;
			starty = 80;
			doorx = 830;
			doory = 65;
			pickupHeartX = 730;
			pickupHeartY = 700;
			portal1x = 290;
			portal1y = 440;
			portal2x = 215;
			portal2y = 440;
			portal3x = 590;
			portal3y = 440;
			portal4x = 515;
			portal4y = 440;
			portalPair1IsShowing = true;
			portalPair2IsShowing = true;
			keyx = 400;
			keyy = 750;
			numberofkeys = 1;
			fileName1 = "4-1.png";
			fileName2 = "4-1.data";
		}
		if (level == 17){
			startx = 175;
			starty = 230;
			doorx = 1020;
			doory = 90;
			fileName1 = "4-2.png";
			fileName2 = "4-2.data";
		}
		if (level == 18){
			startx = 590;
			starty = 10;
			doorx = 1020;
			doory = 175;
			fileName1 = "4-3.png";
			fileName2 = "4-3.data";
			lowGravity = true;
		}
		if (level == 19){
			startx = 200;
			starty = 200;
			doorx = 100;
			doory = 250;
			fileName1 = "4-4.png";
			fileName2 = "4-4.data";
			maySwapGravity = true;
		}
		if (level == 20){
			startx = 119;
			starty = -10;
			doorx = 400;
			doory = 800;
			fileName1 = "4-5.png";
			fileName2 = "4-5.data";
			lowGravity = true;
		}
		if (level == 21){
			pickupHeartX = 720;
			pickupHeartY = 380;
			startx = 200;
			starty = 10;
			doorx = 1150;
			doory = 600;
			fileName1 = "5-1.png";
			fileName2 = "5-1.data";
		}
		if (level == 22){
			startx = 200;
			starty = 100;
			doorx = 1150;
			doory = 600;
			fileName1 = "5-2.png";
			fileName2 = "5-2.data";
		}
		if (level == 23){
			startx = 50;
			starty = 50;
			doorx = 1175;
			doory = 175;
			fileName1 = "5-3.png";
			fileName2 = "5-3.data";
		}
		if (level == 24){
			startx = 50;
			starty = 50;
			doorx = 1175;
			doory = 175;
			fileName1 = "5-4.png";
			fileName2 = "5-4.data";
		}
		if (level == 25){
			startx = 50;
			starty = 50;
			doorx = 1175;
			doory = 750;
			fileName1 = "5-5.png";
			fileName2 = "5-5.data";
			gameLoop.addBoss(2,500,doory-30); //Type, x, y
			gameLoop.addBoss(2,700,doory-30); //Type, x, y
			gameLoop.addBoss(2,900,doory-30); //Type, x, y
			gameLoop.addBoss(2,1100,doory-30); //Type, x, y
		}
		if (level == 26){
			startx = 50;
			starty = 25;
			doorx = 250;
			doory = 675;
			fileName1 = "6-1.png";
			fileName2 = "6-1.data";
			pickupHeartX = 700;
			pickupHeartY = 790;
		}
		if (level == 27){
			startx = 50;
			starty = 25;
			doorx = 500;
			doory = 250;
			fileName1 = "6-2.png";
			fileName2 = "6-2.data";
			isUnderWater  = true;
		}
		if (level == 28){
			startx = 900;
			starty = 200;
			doorx = 600;
			doory = 350;
			fileName1 = "6-3.png";
			fileName2 = "6-3.data";
		}
		if (level == 29){
			startx = 100;
			starty = 800;
			doorx = 1100;
			doory = 700;
			fileName1 = "6-4.png";
			fileName2 = "6-4.data";
		}
		if (level == 30){
			startx = 259;
			starty = 80;
			doorx = 99;
			doory = 499;
			fileName1 = "6-5.png";
			fileName2 = "6-5.data";
			maySwapGravity = true;
			gameLoop.addBoss(3,550,700); //Type, x, y
		}
		if (level == 31){
			startx = 200;
			starty = 450;
			doorx = 1000;
			doory = 450;
			fileName1 = "7-1.png";
			fileName2 = "7-1.data";
			pickupHeartX = 1135;
			pickupHeartY = 575;
		}
		if (level == 32){
			startx = 1100;
			starty = 100;
			doorx = 1100;
			doory = 808;
			fileName1 = "7-2.png";
			fileName2 = "7-2.data";
		}
		if (level == 33){
			startx = 200;
			starty = 150;
			doorx = 100;
			doory = 808;
			fileName1 = "7-3.png";
			fileName2 = "7-3.data";
		}
		if (level == 34){
			startx = 200;
			starty = 150;
			doorx = 1100;
			doory = 70;
			fileName1 = "7-4.png";
			fileName2 = "7-4.data";
		}
		if (level == 35){
			startx = 200;
			starty = 150;
			doorx = 1100;
			doory = 750;
			fileName1 = "7-5.png";
			fileName2 = "7-5.data";
			isUnderWater  = true;
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
		gameLoop.yellowKey.x = keyx;
		gameLoop.yellowKey.y = keyy;
		gameLoop.blackKey.x = keyx;
		gameLoop.blackKey.y = keyy;
		gameLoop.portal1.x = portal1x;
		gameLoop.portal1.y = portal1y;
		gameLoop.portal2.x = portal2x;
		gameLoop.portal2.y = portal2y;
		gameLoop.portal3.x = portal3x;
		gameLoop.portal3.y = portal3y;
		gameLoop.portal4.x = portal4x;
		gameLoop.portal4.y = portal4y;
		gameLoop.pickupHeart.x = pickupHeartX;
		gameLoop.pickupHeart.y = pickupHeartY;
		//gameLoop.boss1.x = bossx;
		//gameLoop.boss1.y = bossy;
	}
	public int getX(int playerNr){
		int x = startx;
		if (playerNr == 2) x += 50;
		if (playerNr == 3) x += 90;
		if (levelNr == 11 && playerNr == 2) x -= 35;
		if (levelNr == 11 && playerNr == 3) x -= 105;		
		if (levelNr == 19 && playerNr == 3) x -= 90;
		return x;
	}
	public int getY(int playerNr){
		int y = starty;
		if (playerNr == 2) y += moreUp;
		if (playerNr == 3) y += moreUp;
		return y;
	}
}
