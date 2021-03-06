/*
 * (c) Jeroen van den Enden <info@endroid.nl>
 *
 * This source file is subject to the MIT license that is bundled
 * with this source code in the file LICENSE.
 */

package nl.endroid.app.platform.screen;

import java.util.Random;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import nl.endroid.app.platform.entity.Cloud;
import nl.endroid.app.platform.entity.Coin;
import nl.endroid.app.platform.entity.Flower;
import nl.endroid.app.platform.entity.Hero;
import nl.endroid.app.platform.entity.Sky;
import nl.endroid.app.platform.entity.Stone;
import nl.endroid.framework.AssetManager;
import nl.endroid.framework.Entity;
import nl.endroid.framework.ShuffleRandom;
import nl.endroid.framework.Utils;
import nl.endroid.framework.screen.BaseGameScreen;

public class GameScreen extends BaseGameScreen
{
	protected Hero hero;
	protected float maxSpeed = 4f;
	
	protected Sky sky;
	
	protected Random random;
	protected ShuffleRandom<Integer> levelIndexes;
	
	protected ObjectMap<Integer, String[]> levels;
	
	protected int right;
	protected Array<Cloud> clouds;
	protected int cloudCount = 5;
	protected int blockSize = 25;
	
	protected boolean createBodies = true;
	
	protected Array<Entity> levelEntities;
	
	protected int score;
	protected Label scoreLabel;
	
	protected PointLight pointLight;
	
	@Override
	protected void configure()
	{
		
	}
	
	@Override
	public void show()
	{
		super.show();
		
		pool.register(Stone.class, 100);
		pool.register(Coin.class, 100);
		pool.register(Flower.class, 50);
		
		sky = new Sky();
		sky.setSize(width, height);
		sky.setY(blockSize / 2);
		stage.addActor(sky);
		
		random = new Random();
		
		levels = new ObjectMap<Integer, String[]>();
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
					
					pool.put(coin);
					levelEntities.removeValue(coin, true);
					
					contact.setEnabled(false);
					
					score++;
					scoreLabel.setText(Integer.valueOf(score).toString());
					
					AssetManager.playSound("coin");
				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse contactImpulse)
			{
				
			}
		});
		
		right = 0;
		
		loadLevels();
		
		createClouds();
		
		createBodies = true;
		
		addLevel(0);
		
		createBodies = false;
		
		score = 0;
		
		scoreLabel = application.getLabel(Integer.valueOf(score).toString(), "white");
		scoreLabel.setY(height - scoreLabel.getHeight() - 10);
		scoreLabel.setWidth(50);
		scoreLabel.setAlignment(Align.right);
		
		if (rayHandler != null && pointLight == null) {
			pointLight = new PointLight(rayHandler, 500, Color.RED, 500, width / 2 - 50, height / 2 + 15);
		}
	}
	
	protected void loadLevels()
	{
		int levelIndex = 0;
		FileHandle fileHandle = null;
		while (true) {
			String levelName = Integer.valueOf(levelIndex).toString();
			while (levelName.length() < 3) {
				levelName = "0" + levelName;
			}
			if (Gdx.files.internal("level/" + levelName + ".txt").exists()) {
				fileHandle = Gdx.files.internal("level/" + levelName + ".txt");
			} else {
				Array<Integer> levelIndexArray = levels.keys().toArray();
				levelIndexArray.removeIndex(0);
				levelIndexes = new ShuffleRandom<Integer>(levelIndexArray);
				return;
			}
			levels.put(levelIndex, fileHandle.readString().split("\n"));
			levelIndex++;
		}
	}
	
	protected void createClouds()
	{
		float depth = 0.5f;
		clouds = new Array<Cloud>();
		for (int index = 0; index < cloudCount; index++) {
			Cloud cloud = new Cloud();
			stage.addActor(cloud);
			cloud.setSky(sky);
			cloud.setPosition(random.nextFloat() * sky.getWidth(), random.nextFloat() * sky.getHeight());
			cloud.setScale(1.0f + depth);
			clouds.add(cloud);
			depth -= 0.1f;
		}
	}
	
	protected void addLevel(int levelIndex)
	{
		Utils.log("Creating level: " + levelIndex);
		
		String[] columns = null;
		String[] rows = levels.get(levelIndex);
		for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
			columns = rows[rows.length - rowIndex - 1].split(" ");
			for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
				Entity entity = null;
				float deltaY = 0.0f;
				switch (columns[columnIndex].charAt(0)) {
					case 'H':
						hero = new Hero();
						entity = hero;
						deltaY = entity.getHeight() / 2 - blockSize / 2;
						break;
					case 'B':
						entity = (Stone) pool.get(Stone.class);
						deltaY = entity.getHeight() / 2 - blockSize / 2;
						break;
					case 'C':
						entity = (Coin) pool.get(Coin.class);
						deltaY = 0.0f;
						break;
					case 'F':
						entity = (Flower) pool.get(Flower.class);
						deltaY = entity.getHeight() / 2 - blockSize / 2;
						break;
					default:
						break;
				}
				if (entity != null) {
					entity.createBody(world);
					stage.addActor(entity);
					entity.setPosition(right + columnIndex * blockSize, rowIndex * blockSize + deltaY);
					levelEntities.add(entity);
				}
			}
		}
		
		stage.addActor(hero);
		
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
		
		if (pointLight != null) {
			pointLight.setPosition(hero.getPosition());
		}
	}
	
	protected void updateCamera()
	{
		float cameraX = hero.getX();
		
		if (cameraX < width / 2) {
			cameraX = width / 2;
		}
		
		cameraX = Math.max(cameraX, camera.position.x);
		
		Vector2 position = hero.getPosition();
		if (position.x < cameraX - width / 2 + blockSize / 2) {
			position.x = cameraX - width / 2 + blockSize / 2;
			hero.setPosition(position);
		}
		
		Vector2 skyPosition = sky.getPosition();
		skyPosition.x = cameraX - width / 2 + blockSize / 2;
		sky.setPosition(skyPosition);
		
		float depth = 0.0f;
		for (Cloud cloud : clouds) {
			cloud.setX(cloud.getX() + ((float) camera.position.x - (float) cameraX) * depth);
			depth += 0.1f;
		}
		
		scoreLabel.setX(cameraX + width / 2 - 60);
		
		camera.position.set(cameraX, camera.position.y, 0);
		camera.update();
		
		stage.addActor(scoreLabel);
	}
	
	protected void updateLevel()
	{
		// Add levels to the right
		while (camera.position.x + width > right) {
			int index = levelIndexes.next();
			addLevel(index);
		}
		
		// Remove levels from the left
		float cameraLeft = camera.position.x - width / 2 - blockSize;
		for (Entity entity : levelEntities) {
			if (entity.getX() < cameraLeft) {
				Utils.log(entity);
				pool.put(entity);
				levelEntities.removeValue(entity, true);
			}
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
		if (hero.getState() != Hero.STATE_JUMPING && hero.getBody() != null) {
			hero.getBody().applyForce(0.0f, 90f, Utils.pixelsToMeters(hero.getX()), Utils.pixelsToMeters(hero.getY()));
			hero.setState(Hero.STATE_JUMPING);
			AssetManager.playSound("jump");
		}
		
		return false;
	}
}
