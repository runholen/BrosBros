package brosbros;

import java.awt.Toolkit;
import java.util.Properties;

import kbxm.XmPlayer;

public class GameLoop extends Thread{

	int nrOfPlayers = 3;
	Level level;	
	GameFrame gameFrame;
	PlayerObject player1;
	PlayerObject player2;
	PlayerObject player3;
	PlayerObject[] players;
	BossObject boss1;
	Door door;
	Key yellowKey;
	Key blackKey;
	Portal portal1;
	Portal portal2;
	Portal portal3;
	Portal portal4;
	int gravitystep = 4;
	int gamespeed = 20;
	int nextLevelCounter = 0;
	XmPlayer xmPlayer = null;
	
	public static void main(String[] args) throws Exception {
		//Doesn't seem to work, as uiscale must be disabled before getScreenSize() is called
		//int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		//System.out.println("Screen height: "+height);
		//if (height < 1300){ //Disable dpi scaling on windows on normal screens!
			System.out.println("Disabling uiScale");
			Properties props = System.getProperties();
			props.setProperty("sun.java2d.uiScale.enabled","false");
		//}
		GameLoop gl = new GameLoop();
		gl.start();
	}
	
	public GameLoop() throws Exception {
		player1 = new PlayerObject(this,1,"Frode");
		player2 = new PlayerObject(this,2,"Fritjof");
		player3 = new PlayerObject(this,3,"Liv");
		boss1 = new BossObject(this,1,"Blacho");
		players = new PlayerObject[nrOfPlayers];
		players[0] = player1;
		if (nrOfPlayers >= 2) players[1] = player2;
		if (nrOfPlayers >= 3) players[2] = player3;
		door = new Door();
		yellowKey = new Key(false);
		blackKey = new Key(true);
		portal1 = new Portal(1,false);
		portal2 = new Portal(1,true);
		portal3 = new Portal(2,false);
		portal4 = new Portal(2,true);
		//level = new GameOverScreen(this); //For debugging of GameOverScreen
		level = new Intro(this);
		gameFrame = new GameFrame(this);
		System.out.println("GameFrame initialized");
		checkNewMusic();
	}
	public void run(){
		while(true){
			if (level instanceof Intro){
				((Intro)level).advance();
			}
			else{
				if (nextLevelCounter == 0){
					for (PlayerObject player : players){
						move(player);
						if (player.enters(level,door)) nextLevelCounter = 1;
						else if (level.numberofkeys > 0 && player.enters(null, yellowKey)){
							level.numberofkeys--;
						}
					}
				}
				if (nextLevelCounter > 0){
					if (nextLevelCounter == 150) nextLevel();
					else nextLevelCounter++;
				}
				boolean allPlayersHaveDied = true;
				for (PlayerObject player : players){
					if (player.lives > 0 || player.dying == -1 || player.dying > 0) allPlayersHaveDied = false;
					if (player.teleportCooldown > 0) player.teleportCooldown--;
					if (player.dying > 0 && player.y+player.getHeight() >= GameFrame.height - 1){
						player.dying--;
						if (player.dying == 0 && player.lives > 0){
							player.lives--;
							player.hitpoints = PlayerObject.max_hitpoints;
							player.x = level.getX(player.playerNr);
							player.y = level.getY(player.playerNr);
							player.dying = -1;
						}
					}
					else if (player.enters(null,portal1) && level.portalPair1IsShowing && player.teleportCooldown == 0){
						player.teleport(portal2,false);
					}
					else if (player.enters(null,portal2) && level.portalPair1IsShowing && player.teleportCooldown == 0){
						player.teleport(portal1,true);
					}
					else if (player.enters(null,portal3) && level.portalPair1IsShowing && player.teleportCooldown == 0){
						player.teleport(portal4,false);
					}
					else if (player.enters(null,portal4) && level.portalPair1IsShowing && player.teleportCooldown == 0){
						player.teleport(portal3,true);
					}
					
				}
				if (level.numberofBosses > 0){
					boss1.move(level.levelNr);
					for (PlayerObject player : players){
						if (player.collidesWith2(boss1)){
							player.hitpoints--;
							boss1.shove(player);
							if (player.hitpoints <= 0) player.dying = 70;
						}
					}
				}
				if (allPlayersHaveDied){
					try{
						for (PlayerObject p : players){
							p.hitpoints = PlayerObject.max_hitpoints;
							p.lives = 5;
							p.dying = -1;
						}
						level = new GameOverScreen(GameLoop.this);
						checkNewMusic();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
			gameFrame.repaint();
			try{Thread.sleep(gamespeed);}catch(InterruptedException ex){}
		}
	}

	public synchronized void move(PlayerObject player){
		player.handleInput(level);
		player.touchingGround = false;
		int dy = 0;
		int newx = player.x + player.mx;
		int newy = player.y + player.my;
		//if (player.mx != 0 || player.my != 0){
		//	System.out.println(player.playerNr+": "+player.mx+" "+player.my+" "+(player.ghostCollition!=null));
		//}
		Collition collition = new Collition(0,0);
		if (player.isDying() == false){
			collition = collidesBackground(player,newx,newy);
			//If collition, try to move only one pixel in x direction instead of speedx
			if (collition.someCollitionIgnoreLadder() && Math.abs(player.mx) > 0){
				int testmx = player.mx > 0 ? 1 : -1;
				Collition test = collidesBackground(player,player.x+testmx,newy);
				if (test.noCollitionIgnoreLadder()){
					player.mx = testmx;
					newx = player.x + player.mx;
					collition = test;
				}
			}
			player.ghostCollition = collition;
			if (collition.ees) player.dying = 70;
		}
		if (collition.noCollitionIgnoreLadder()){ //Handle gravity etc
			if (player.my < 0 && level.isUnderWater){
				if (player.y <= 0) player.my = 0;
				if (player.swimUp == false) player.my = 1;
			}
			else if (player.my > 0){
				if (level.isUnderWater){
					player.my = 2;
				}
				else{
					if (player.my == 4) player.my = 5;
					else if (player.my == 3) player.my = 4;
					else if (player.my == 2) player.my = 3;
					else if (player.my == 1) player.my = 2;
				}
			}
			else if (player.my == 0){
				if (player.floating > 0 && player.jumping){
					player.floating--;
					if (player.floating == 0) player.my = 1;
				}
				else{
					if (player.floatValue > 0 && player.touchingGround == false && player.jumping){
						player.floating = player.floatValue;
						player.jumping = false;
					}
					else{
						player.my = 1;
						player.jumping = false;
					}
				}
			}
			if (player.my < 0){ //Jumping
				player.gravityCounter--;
				if (player.gravityCounter == 0){
					player.my++;
					player.gravityCounter = player.gravityStep;
				}
			}
			if (player.my > 0){ //Falling
				newy = player.y + player.my; //my may be changed, recalculate newy
				if (player.isDying() == false){
					collition = collidesBackground(player,newx,newy);
					if (collition.onlyLadder() && player.downPressed == false){ //Stop on ladder
						player.my = 0;
						player.touchingGround = true;
						collition.noCollition = true;
					}
					else if (collition.someCollitionIgnoreLadder()){
						player.my = 0;
						player.touchingGround = true;
						newy = player.y + player.my;
						collition = collidesBackground(player,newx,newy);		
					}
				}
			}
		}
		else{ //Collition detected
			//Climbing when moving right
			if (player.my == 0 && player.mx > 0 && collition.right && collition.bottom){
				Collition test = collidesBackground(player, newx, newy-4);
				if (test.noCollitionIgnoreLadder()){
					dy = -player.speedx;
					collition = test;
				}
			}
			//Climbing when moving left
			if (player.my == 0 && player.mx < 0 && collition.left && collition.bottom){
				Collition test = collidesBackground(player, newx, newy-4);
				if (test.noCollitionIgnoreLadder()){
					dy = -player.speedx;
					collition = test;
				}
			}
		}
		
		if (collition.noCollitionIgnoreLadder() == false){
			player.mx = 0;
			player.my = 0;
		}
		//Now, move the player
		player.x += player.mx;
		player.y += player.my+dy;
		player.previousCollition = collition;
		//Screen bounds
		if (player.x < 0) player.x = 0;
		if (player.x > GameFrame.width - player.getWidth()) player.x = GameFrame.width-player.getWidth();
		if (player.y > GameFrame.height - player.getHeight()) player.y = GameFrame.height-player.getHeight();
	}
	
	public Collition collidesBackground(GameObject player, int newx, int newy){
		Collition collition = new Collition(newx,newy);
		for (int y = 20; y < player.getHeight(); y++){
			for (int x = 0; x < player.getWidth(); x++){
				int px = newx + x;
				int py = newy + y;
				if (py < 0) py = 0;
				if (py > gameFrame.height) py = gameFrame.height;
				if (px < 0) px = 0;
				if (px > gameFrame.width-player.getWidth()) px = gameFrame.width-player.getWidth();
				px = px / 2;
				py = py / 2;
				if (px >= level.levelData.length) continue;
				if (py >= level.levelData[0].length) continue;
				if (level.levelData[px][py] == 3){
					collition.ees = true;
				}
				if (level.levelData[px][py] == 2 && y >= 50){
					collition.ladder = true;
				}
				if (level.levelData[px][py] == 1){
					if (y > 50) collition.bottom = true;
					if (y < 30) collition.top = true;
					if (x < 10) collition.left = true;
					if (x > 30) collition.right = true;
					if (x >= 10 && x <= 30 && y >= 30 && y <= 50) collition.middle = true;
				}
			}
		}
		collition.validate();
		return collition;
	}
	
	public void nextLevel() {
		try{
			if (level.levelNr < Level.maxLevels){
				level = new Level(this,level.levelNr+1);
				gameFrame.repaint();
			}
			else{
				level = new GameCompletedScreen(this);
				gameFrame.repaint();
			}
			for (PlayerObject player : players){
				player.mx = 0; player.my = 0;
			}
			nextLevelCounter = 0;
			checkNewMusic();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void checkNewMusic() {
		try{
			if (level instanceof GameOverScreen){
				if (xmPlayer != null) xmPlayer.stopNow();
				xmPlayer = new XmPlayer("gameover.xm",99);
				xmPlayer.start();
			}
			else{
				int old = xmPlayer==null?-99:xmPlayer.getFileId();
				int current = 1;
				String name = "world1music.xm";
				if (level.levelNr > 0 && level.levelNr % 5 == 0){
					current = 0; //Spooky
					name = "spooky.xm";
				}
				else if (level.levelNr >= 6 && level.levelNr <= 10){
					current = 2;
					name = "world2music.xm";
				}
				else if (level.levelNr >= 11 && level.levelNr <= 15){
					current = 3;
					name = "world3music.xm";
				}
				if (old != current){
					if (xmPlayer != null) xmPlayer.stopNow();
					xmPlayer = new XmPlayer(name,current);
					xmPlayer.start();
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void previousLevel() {
		try{
			if (level.levelNr > 1){
				level = new Level(this,level.levelNr-1);
				gameFrame.repaint();
			}
			nextLevelCounter = 0;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void lastLevel() {
		try{
			level = new Level(this,Level.maxLevels);
			gameFrame.repaint();
			nextLevelCounter = 0;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
