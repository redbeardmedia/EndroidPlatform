package nl.endroid.app.platform.screen;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import nl.endroid.app.platform.entity.Hero;
import nl.endroid.app.platform.entity.Stone;
import nl.endroid.framework.Utils;
import nl.endroid.framework.screen.BaseGameScreen;

public class GameScreen extends BaseGameScreen
{
	protected Array<Stone> stones;
	protected Hero hero;
	
	@Override
	public void show()
	{
		super.show();
		
		world.setGravity(new Vector2(0.0f, -9.81f));
		
		int currentX = -1000;
		int currentY = 0;
		
		Stone stone = null;
		stones = new Array<Stone>();
		for (int index = 0; index < 100; index++) {
			stone = new Stone();
			stone.createBody(world);
			stage.addActor(stone);
			stone.setPosition(currentX, currentY);
			currentX += stone.getWidth();
		}
		
		currentX = (int) stone.getWidth() * 2;
		currentY += stone.getHeight();
		
		Random random = new Random();
		for (int index = 0; index < 100; index++) {
			if (random.nextBoolean()) {
				stone = new Stone();
				stone.createBody(world);
				stage.addActor(stone);
				stone.setPosition(currentX, currentY);
			}
			currentX += stone.getWidth();
		}
		
		hero = new Hero();
		hero.createBody(world);
		stage.addActor(hero);
		hero.setPosition(hero.getWidth() / 2, stone.getHeight() / 2 + hero.getHeight() / 2 + 150);
		
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		
		Vector2 velocity = hero.getBody().getLinearVelocity();
		velocity.x = Gdx.input.getAccelerometerY() * 2;
		
		hero.getBody().setLinearVelocity(velocity);
	}
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button)
	{
		hero.getBody().applyForce(0.0f, 30f, Utils.pixelsToMeters(hero.getX()), Utils.pixelsToMeters(hero.getY()));
		
		return false;
	}
}
