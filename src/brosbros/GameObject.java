package brosbros;

import java.awt.image.BufferedImage;

public class GameObject {

	int x;
	int y;
	int mx;
	int my;
	BufferedImage image;
	BufferedImage flipped;
	boolean useFlipped = false;
	int gravityCounter = 0;
	int gravityStep = 4;
	int width = 40;
	int height = 60;
	
	public GameObject(){
		
	}
	
	public GameObject(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean collidesWith(GameObject o) {
		if (x < o.x + o.getWidth() && x + getWidth() > o.x && y < o.y + o.getHeight() && y + getHeight() > o.y) {
			return true;
		}
		return false;
	}
	public boolean enters(GameObject o) {
		if (x < o.x + o.getWidth()/2 && x + getWidth()/2 > o.x && y < o.y + o.getHeight()/2 && y + getHeight()/2 > o.y) {
			return true;
		}
		return false;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
}
