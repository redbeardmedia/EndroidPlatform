package nl.endroid.app.platform.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import nl.endroid.app.platform.entity.Coin;
import nl.endroid.app.platform.entity.Flower;
import nl.endroid.app.platform.entity.Hero;
import nl.endroid.app.platform.entity.Stone;
import nl.endroid.framework.AssetManager;
import nl.endroid.framework.Entity;
import nl.endroid.framework.Utils;
import nl.endroid.framework.screen.BaseGameScreen;

public class GameScreen extends BaseGameScreen
{
	protected Hero hero;
	
	protected float blockSize = 25;
	
	protected Integer score;
	protected Label scoreLabel;
	
	@Override
	protected void configure()
	{
		
	}
	
	@Override
	public void show()
	{
		super.show();
		
		AssetManager.createSound("coin", "coin.wav");
		AssetManager.createSound("jump", "jump.wav");
		
		world.setGravity(new Vector2(0.0f, -20.0f));
		
		createLevel("001");
		
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
	
	public void createLevel(String levelName)
	{
		FileHandle handle = Gdx.files.internal("level/" + levelName + ".txt");
		String level = handle.readString();
		String[] rows = level.split("\n");
		int currentY = 0;
		for (int index = rows.length - 1; index >= 0; index--) {
			int currentX = 0;
			String row = rows[index];
			for (int charIndex = 0; charIndex < row.length(); charIndex++) {
				Entity entity = null;
				switch (row.charAt(charIndex)) {
					case 'H':
						hero = new Hero();
						entity = hero;
						break;
					case 'B':
						entity = new Stone();
						break;
					case 'C':
						entity = new Coin();
						break;
					case 'F':
						entity = new Flower();
						break;
					default:
						break;
				}
				if (entity != null) {
					entity.createBody(world);
					stage.addActor(entity);
					entity.setPosition(currentX, currentY - blockSize / 2 + entity.getHeight() / 2);
				}
				currentX += blockSize;
			}
			currentY += blockSize;
		}
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		
		if (Math.abs(Gdx.input.getAccelerometerY()) > 0.5) {
			Vector2 velocity = hero.getBody().getLinearVelocity();
			velocity.x = Gdx.input.getAccelerometerY() * 2;
			hero.setDirection(velocity.x >= 0 ? Hero.DIRECTION_RIGHT : Hero.DIRECTION_LEFT);
			hero.getBody().setLinearVelocity(velocity);
		}
		
		if (hero.getY() < - blockSize) {
			dispose();
			show();
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
