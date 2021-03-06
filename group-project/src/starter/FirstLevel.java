package starter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;

public class FirstLevel extends GraphicsPane implements ActionListener {
	private MainApplication program; // you will use program to get access to
										// all of the GraphicsProgram calls
	private GImage img;
	static boolean[] Keys = new boolean[4];
	private boolean spawningCycleOn = false;
	private CharacterInteraction input, enemyInput;
	private MainCharacter hero;
	private Enemy[] enemies = {
			new Enemy(EnemyType.SLIME, 10, 5, true, 250, 100, true),
			new Enemy(EnemyType.SLIME, 10, 5, true, 450, 375, true),
			new Enemy(EnemyType.GOBLIN, 15, 10, false,  50, 100, false),
			new Enemy(EnemyType.GOBLIN, 15, 10, false,  150, 475, false),
			new Enemy(EnemyType.BOSS, 20, 20, true, 600, 100, false)};
	
	private Timer attackTimer;
	private GImage floor;
	private GImage topWall, botWall;
	private GImage inStairs;
	private GImage outStairs;
	private GImage leftWall;
	private GImage rightWall;
	private GRect playerHealth;
	private GRect healthBG;
	
	public int counter = enemies.length;
	
	public FirstLevel(MainApplication app) {
		this.program = app;
	}

	
	@Override
	public void showContents() {
		generateLevel();
		generateEnemies();
	}

	public void generateLevel() {
		floor = new GImage("floor.png", 0, 0);
		floor.setSize(800, 600);
		
		topWall = new GImage("wall.png", 0, 0);
		botWall = new GImage("wall.png", 0, 550);
		leftWall = new GImage("wall.png", 0, 0);
		rightWall = new GImage("wall.png", 775, 0);
		topWall.setSize(800, 50);
		botWall.setSize(800, 50);
		leftWall.setSize(25, 600);
		rightWall.setSize(25, 600);
		
		inStairs = new GImage("stairs.png", 50, 250);
		outStairs = new GImage("stairs.png", 800, 250);
		inStairs.setSize(-50, 100);
		outStairs.setSize(50, 100);
		
		hero = new MainCharacter(50, 275, 10, 10);
		input = new CharacterInteraction(hero, null);
		
		program.add(floor);
		
		program.add(leftWall);
		program.add(rightWall);
		program.add(topWall);
		program.add(botWall);
		
		program.add(inStairs);
		program.add(outStairs);
		
		healthBG = new GRect(22, 5, 106, 35);
		healthBG.setFilled(true);
		healthBG.setColor(Color.BLACK);
		program.add(healthBG);
		
		playerHealth = new GRect(25, 10, hero.getPlayerHP(), 25);
		playerHealth.setFilled(true);
		playerHealth.setColor(Color.RED);
		program.add(playerHealth);
		
		program.add(hero.getCharacter());
		attackTimer = new Timer(250, this);
		attackTimer.start();
	}
	
	public void generateEnemies() {
		for(Enemy enemy : enemies) {
			enemyInput = new CharacterInteraction(null, enemy);
			program.add(enemy.getEnemyImage());
		}
	}
	
	@Override
	public void hideContents() {
		program.removeAll();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		GObject obj = program.getElementAt(e.getX(), e.getY());
		if (obj == img) {
			program.switchToMenu();
		System.out.println("left mouse has been pressed");
		}
		
		for(int i = 0; i < enemies.length; i++) {
			if (enemies[i] != null) {
				if (hero.getAttackCooldown() < 0) {
					if (enemies[i].getEnemyImage().getBounds().intersects(hero.getHitBox())) {
						enemies[i].setEnemyHp(enemies[i].getEnemyHp() - hero.getAttackValue());
						System.out.println("Player dealing damage");
						hero.resetAttackCooldown();
						
						if (enemies[i].getEnemyHp() <= 0) {
							program.remove(enemies[i].getEnemyImage());
							enemies[i].turnToSkull();
							program.add(enemies[i].getEnemyImage());
							enemies[i] = null;
							//REMOVE ENEMY FROM SCREEN
						}
					}
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
	//hero movement		
		if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP)  {
			//System.out.println("Character is being moved up");
			HeroMoveUp();
			//System.out.println("hero y pos: " + hero.getYPosPlayer());
		}
		else if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
			//System.out.println("Character is being moved down");
			HeroMoveDown();
			//System.out.println("hero y pos: " + hero.getYPosPlayer());
		}
		else if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
			//System.out.println("Character is being moved left");
			HeroMoveLeft();
			//System.out.println("hero x pos: " + hero.getXPosPlayer());
		}
		else if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			//System.out.println("Character is being moved right");
			HeroMoveRight();
			//System.out.println("hero x pos: " + hero.getXPosPlayer());
		}
	}
	//hero bounds kind of
	public void HeroMoveLeft() {
		if (hero.getXPosPlayer() - 5 >= 25) {
			hero.tick(-5.0, 0);
		}
	}
	public void HeroMoveRight() {
		if (hero.getXPosPlayer() + 55 <= 775) {
			hero.tick(5, 0);
		}
	}
	public void HeroMoveUp() {
		if (hero.getYPosPlayer() - 5 >= 50) {
			hero.tick(0, -5);
		}
	}
	public void HeroMoveDown() {
		if (hero.getYPosPlayer() + 55 <= 550) {
			hero.tick(0, 5);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//check if player hitbox collides with enemy hitbox (still need to fix enemy/player updates. it's off)
		hero.decreaseAttackCooldown();
		int gameOverCounter = 0;
		for(int i = 0; i < enemies.length; i++) {
			if (enemies[i] == null) {
				gameOverCounter++;
			}
			if (enemies[i] != null) {
				if (hero.getHitBox().intersects(enemies[i].getAttackRange())) {
					enemies[i].resetAgro();
					enemies[i].setMoving(false);  
					enemies[i].decreaseMovesAttack();
					if (enemies[i].getMovesUntilAttack() == 0) {
						hero.setPlayerHP(hero.getPlayerHP() - enemies[i].getDamage());
						playerHealth.setSize(hero.getPlayerHP(), 25);
						enemies[i].resetMovesAttack();
					}
					
		        }
				else {
					enemies[i].decreaseAgro();
					if (enemies[i].getAgro() == 0) {
						enemies[i].setMoving(true);
						enemies[i].resetMovesAttack();
					}
				}
			}
		}
		
		if (hero.getPlayerHP() <= 0) {
			attackTimer.stop();
			program.switchToLose();
		}
		if (gameOverCounter == enemies.length) {
			attackTimer.stop();
			program.switchToWin();
		}
		
	
	}
}