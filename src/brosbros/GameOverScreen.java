package brosbros;

import java.awt.Color;

public class GameOverScreen extends Intro{

	public GameOverScreen(GameLoop gameLoop) throws Exception {
		super(gameLoop);
		backgroundColor = Color.black;
		messages.clear();
		messages.add(new IntroMessage(0,40,"Game Over",45,0,500,-20,0,2,Color.white));
		messages.add(new IntroMessage(40,60,"Oh no, we died",                  16,1,-5,100,2,0,Color.white));
		messages.add(new IntroMessage(100,60,"Yes we did",                      16,2,-5,200,2,0,Color.white));
		messages.add(new IntroMessage(160,60,"We will do better next time",    16,3,-5,300,2,0,Color.white));
		messages.add(new IntroMessage(220,80,"Please press space to revive us",16,1,-5,0,2,2,Color.white));
		messages.add(new IntroMessage(300,80,"Me too",                         16,2,-5,0,2,2,Color.white));
		messages.add(new IntroMessage(380,80,"And me too",                     16,3,-5,0,2,2,Color.white));
		messages.add(new IntroMessage(200,88,"Press SPACE to restart",35,0,420,1000,0,-10,Color.gray));
	}

}
