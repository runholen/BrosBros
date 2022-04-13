package brosbros;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Bullet extends GameObject{
	private int dx, dy;
	public BufferedImage img;

	public Bullet(int type, int x, int y, int targetX, int targetY) throws Exception{
		this.x = x;
		this.y = y;
		this.width = 12;
		this.height = 12;
		double ddx = targetX - x;
		double ddy = targetY - y;
		double sum = Math.abs(ddx)+Math.abs(ddy);
		if (sum == 0) sum = 1;
		dx = (int) (8 * ddx / sum);
		dy = (int) (8 * ddy / sum);
		if (dy == 0) dy = -1;
		if (type == 1) {
			image = ImageIO.read(getClass().getResourceAsStream("/resources/bluchus_laser_bullet.png"));
			image = GameFrame.scale(image);
		} 
	}

	public boolean move(GameLoop loop) {
		if (x > GameFrame.width) return true;
		if (y > GameFrame.height) return true;
		if (x < -5) return true;
		if (y < -5) return true;
		x += dx;
		y += dy;
		return false;
	}
}
