package nl.endroid.app.platform.entity;

import java.util.Random;

import nl.endroid.framework.Animation;
import nl.endroid.framework.Entity;

public class Cloud extends Entity
{
	protected float max;
	protected float speed;
	
	@Override
	protected void configure()
	{
		Random random = new Random();
		
		speed = random.nextFloat();
		
		addAnimation(new Animation("cloud"));
		
		if (random.nextBoolean()) {
			setRotation(180);
		}
	}
	
	@Override
	public void act(float delta)
	{
		this.setX(this.getX() + speed * 3 - 1.5f);
		
		if (this.getX() > max + getWidth()) {
			this.setX(-getWidth());;
		}
		
		if (this.getX() < -getWidth()) {
			this.setX(max + getWidth());
		}
	}
	
	public void setMax(float max)
	{
		this.max = max;
	}
}