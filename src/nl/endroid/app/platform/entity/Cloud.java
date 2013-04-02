/*
 * (c) Jeroen van den Enden <info@endroid.nl>
 *
 * This source file is subject to the MIT license that is bundled
 * with this source code in the file LICENSE.
 */

package nl.endroid.app.platform.entity;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nl.endroid.framework.Animation;
import nl.endroid.framework.Entity;

public class Cloud extends Entity
{
	protected Sky sky;
	protected float speed;
	
	protected Random random;
	
	@Override
	protected void configure()
	{
		random = new Random();
		
		speed = random.nextFloat();
		
		addAnimation(new Animation("cloud"));
		
		if (random.nextBoolean()) {
			setRotation(180);
		}
	}
	
	@Override
	public void act(float delta)
	{
		this.setX(this.getX() + speed * 2 - 1.0f);
		
		if (this.getX() > sky.getWidth() + getWidth()) {
			this.setY(random.nextFloat() * sky.getHeight());
			this.setX(-getWidth());
		}
		
		if (this.getX() < -getWidth()) {
			this.setY(random.nextFloat() * sky.getHeight());
			this.setX(sky.getWidth() + getWidth());
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha)
	{
		Color color = getColor();
		
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		
		currentAnimation.draw(batch, sky.getX() + getX(), sky.getY() + getY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
	
	public void setSky(Sky sky)
	{
		this.sky = sky;
	}
}