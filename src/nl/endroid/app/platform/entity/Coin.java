package nl.endroid.app.platform.entity;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import nl.endroid.framework.Animation;
import nl.endroid.framework.Entity;

public class Coin extends Entity
{
	@Override
	protected void configure()
	{
		bodyType = BodyType.StaticBody;
		
		addAnimation(new Animation("coin", 14, 16, 4, 0.2f));
	}
}