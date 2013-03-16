package nl.endroid.app.platform.screen;

import com.badlogic.gdx.graphics.Color;

import nl.endroid.framework.screen.BaseMenuScreen;

public class MenuScreen extends BaseMenuScreen
{
	// LIFECYCLE
	
	@Override
	protected void configure()
	{
		backgroundColor = new Color(0, 0, 0.6f, 1f);
		
		addItem("New game", "game");
	}
}
