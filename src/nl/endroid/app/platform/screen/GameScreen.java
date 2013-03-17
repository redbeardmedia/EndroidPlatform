package nl.endroid.app.platform.screen;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

import nl.endroid.app.platform.entity.Coin;
import nl.endroid.app.platform.entity.Hero;
import nl.endroid.app.platform.entity.Stone;
import nl.endroid.framework.AssetManager;
import nl.endroid.framework.Entity;
import nl.endroid.framework.Utils;
import nl.endroid.framework.screen.BaseGameScreen;

public class GameScreen extends BaseGameScreen
{
	protected Array<Coin> coins;
	protected Array<Stone> stones;
	protected Hero hero;
	
	protected float blockWidth;
	protected float blockHeight;
	
	protected Integer score;
	protected Label scoreLabel;
	
	@Override
	protected void configure()
	{
		backgroundColor = new Color(0.75f, 0.9f, 1.0f, 1.0f);
	}
	
	@Override
	public void show()
	{
		super.show();
		
		AssetManager.createSound("coin", "coin.wav");
		AssetManager.createSound("jump", "jump.wav");
		
		world.setGravity(new Vector2(0.0f, -20.0f));
		
		float currentX = 0.0f;
		float currentY = 0.0f;
		
		Stone stone = new Stone();
		
		blockWidth = stone.getWidth();
		blockHeight = stone.getHeight();
		
		stones = new Array<Stone>();
		
		// Left wall
		for (int index = 0; index < 15; index++) {
			stone = new Stone();
			stone.createBody(world);
			stage.addActor(stone);
			stone.setPosition(currentX, currentY);
			stones.add(stone);
			currentY += stone.getHeight();
		}
		
		currentX = stone.getWidth();
		currentY = 0.0f;
		
		// Bottom wall
		for (int index = 0; index < 100; index++) {
			stone = new Stone();
			stone.createBody(world);
			stage.addActor(stone);
			stone.setPosition(currentX, currentY);
			stones.add(stone);
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
				stones.add(stone);
			}
			currentX += stone.getWidth();
		}
		
		// Coins
		Coin coin = null;
		coins = new Array<Coin>();
		
		currentX = blockWidth;
		currentY = blockHeight * 4.0f;
		
		for (int index = 0; index < 28; index++) {
			coin = new Coin();
			coin.createBody(world);
			coin.setPosition(currentX + 5.5f, currentY + 4.5f);
			stage.addActor(coin);
			coins.add(coin);
			currentX += blockWidth * 2;
			
			if (currentX + blockWidth > width) {
				currentX = blockWidth;
				currentY -= blockHeight;
			}
		}
		
		hero = new Hero();
		hero.createBody(world);
		hero.setPosition(hero.getWidth() / 2, stone.getHeight() / 2 + hero.getHeight() / 2 + 150);
		stage.addActor(hero);
		
		world.setContactListener(new ContactListener()
		{
			@Override
			public void beginContact(Contact contact)
			{
				
			}

			@Override
			public void endContact(Contact contact)
			{
				
			}
			
			@Override
			public void preSolve(Contact contact, Manifold manifold)
			{
				Entity entity1 = (Entity) contact.getFixtureA().getBody().getUserData();
				Entity entity2 = (Entity) contact.getFixtureB().getBody().getUserData();
				
				if (entity1 instanceof Coin || entity2 instanceof Coin) {
					Coin coin = (entity1 instanceof Coin) ? (Coin) entity1 : (Coin) entity2;
					coin.destroy();
					coins.removeValue(coin, true);
					
					contact.setEnabled(false);
					
					score++;
					scoreLabel.setText(score.toString());
					
					AssetManager.playSound("coin");
				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse contactImpulse)
			{
				
			}
		});
		
		score = 0;
		
		scoreLabel = application.getLabel(score.toString(), "black");
		scoreLabel.setX(width - 60);
		scoreLabel.setY(height - scoreLabel.getHeight() - 10);
		scoreLabel.setWidth(50);
		scoreLabel.setAlignment(Align.right);
		stage.addActor(scoreLabel);
		
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		
		Vector2 velocity = hero.getBody().getLinearVelocity();
		velocity.x = Gdx.input.getAccelerometerY() * 2;
		
		hero.setDirection(velocity.x >= 0 ? Hero.DIRECTION_RIGHT : Hero.DIRECTION_LEFT);
		
		hero.getBody().setLinearVelocity(velocity);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		hero.destroyHard();
		
		for (Stone stone : stones) {
			stone.destroyHard();
		}
		
		for (Coin coin : coins) {
			coin.destroyHard();
		}
	}
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button)
	{
		if (hero.getState() != Hero.STATE_JUMPING) {
			hero.getBody().applyForce(0.0f, 50f, Utils.pixelsToMeters(hero.getX()), Utils.pixelsToMeters(hero.getY()));
			hero.setState(Hero.STATE_JUMPING);
			AssetManager.playSound("jump");
		}
		
		return false;
	}
}
