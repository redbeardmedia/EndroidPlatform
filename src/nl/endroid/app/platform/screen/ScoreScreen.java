package nl.endroid.app.platform.screen;

import com.badlogic.gdx.graphics.Color;

import nl.endroid.framework.screen.BaseScoreScreen;

public class ScoreScreen extends BaseScoreScreen
{
	// LIFECYCLE
	
	@Override
	protected void configure()
	{
		backgroundColor = new Color(0.75f, 0.9f, 1.0f, 1.0f);
	}
}