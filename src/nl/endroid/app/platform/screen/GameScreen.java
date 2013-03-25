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
import com.badlogic.gdx.utils.ObjectMap;

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
	
	protected Integer levelCount = 10;
	protected Integer levelFiles = 4;
	protected Integer levelWidth;
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
		
		world.setGravity(new Vector2(0.0f, -30.0f));
		
		createLevel();
		
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
	
	public void createLevel()
	{
		String row;
		FileHandle handle;
		Random random = new Random();
		ObjectMap<Integer, String> rows = new ObjectMap<Integer, String>();
		
		Array<String> levelNames = new Array<String>();
		levelNames.add("000");
		for (int levelIndex = 0; levelIndex < levelCount; levelIndex++) {
			Integer level = random.nextInt(levelFiles) + 1;
			String levelName = level.toString();
			while (levelName.length() < 3) {
				levelName = "0" + levelName;
			}
			levelNames.add(levelName);
		}
		
		
		for (int levelIndex = 0; levelIndex < levelCount; levelIndex++) {
			String levelName = levelNames.get(levelIndex);
			handle = Gdx.files.internal("level/" + levelName + ".txt");
			String level = handle.readString();
			String[] levelRows = level.split("\n");
			for (int rowIndex = levelRows.length - 1; rowIndex >= 0; rowIndex--) {
				row = rows.get(rowIndex);
				if (row == null) {
					row = "";
				}
				row += " " + levelRows[rowIndex];
				rows.put(rowIndex, row);
			}
		}
		
		int currentY = 0;
		int maxLength = 0;
		Array<Entity> entities = new Array<Entity>();
		for (int index = rows.size - 1; index >= 0; index--) {
			int currentX = -blockSize;
			rows.put(index, "B" + rows.get(index) + " B");
			String[] rowArray = rows.get(index).split(" ");
			maxLength = Math.max(maxLength, rowArray.length);
			for (int columnIndex = 0; columnIndex < rowArray.length; columnIndex++) {
				Entity entity = null;
				switch (rowArray[columnIndex].charAt(0)) {
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
		for (int index = 0; index < 10; index++) {
			Cloud cloud = new Cloud();
			stage.addActor(cloud);;
			cloud.setMax(maxLength * blockSize);
			cloud.setPosition(random.nextInt(maxLength * blockSize), random.nextInt(rows.size * blockSize));
		}
		
		// Add actors
		for (Entity entity : entities) {
			stage.addActor(entity);
		}
		
		levelWidth = maxLength * blockSize;
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
		hero.setSpeed(velocity);
		
		if (hero.getY() < - blockSize * 2) {
			AssetManager.playSound("die");
			updateHighScores();
			dispose();
			show();
		}
		
		float cameraX = hero.getX();
		
		if (cameraX > levelWidth - width / 2) {
			cameraX = levelWidth - width / 2;
		}
		if (cameraX < width / 2) {
			cameraX = width / 2;
		}
		
		scoreLabel.setX(cameraX + width / 2 - 60);
		
		camera.position.set(cameraX, camera.position.y, 0);
		camera.update();
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
