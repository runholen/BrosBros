package brosbros;

import java.awt.*;
import java.util.Vector;

public class Intro extends Level{

	Vector<IntroMessage> messages = new Vector<>();
	Vector<IntroMessage> currentMessages = new Vector<>();
	int counter = 0;
	GameLoop loop;
	
	public Intro(GameLoop gameLoop) throws Exception{
		super(gameLoop,0);
		this.loop = gameLoop;
		if (GameFrame.isDeveloperPc()){
			messages.add(new IntroMessage(0,60,"Java version: "+System.getProperty("java.version"),10,0,10,800,0,0,Color.gray));
		}
		messages.add(new IntroMessage(0,40,"BrosBros",45,0,500,-20,0,2,Color.gray));
		messages.add(new IntroMessage(40,40,"Hi, I'm Frode",  16,1,-5,100,2,0,Color.black));
		messages.add(new IntroMessage(80,40,"Hi, I'm Fritjof",16,2,-5,200,2,0,Color.black));
		messages.add(new IntroMessage(120,40,"Hi, I'm Liv",   16,3,-5,300,2,0,Color.black));
		messages.add(new IntroMessage(160,60,"Welcome to BrosBros!",16,1,0,0,0,0,Color.black));
		messages.add(new IntroMessage(160,60,"Welcome to BrosBros!",16,2,0,0,0,0,Color.black));
		messages.add(new IntroMessage(160,60,"Welcome to BrosBros!",16,3,0,0,0,0,Color.black));
		messages.add(new IntroMessage(220,20,"Control me with the arrow keys",16,1,0,0,1,0,Color.black));
		messages.add(new IntroMessage(240,20,"Control me with the arrow keys",16,1,0,0,0,-1,Color.black));
		messages.add(new IntroMessage(260,20,"Control me with the arrow keys",16,1,0,0,1,1,Color.black));
		messages.add(new IntroMessage(280,40,"Control me with the arrow keys",16,1,0,0,1,0,Color.black));
		messages.add(new IntroMessage(320,20,"Control me with the WASD keys",16,2,0,0,1,0,Color.black));
		messages.add(new IntroMessage(340,20,"Control me with the WASD keys",16,2,0,0,0,-1,Color.black));
		messages.add(new IntroMessage(360,20,"Control me with the WASD keys",16,2,0,0,1,1,Color.black));
		messages.add(new IntroMessage(380,40,"Control me with the WASD keys",16,2,0,0,1,0,Color.black));
		messages.add(new IntroMessage(420,20,"Control me with the YGHJ keys",16,3,0,0,1,0,Color.black));
		messages.add(new IntroMessage(440,20,"Control me with the YGHJ keys",16,3,0,0,0,-1,Color.black));
		messages.add(new IntroMessage(460,20,"Control me with the YGHJ keys",16,3,0,0,1,1,Color.black));
		messages.add(new IntroMessage(480,40,"Control me with the YGHJ keys",16,3,0,0,1,0,Color.black));
		messages.add(new IntroMessage(520,88,"Press SPACE to start",35,0,420,1000,0,-10,Color.gray));
		messages.add(new IntroMessage(540,60,"The theme of world 1 is caves",16,1,0,0,1,0,Color.black));
		messages.add(new IntroMessage(620,60,"The theme of world 2 is boats",16,2,0,0,1,0,Color.black));
		messages.add(new IntroMessage(680,60,"The theme of world 3 is islands",16,3,0,0,1,0,Color.black));
	}
	
	public class IntroMessage{
		int time;
		int duration;
		String message;
		int size;
		int player;
		int startx;
		int starty;
		int x;
		int y;
		int movex;
		int movey;
		Color color;
		public IntroMessage(int time, int duration, String message, int size, int player, int startx, int starty, int movex, int movey, Color color) {
			this.time = time;
			this.duration = duration;
			this.message = message;
			this.size = size;
			this.player = player;
			this.duration = duration;
			this.startx = startx;
			this.starty = starty;
			this.movex = movex;
			this.movey = movey;
			this.color = color;
		}
		
	}

	public void advance() {
		IntroMessage[] playerMessages = new IntroMessage[loop.players.length];
		for (int t = 0; t < currentMessages.size(); t++){
			IntroMessage m = currentMessages.elementAt(t);
			if (m.duration > 0){
				m.x += m.movex;
				m.y += m.movey;
				m.duration--;
			}
			if (m.player > 0){
				if (m.duration == 0) m.message = "";
				if (playerMessages[m.player-1] != null){
					currentMessages.remove(playerMessages[m.player-1]);
					t--;
				}
				playerMessages[m.player-1] = m;
			}
		}
		for (IntroMessage m : messages){
			if (m.time == counter){
				currentMessages.add(m);
				if (m.player > 0 && playerMessages[m.player-1] != null){
					m.x = playerMessages[m.player-1].x;
					m.y = playerMessages[m.player-1].y;
				}
				else{
					m.x = m.startx;
					m.y = m.starty;
				}
			}
		}
		counter++;
	}

	public void paintIntro(Graphics2D g) {
		for (IntroMessage m : currentMessages){
			g.setColor(m.color);
			g.setFont(g.getFont().deriveFont(Font.BOLD,(float)m.size));
			g.drawString(m.message,m.x,m.y);
			if (m.player > 0){
				g.drawImage(loop.players[m.player-1].image,m.x,m.y+10, null);
			}
		}
	}
}
