package brosbros;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class PlayerObject extends GameObject{

	int playerNr;
	String playerName;
	boolean touchingGround = false;
	int dying = -1;
	public static int startLives = 4;
	int lives;
	int speedx = 4;
	int speedy = 7;
	int floating = 0;
	int floatValue = 0;
	
	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean upPressed = false;
	boolean downPressed = false;
	
	boolean jumping = false;
	boolean swimUp = false;
	Collition previousCollition = null;
	Collition ghostCollition = null;
	int teleportCooldown = 0;
	public static int max_hitpoints = 20;
	int hitpoints = max_hitpoints;
	
	
	BufferedImage swimming;
	BufferedImage swimmingFlipped;
	BufferedImage upsideDown;
	BufferedImage upsideDownFlipped;
	
	private GameLoop loop;
	
	public PlayerObject(GameLoop loop, int playerNr, String playerName) throws Exception{
		this.loop = loop;
		this.playerNr = playerNr;
		this.playerName = playerName;
		String fileName = "frode.png";
		if (playerNr == 2) fileName = "fritjof.png";
		if (playerNr == 3) fileName = "liv.png";
		image = ImageIO.read(getClass().getResourceAsStream("/resources/"+fileName));
		image = GameFrame.scale(image);
		flipped = GameFrame.flip(image);
		swimmingFlipped = GameFrame.rotateClockwise90(image);
		swimming = GameFrame.flip(swimmingFlipped);
		upsideDown = GameFrame.rotateClockwise90(swimmingFlipped);
		upsideDownFlipped = GameFrame.flip(upsideDown);
		
		
		if (playerNr == 1){
			speedx = 5;
			speedy = 7;
		}
		else if (playerNr == 2){
			speedx = 4;
			speedy = 9;
		}
		else if (playerNr == 3){
			floatValue = 20;
		}
		lives = startLives;
	}

	public void handleInput(Level level){
		mx = 0;
		if (leftPressed) mx = -speedx;	
		if (rightPressed) mx = speedx;
		
		if (upPressed){
			if (level.isUnderWater){
				my = -1;
				swimUp = true;
			}
			else if (touchingGround && my == 0){ //Initialize jump
				my = -speedy;
				if (level.lowGravity) my -= 3;
				gravityCounter = gravityStep;
				jumping = true;
			}
		}
		else{
			swimUp = false;
		}
	}
	
	public void checkInput(int keyCode, boolean pressed){
		int left = 0, right = 0, up = 0, down = 0;
		if (playerNr == 1){
			left = KeyEvent.VK_LEFT;
			right = KeyEvent.VK_RIGHT;
			up = KeyEvent.VK_UP;
			down = KeyEvent.VK_DOWN;
		}
		if (playerNr == 2){
			left = KeyEvent.VK_A;
			right = KeyEvent.VK_D;
			up = KeyEvent.VK_W;
			down = KeyEvent.VK_S;
		}
		if (playerNr == 3){
			left = KeyEvent.VK_G;
			up = KeyEvent.VK_Y;
			right = KeyEvent.VK_J;
			down = KeyEvent.VK_H;
		}
		if (keyCode == left){
			leftPressed = pressed;
			if (pressed) useFlipped = true;
		}
		if (keyCode == right){
			rightPressed = pressed;
			if (pressed) useFlipped = false;
		}
		if (keyCode == up){
			upPressed = pressed;
		}
		if (keyCode == down){
			downPressed = pressed;
		}
	}

	public boolean isDying() {
		return dying >= 0;
	}

	public void teleport(GameObject object, boolean right) {
		x = object.x + (right?10:-10);
		y = object.y;
		teleportCooldown = 10;
	}
	
	@Override
	public int getWidth(){
		if (loop.level.isUnderWater) return height;
		return width;
	}
	@Override
	public int getHeight(){
		if (loop.level.isUnderWater) return width;
		return height;
	}
}
