package nl.endroid.app.platform.entity;

import nl.endroid.framework.Animation;
import nl.endroid.framework.Entity;

public class Sky extends Entity
{
	@Override
	protected void configure()
	{		
		addAnimation(new Animation("sky"));
	}
}