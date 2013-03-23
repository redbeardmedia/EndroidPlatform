package nl.endroid.app.platform.screen;

import com.badlogic.gdx.graphics.Color;

import nl.endroid.framework.screen.BaseMenuScreen;

public class MenuScreen extends BaseMenuScreen
{
	// LIFECYCLE
	
	@Override
	protected void configure()
	{
		backgroundColor = new Color(0.0f, 0.64f, 0.99f, 1.0f);
		
		addItem("New game", "game");
		addItem("Highscores", "score");
	}
}
