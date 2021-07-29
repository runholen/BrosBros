package brosbros;

import javax.imageio.ImageIO;

public class BossObject extends GameObject{

	private GameLoop loop;
	private int bossNr;
	private String bossName;

	
	public BossObject(GameLoop loop, int bossNr, String bossName) throws Exception{
		this.loop = loop;
		this.bossNr = bossNr;
		this.bossName = bossName;
		String fileName = "blacho.png";
		image = ImageIO.read(getClass().getResourceAsStream("/resources/"+fileName));
		image = GameFrame.scale(image);
		flipped = GameFrame.flip(image);
		width = 80;
		height = 120;
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
	}

	public void shove(PlayerObject player) {
		int middlexPlayer = player.x + player.width/2;
		int middlexBoss = x + width/2;
		if (middlexPlayer >= middlexBoss) player.x += 6;
		else player.x -= 6;
	}

}
