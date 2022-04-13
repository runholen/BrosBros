package brosbros;

import javax.imageio.ImageIO;

public class BossObject extends GameObject{

	private GameLoop loop;
	private int bossNr;
	private String bossName;
	protected int startx;
	private int shootCounter = 0;

	
	public BossObject(GameLoop loop, int bossNr, String bossName) throws Exception{
		this.loop = loop;
		this.bossNr = bossNr;
		this.bossName = bossName;
		String fileName = "blacho.png";
		if (bossNr == 2) fileName = "blicho.png";
		if (bossNr == 3) fileName = "bluchu.png";
		if (bossNr == 4) fileName = "hatched blicho.png";
		if (bossNr == 5) fileName = "super blacho.png";
		if (bossNr == 6) fileName = "super bluchu.png";
		image = ImageIO.read(getClass().getResourceAsStream("/resources/"+fileName));
		image = GameFrame.scale(image);
		flipped = GameFrame.flip(image);
		width = image.getWidth()*2;
		height = image.getHeight()*2;
	}

	public void move(int levelNr) {
		if (levelNr == 15){
			if (useFlipped == false){
				x = x + 5;
				if (x >= 500){
					useFlipped = true;
				}
			}
			else{
				x = x - 5;
				if (x <= 40){
					useFlipped = false;
				}
			}
		}
		if (levelNr == 25) {
			int currentDistance = 0;
			PlayerObject closestPlayer = null;
			for (PlayerObject p : loop.players) {
				if (p.dying >= 0) continue;
				int distance = p.x-x;
				if (closestPlayer == null || Math.abs(distance) < Math.abs(currentDistance)) {
					closestPlayer = p;
					currentDistance = distance;
				}
			}
			if (closestPlayer != null && currentDistance != 0 && Math.abs(currentDistance) < 150) {
				if (currentDistance > 0) x = x + 1;
				else if (currentDistance < 0) x = x - 2;
				useFlipped = currentDistance > 0;
				if (x < startx - 200) x = startx - 200;
			}
		}
		if (bossNr == 3) {
			shootCounter--;
			if (shootCounter <= 0) {
				shootCounter = 30 + (int)(Math.random()*35);
				int targetX = (int)(GameFrame.width*Math.random());
				int targetY = (int)(Math.random() * (y - 10));
				try {
					BluchuLaserBullet b = new BluchuLaserBullet(x+width/4, y+height/4, targetX, targetY);
					//System.out.println(x+" "+width+" "+b.x+" "+y+" "+height+" "+b.y);
					loop.bullets.add(b);
				}catch(Exception ex) {
					ex.printStackTrace();					
				}
			}
		}
	}

	public void shove(PlayerObject player) {
		int middlexPlayer = player.x + player.width/2;
		int middlexBoss = x + width/2;
		if (middlexPlayer >= middlexBoss) player.x += 6;
		else player.x -= 6;
	}

}
