package brosbros;

import java.awt.Color;

public class GameCompletedScreen extends Intro{

	public GameCompletedScreen(GameLoop gameLoop) throws Exception {
		super(gameLoop);
		messages.clear();
		messages.add(new IntroMessage(0,40,"Game Completed",45,0,500,-20,0,2,Color.gray));
		messages.add(new IntroMessage(40,85,"More levels will be added soon",35,0,400,1000,0,-10,Color.gray));
		messages.add(new IntroMessage(80,75,"Press SPACE to restart",35,0,400,1000,0,-10,Color.gray));
	}
}
