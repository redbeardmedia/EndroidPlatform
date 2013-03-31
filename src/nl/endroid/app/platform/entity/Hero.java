/*
 * (c) Jeroen van den Enden <info@endroid.nl>
 *
 * This source file is subject to the MIT license that is bundled
 * with this source code in the file LICENSE.
 */

package nl.endroid.app.platform.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

import nl.endroid.framework.Animation;
import nl.endroid.framework.Entity;
import nl.endroid.framework.Utils;

public class Hero extends Entity
{
	public static final int STATE_STANDING = 0;
	public static final int STATE_WALKING = 1;
	public static final int STATE_JUMPING = 2;
	
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	
	protected int state = STATE_STANDING;
	protected int direction = DIRECTION_RIGHT;
	
	protected Float currentX = null;
	protected Float currentY = null;
	
	protected boolean jumpTopReached = false;
	
	@Override
	protected void configure()
	{
		bodyShape = BODY_SHAPE_RECTANGLE;
		
		addAnimation(new Animation("hero-stand"));
		addAnimation(new Animation("hero-walk", 22, 32, 4, 0.2f));
		addAnimation(new Animation("hero-jump"));
	}
	
	@Override
	protected void createFixture(Shape shape)
	{
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;
		fixture = body.createFixture(fixtureDef);
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		
		// Our hero does not like to be rotated
		body.setTransform(Utils.pixelsToMeters(getX()), Utils.pixelsToMeters(getY()), 0.0f);
		
		if (currentX == null && currentY == null) {
			currentX = getX();
			currentY = getY();
			return;
		}
		
		float deltaX = Math.abs(getX() - currentX);
		float deltaY = Math.abs(getY() - currentY);
		
		// Move to standing state
		if (state != STATE_STANDING && deltaX < 0.1f && deltaY < 0.1f) {
			setState(STATE_STANDING);
		}
		
		// Move from standing to walking state
		if (state == STATE_STANDING && deltaX > 0.1f) {
			setState(STATE_WALKING);
		}
		
		// Move from jumping state to walking state
		if (state == STATE_JUMPING && getY() < currentY) {
			jumpTopReached = true;
		}
		if (state == STATE_JUMPING && jumpTopReached && getY() >= currentY) {
			setState(STATE_WALKING);
		}
		
		currentX = getX();
		currentY = getY();
	}
	
	public void setState(int state)
	{
		if (this.state == state) {
			return;
		}
		
		switch (state) {
			case STATE_STANDING:
				startAnimation("hero-stand");
				break;
			case STATE_WALKING:
				startAnimation("hero-walk");
				break;
			case STATE_JUMPING:
				startAnimation("hero-jump");
				break;
		}
		
		jumpTopReached = false;
		
		this.state = state;
	}
	
	public int getState()
	{
		return state;
	}
	
	public void setSpeed(Vector2 velocity)
	{
		getBody().setLinearVelocity(velocity);
		
		float frameRate = 0.4f / Math.abs(velocity.x);
		for (Animation animation : animations.values()) {
			animation.setFrameRate(frameRate);
		}
	}
	
	public void setDirection(int direction)
	{
		if (this.direction == direction) {
			return;
		}
		
		for (Animation animation : animations.values()) {
			for (TextureRegion textureRegion : animation.getTextureRegions()) {
				textureRegion.flip(true, false);
			}
		}
		
		this.direction = direction;
	}
}