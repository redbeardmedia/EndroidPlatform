package nl.endroid.app.platform.screen;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

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
	
	protected Float maxSpeed = 2.5f;
	protected Integer levelHeight = 13;
	protected Integer levelCount = 4;
	protected Integer right;
	protected Integer blockSize = 25;
	
	protected Array<Entity> wall;
	protected Array<Entity> levelEntities;
	
	
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
		
		levelEntities = new Array<Entity>();
		
		AssetManager.createSound("coin", "coin.wav");
		AssetManager.createSound("jump", "jump.wav");
		AssetManager.createSound("die", "die.wav");
		
		world.setGravity(new Vector2(0.0f, -30.0f));
		
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
		
		right = 0;
		
		createWall();
		
		addLevel("000");
		
		score = 0;
		
		scoreLabel = application.getLabel(score.toString(), "white");
		scoreLabel.setY(height - scoreLabel.getHeight() - 10);
		scoreLabel.setWidth(50);
		scoreLabel.setAlignment(Align.right);
	}
	
	protected void createWall()
	{
		wall = new Array<Entity>();
		
		Stone stone;
		for (int index = 0; index < levelHeight; index++) {
			stone = new Stone();
			stone.createBody(world);
			stone.setPosition(-blockSize, index * blockSize);
			wall.add(stone);
		}
	}
	
	protected void addLevel(String levelName)
	{
		Utils.log("Creating level: " + levelName);
		
		FileHandle fileHandle = Gdx.files.internal("level/" + levelName + ".txt");
		String levelString = fileHandle.readString();
		
		String[] columns = null;
		String[] rows = levelString.split("\n");
		for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
			columns = rows[rows.length - rowIndex - 1].split(" ");
			for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
				Entity entity = null;
				switch (columns[columnIndex].charAt(0)) {
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
				sky.setPosition(right + columnIndex * blockSize, rowIndex * blockSize);
				levelEntities.add(sky);
				if (entity != null) {
					entity.createBody(world);
					stage.addActor(entity);
					entity.setPosition(right + columnIndex * blockSize, rowIndex * blockSize - blockSize / 2 + entity.getHeight() / 2);
					levelEntities.add(entity);
				}
			}
		}
		
		// Add sky
		for (Entity entity : levelEntities) {
			if (entity instanceof Sky && entity.getStage() != null) {
				stage.addActor(entity);
			}
		}
		
		// Add clouds
//		for (int index = 0; index < 10; index++) {
//			Cloud cloud = new Cloud();
//			stage.addActor(cloud);;
//			cloud.setMax(maxLength * blockSize);
//			cloud.setPosition(random.nextInt(maxLength * blockSize), random.nextInt(rows.size * blockSize));
//		}
		
		// Add actors
		for (Entity entity : levelEntities) {
			if (!(entity instanceof Sky) && entity.getStage() != null) {
				stage.addActor(entity);
			}
		}
		
		right += columns.length * blockSize;
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		
		checkFallen();
		
		updateSpeed();
		
		updateCamera();
		
		updateLevel();
	}
	
	protected void checkFallen()
	{
		if (hero.getY() < - blockSize * 2) {
			AssetManager.playSound("die");
			updateHighScores();
			dispose();
			show();
		}
	}
	
	protected void updateSpeed()
	{
		float speed = Gdx.input.getAccelerometerY();
		
		if (Math.abs(speed) < 0.2) {
			speed = 0;
		}
		
		Vector2 velocity = hero.getBody().getLinearVelocity();
		velocity.x = speed * 2;
		
		if (velocity.x > 0.2) {
			velocity.x = Math.min(maxSpeed, velocity.x);
			hero.setDirection(Hero.DIRECTION_RIGHT);
		} else if (velocity.x < -0.2) {
			velocity.x = Math.max(-maxSpeed, velocity.x);
			hero.setDirection(Hero.DIRECTION_LEFT);
		}
		
		hero.setSpeed(velocity);
	}
	
	protected void updateCamera()
	{
		float cameraX = hero.getX();
		
		if (cameraX < width / 2) {
			cameraX = width / 2;
		}
		
		cameraX = Math.max(cameraX, camera.position.x);
		
		scoreLabel.setX(cameraX + width / 2 - 60);
		
		camera.position.set(cameraX, camera.position.y, 0);
		camera.update();
		
		stage.addActor(scoreLabel);
	}
	
	protected void updateLevel()
	{
		// Add levels to the right
		Random random = new Random();
		while (camera.position.x + width > right) {
			Integer level = random.nextInt(levelCount) + 1;
			String levelName = level.toString();
			while (levelName.length() < 3) {
				levelName = "0" + levelName;
			}
			addLevel(levelName);
		}
		
		// Remove levels from the left
		float cameraLeft = camera.position.x - width / 2 - blockSize;
		for (Entity entity : levelEntities) {
			if (entity.getX() < cameraLeft) {
				entity.destroy();
				levelEntities.removeValue(entity, true);
			}
		}
		
		// Move the wall to always match the left
		for (int index = 0; index < wall.size; index++) {
			wall.get(index).setPosition(cameraLeft - 5, index * blockSize);
		}
	}
	
	protected void updateHighScores()
	{
		Array<Integer> highScores = new Array<Integer>();
		
		for (int index = 0; index < 5; index++) {
			highScores.add(Integer.valueOf(application.getPreference("highscore_" + index, "0")));
		}
		
		highScores.add(score);
		highScores.sort();
		
		for (int index = 0; index < 5; index++) {
			application.setPreference("highscore_" + index, highScores.get(highScores.size - index - 1).toString());
		}
	}
	
	@Override
	public boolean keyDown(int keyCode)
	{
		if (keyCode == Keys.BACK) {
			updateHighScores();
		}
		
		super.keyDown(keyCode);
		
		return false;
	}
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button)
	{
		if (hero.getState() != Hero.STATE_JUMPING) {
			hero.getBody().applyForce(0.0f, 70f, Utils.pixelsToMeters(hero.getX()), Utils.pixelsToMeters(hero.getY()));
			hero.setState(Hero.STATE_JUMPING);
			AssetManager.playSound("jump");
		}
		
		return false;
	}
}
