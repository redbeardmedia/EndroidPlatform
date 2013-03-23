package nl.endroid.app.platform.screen;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

import nl.endroid.app.platform.entity.Cloud;
import nl.endroid.app.platform.entity.Coin;
import nl.endroid.app.platform.entity.Flower;
import nl.endroid.app.platform.entity.Hero;
import nl.endroid.app.platform.entity.Sky;
import nl.endroid.app.platform.entity.Stone;
import nl.endroid.framework.AssetManager;
import nl.endroid.framework.Entity;
import nl.endroid.framework.Utils;
import nl.endroid.framework.screen.BaseGameScreen;

public class GameScreen extends BaseGameScreen
{
	protected Hero hero;
	
	protected Integer blockSize = 25;
	
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
		AssetManager.createSound("die", "die.wav");
		
		world.setGravity(new Vector2(0.0f, -40.0f));
		
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
		
		scoreLabel = application.getLabel(score.toString(), "white");
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
		int maxLength = 0;
		Array<Entity> entities = new Array<Entity>();
		for (int index = rows.length - 1; index >= 0; index--) {
			int currentX = -blockSize;
			rows[index] = "B " + rows[index] + " B";
			String[] row = rows[index].split(" ");
			maxLength = Math.max(maxLength, row.length);
			for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
				Entity entity = null;
				switch (row[columnIndex].charAt(0)) {
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
				Sky sky = new Sky();
				stage.addActor(sky);
				sky.setPosition(currentX, currentY);
				if (entity != null) {
					entity.createBody(world);
					entity.setPosition(currentX, currentY - blockSize / 2 + entity.getHeight() / 2);
					entities.add(entity);
				}
				currentX += blockSize;
			}
			currentY += blockSize;
		}
		
		// Add clouds
		Random random = new Random();
		for (int index = 0; index < 10; index++) {
			Cloud cloud = new Cloud();
			stage.addActor(cloud);;
			cloud.setMax(maxLength * blockSize);
			cloud.setPosition(random.nextInt(maxLength * blockSize), random.nextInt(rows.length * blockSize));
		}
		
		// Add actors
		for (Entity entity : entities) {
			stage.addActor(entity);
		}
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		
		float speed = Gdx.input.getAccelerometerY();
		
		if (Math.abs(speed) < 0.2) {
			speed = 0;
		}
		
		Vector2 velocity = hero.getBody().getLinearVelocity();
		velocity.x = speed * 2;
		if (velocity.x > 0.2) {
			hero.setDirection(Hero.DIRECTION_RIGHT);
		} else if (velocity.x < -0.2) {
			hero.setDirection(Hero.DIRECTION_LEFT);
		}
		hero.getBody().setLinearVelocity(velocity);
		
		if (hero.getY() < - blockSize * 2) {
			AssetManager.playSound("die");
			dispose();
			show();
		}
		
		float cameraX = hero.getX();
		
		if (cameraX > stage.getWidth() + width) {
			cameraX = stage.getWidth() + width;
		}
		if (cameraX < width / 2) {
			cameraX = width / 2;
		}
		
		scoreLabel.setX(cameraX + width / 2 - 60);
		
		camera.position.set(cameraX, camera.position.y, 0);
		camera.update();
	}
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button)
	{
		if (hero.getState() != Hero.STATE_JUMPING) {
			hero.getBody().applyForce(0.0f, 90f, Utils.pixelsToMeters(hero.getX()), Utils.pixelsToMeters(hero.getY()));
			hero.setState(Hero.STATE_JUMPING);
			AssetManager.playSound("jump");
		}
		
		return false;
	}
}
