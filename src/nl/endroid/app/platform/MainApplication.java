package nl.endroid.app.platform;

import nl.endroid.app.platform.screen.GameScreen;
import nl.endroid.app.platform.screen.MenuScreen;
import nl.endroid.app.platform.screen.ScoreScreen;
import nl.endroid.app.platform.screen.SplashScreen;
import nl.endroid.framework.BaseApplication;
import nl.endroid.framework.Utils;

public class MainApplication extends BaseApplication
{
	// LIFECYCLE
	
	@Override
	protected void configure()
	{
		setTitle(Utils.getString(R.string.app_name));
		setVersion(Utils.getString(R.string.app_version));
		
		addScreen("splash", new SplashScreen());
		addScreen("menu", new MenuScreen());
		addScreen("game", new GameScreen());
		addScreen("score", new ScoreScreen());
		
		setStartScreen("splash");
		
		setFPSLoggerEnabled(false);
	}
}
