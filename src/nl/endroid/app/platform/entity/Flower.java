package nl.endroid.app.platform.entity;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import nl.endroid.framework.Animation;
import nl.endroid.framework.Entity;

public class Flower extends Entity
{
	@Override
	protected void configure()
	{
		bodyType = BodyType.StaticBody;
		
		addAnimation(new Animation("flower"));
	}
	
	protected void createFixture(Shape shape)
	{
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;
		fixture = body.createFixture(fixtureDef);
	}
}