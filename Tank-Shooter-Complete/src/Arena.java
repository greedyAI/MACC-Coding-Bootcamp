import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Arena extends JPanel {
	public int arenaWidth = 0;
	public int arenaHeight = 0;

	public Map map;
	public int mapWidth = 50;
	public int mapHeight = 50;

	public Player player1;
	public Player player2;

	public int gameState = 0;
	
	Timer timer;
	public static int INTERVAL = 35;
	public long lastTick;

	public static final double EPSILON = Math.pow(10, -4);

	public ArrayList<DamageCounter> damageCounters;

	public Arena() {
		timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		lastTick = System.currentTimeMillis();
		timer.start();

		setFocusable(true);

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_S
						|| e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_C) {
					player1.processKeyEvent(e, KeyStatus.PRESSED);
				} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET || e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
					player2.processKeyEvent(e, KeyStatus.PRESSED);
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_S
						|| e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_C) {
					player1.processKeyEvent(e, KeyStatus.UNPRESSED);
					if (e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_C) {
						player1.weaponSwitchDelay = 0;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET || e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
					player2.processKeyEvent(e, KeyStatus.UNPRESSED);
					if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET || e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
						player2.weaponSwitchDelay = 0;
					}
				}
			}
		});
	}

	public void reset() {
		this.removeAll();
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		arenaWidth = (int) screenSize.getWidth();
		arenaHeight = (int) screenSize.getHeight();
		mapWidth = (int) (screenSize.getWidth() * mapHeight / screenSize.getHeight());

		player1 = new Player(arenaWidth, arenaHeight, 100, 100, 1);
		player2 = new Player(arenaWidth, arenaHeight, arenaWidth - Player.INIT_SIZE - 100, arenaHeight - Player.INIT_SIZE - 100, 2);

		map = new Map(mapWidth, mapHeight, arenaWidth, arenaHeight, player1, player2);

		gameState = 0;
		
		damageCounters = new ArrayList<DamageCounter>();
		damageCounters.add(new DamageCounter(player1, -player1.health));
		damageCounters.add(new DamageCounter(player2, -player2.health));

		requestFocusInWindow();
	}

	// Called once per "frame"
	void tick() {
		long currentTime = System.currentTimeMillis();
		long timeDelta = currentTime - lastTick;
		lastTick = currentTime;

		if (gameState >= 1 && gameState <= 3) {
			// Game over. Offer a chance to restart/reset the game.
			timer.stop();
			
			this.setLayout(new GridBagLayout());
			final JPanel restartPanel = new JPanel();
			restartPanel.setLayout(new GridBagLayout());
			restartPanel.setBackground(Color.WHITE);
			restartPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			
			GridBagConstraints c = new GridBagConstraints();
			
			JLabel gameOver = new JLabel("Gold Player Wins!");
			if (gameState == 1) {
				gameOver.setForeground(new Color(255, 215, 0));
			} else if (gameState == 2) {
				gameOver = new JLabel("Red Player Wins!");
				gameOver.setForeground(Color.RED);
			} else if (gameState == 3) {
				gameOver = new JLabel("Tie!");
				gameOver.setForeground(Color.BLUE);
			}
			gameOver.setHorizontalAlignment(JLabel.CENTER);
			gameOver.setFont(new Font("GillSans", Font.PLAIN, 64));
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(30, 30, 30, 30);
			restartPanel.add(gameOver, c);

			JButton restart = new JButton("Restart");
			restart.setFont(new Font("GillSans", Font.PLAIN, 20));
			restart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					lastTick = System.currentTimeMillis();
					timer.restart();
					reset();
				}
			});
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(30, 200, 30, 200);
			c.ipady = 10;
			restartPanel.add(restart, c);
			
			this.add(restartPanel, new GridBagConstraints());
			super.validate();
		} else {
			// Player 1 weapon
			if (player1.weaponSwitchDelay > 0) {
				player1.weaponSwitchDelay -= timeDelta;
			}
			if (player1.weaponSwitchDelay <= 0) {
				if (player1.keyStatuses.get(KeyEvent.VK_X) == KeyStatus.PRESSED) {
					if (player1.currentWeapon == Weapon.TRIPLET) {
						player1.currentWeapon = Weapon.TRAPPER;
					} else if (player1.currentWeapon == Weapon.FLANK) {
						player1.currentWeapon = Weapon.TRIPLET;
					} else if (player1.currentWeapon == Weapon.SPRAYER) {
						player1.currentWeapon = Weapon.FLANK;
					} else if (player1.currentWeapon == Weapon.TRAPPER) {
						player1.currentWeapon = Weapon.SPRAYER;
					}
					player1.weaponSwitchDelay = Player.INIT_WEAPON_SWITCH_DELAY;
				}
				if (player1.keyStatuses.get(KeyEvent.VK_C) == KeyStatus.PRESSED) {
					if (player1.currentWeapon == Weapon.TRIPLET) {
						player1.currentWeapon = Weapon.FLANK;
					} else if (player1.currentWeapon == Weapon.FLANK) {
						player1.currentWeapon = Weapon.SPRAYER;
					} else if (player1.currentWeapon == Weapon.SPRAYER) {
						player1.currentWeapon = Weapon.TRAPPER;
					} else if (player1.currentWeapon == Weapon.TRAPPER) {
						player1.currentWeapon = Weapon.TRIPLET;
					}
					player1.weaponSwitchDelay = Player.INIT_WEAPON_SWITCH_DELAY;
				}
			}
			
			// Player 2 weapon
			if (player2.weaponSwitchDelay > 0) {
				player2.weaponSwitchDelay -= timeDelta;
			}
			if (player2.weaponSwitchDelay <= 0) {
				if (player2.keyStatuses.get(KeyEvent.VK_OPEN_BRACKET) == KeyStatus.PRESSED) {
					if (player2.currentWeapon == Weapon.TRIPLET) {
						player2.currentWeapon = Weapon.TRAPPER;
					} else if (player2.currentWeapon == Weapon.FLANK) {
						player2.currentWeapon = Weapon.TRIPLET;
					} else if (player2.currentWeapon == Weapon.SPRAYER) {
						player2.currentWeapon = Weapon.FLANK;
					} else if (player2.currentWeapon == Weapon.TRAPPER) {
						player2.currentWeapon = Weapon.SPRAYER;
					}
					player2.weaponSwitchDelay = Player.INIT_WEAPON_SWITCH_DELAY;
				}
				if (player2.keyStatuses.get(KeyEvent.VK_CLOSE_BRACKET) == KeyStatus.PRESSED) {
					if (player2.currentWeapon == Weapon.TRIPLET) {
						player2.currentWeapon = Weapon.FLANK;
					} else if (player2.currentWeapon == Weapon.FLANK) {
						player2.currentWeapon = Weapon.SPRAYER;
					} else if (player2.currentWeapon == Weapon.SPRAYER) {
						player2.currentWeapon = Weapon.TRAPPER;
					} else if (player2.currentWeapon == Weapon.TRAPPER) {
						player2.currentWeapon = Weapon.TRIPLET;
					}
					player2.weaponSwitchDelay = Player.INIT_WEAPON_SWITCH_DELAY;
				}
			}
			
			// Player 1 move
			if (player1.keyStatuses.get(KeyEvent.VK_A) == KeyStatus.PRESSED) {
				player1.orientation -= timeDelta * player1.rotateSpeed;
			}
			if (player1.keyStatuses.get(KeyEvent.VK_D) == KeyStatus.PRESSED) {
				player1.orientation += timeDelta * player1.rotateSpeed;
			}

			int moveForwardCount1 = 0;
			if (player1.keyStatuses.get(KeyEvent.VK_W) == KeyStatus.PRESSED) {
				moveForwardCount1++;
			}
			if (player1.keyStatuses.get(KeyEvent.VK_S) == KeyStatus.PRESSED) {
				moveForwardCount1--;
			}
			if (moveForwardCount1 != 0) {
				player1.vX = Math.cos(Math.toRadians(player1.orientation)) * timeDelta * player1.speed * moveForwardCount1;
				player1.vY = Math.sin(Math.toRadians(player1.orientation)) * timeDelta * player1.speed * moveForwardCount1;
			}

			// Player 2 move
			if (player2.keyStatuses.get(KeyEvent.VK_LEFT) == KeyStatus.PRESSED) {
				player2.orientation -= timeDelta * player2.rotateSpeed;
			}
			if (player2.keyStatuses.get(KeyEvent.VK_RIGHT) == KeyStatus.PRESSED) {
				player2.orientation += timeDelta * player2.rotateSpeed;
			}

			int moveForwardCount2 = 0;
			if (player2.keyStatuses.get(KeyEvent.VK_UP) == KeyStatus.PRESSED) {
				moveForwardCount2++;
			}
			if (player2.keyStatuses.get(KeyEvent.VK_DOWN) == KeyStatus.PRESSED) {
				moveForwardCount2--;
			}
			if (moveForwardCount2 != 0) {
				player2.vX = Math.cos(Math.toRadians(player2.orientation)) * timeDelta * player2.speed * moveForwardCount2;
				player2.vY = Math.sin(Math.toRadians(player2.orientation)) * timeDelta * player2.speed * moveForwardCount2;
			}

			// Collision detection between players
			player1.vX *= 0.9;
			player1.vY *= 0.9;
			player2.vX *= 0.9;
			player2.vY *= 0.9;
			double[] actualVelocities = new double[4];
			actualVelocities[0] = player1.posX;
			actualVelocities[1] = player1.posY;
			actualVelocities[2] = player2.posX;
			actualVelocities[3] = player2.posY;
			if (player1.vX * player1.vX + player1.vY * player1.vY < EPSILON) {
				player1.vX = 0;
				player1.vY = 0;
			} else {
				player1.move(map, player1.vX, player1.vY);
			}
			if (player2.vX * player2.vX + player2.vY * player2.vY < EPSILON) {
				player2.vX = 0;
				player2.vY = 0;
			} else {
				player2.move(map, player2.vX, player2.vY);
			}
			actualVelocities[0] = player1.posX - actualVelocities[0];
			actualVelocities[1] = player1.posY - actualVelocities[1];
			actualVelocities[2] = player2.posX - actualVelocities[2];
			actualVelocities[3] = player2.posY - actualVelocities[3];
			if (player1.bounceWith(player2, map, timeDelta, actualVelocities)) {
				player1.health -= player2.damage;
				player2.health -= player1.damage;
				damageCounters.add(new DamageCounter(player1, player2.damage));
				damageCounters.add(new DamageCounter(player2, player1.damage));
				player1.damage = Math.max((int) (player1.damage * 0.5), 0);
				player2.damage = Math.max((int) (player2.damage * 0.5), 0);
			} else {
				player1.damage = Math.min(player1.damage + 1, Player.INIT_DAMAGE);
				player2.damage = Math.min(player2.damage + 1, Player.INIT_DAMAGE);
			}
			
			// Player 1 projectiles
			player1.currentReloads.replace(player1.currentWeapon, player1.currentReloads.get(player1.currentWeapon) - timeDelta);
			if (player1.keyStatuses.get(KeyEvent.VK_SPACE) == KeyStatus.PRESSED) {
				if (player1.currentReloads.get(player1.currentWeapon) <= 0) {
					if (player1.currentWeapon == Weapon.TRIPLET) {
						PlayerProjectile newProjectile = new TripletWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - TripletWeapon.INIT_SIZE / 2.0,
								player1.posY + player1.height / 2.0 - TripletWeapon.INIT_SIZE / 2.0, player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new TripletWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - TripletWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.cos(Math.toRadians(player1.orientation + 90)) / 2.5,
								player1.posY + player1.height / 2.0 - TripletWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.sin(Math.toRadians(player1.orientation + 90)) / 2.5,
								player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new TripletWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - TripletWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.cos(Math.toRadians(player1.orientation - 90)) / 2.5,
								player1.posY + player1.height / 2.0 - TripletWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.sin(Math.toRadians(player1.orientation - 90)) / 2.5,
								player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						player1.currentReloads.replace(player1.currentWeapon, TripletWeapon.INIT_RELOAD);
					} else if (player1.currentWeapon == Weapon.FLANK) {
						PlayerProjectile newProjectile = new FlankWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.cos(Math.toRadians(player1.orientation + 90)) / 3.0,
								player1.posY + player1.height / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.sin(Math.toRadians(player1.orientation + 90)) / 3.6,
								player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new FlankWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.cos(Math.toRadians(player1.orientation - 90)) / 3.0,
								player1.posY + player1.height / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.sin(Math.toRadians(player1.orientation - 90)) / 3.6,
								player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new FlankWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.cos(Math.toRadians(player1.orientation + 90)) / 3.0,
								player1.posY + player1.height / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.sin(Math.toRadians(player1.orientation + 90)) / 3.6,
								player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation + 180)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation + 180)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new FlankWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.cos(Math.toRadians(player1.orientation - 90)) / 3.0,
								player1.posY + player1.height / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player1.radius * Math.sin(Math.toRadians(player1.orientation - 90)) / 3.6,
								player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation + 180)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation + 180)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						player1.currentReloads.replace(player1.currentWeapon, FlankWeapon.INIT_RELOAD);
					} else if (player1.currentWeapon == Weapon.SPRAYER) {
						PlayerProjectile newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player1.posY + player1.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player1.posY + player1.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation + 22.5)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation + 22.5)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player1.posY + player1.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation - 22.5)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation - 22.5)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player1.posY + player1.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation + 45)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation + 45)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player1.posY + player1.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation - 45)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation - 45)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						player1.currentReloads.replace(player1.currentWeapon, SprayerWeapon.INIT_RELOAD);
					} else if (player1.currentWeapon == Weapon.TRAPPER) {
						PlayerProjectile newProjectile = new TrapperWeapon(arenaWidth, arenaHeight,
								player1.posX + player1.width / 2 - TrapperWeapon.INIT_SIZE / 2,
								player1.posY + player1.height / 2 - TrapperWeapon.INIT_SIZE / 2, player1);
						newProjectile.vX = Math.cos(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player1.orientation)) * timeDelta * newProjectile.speed;
						player1.projectileList.add(newProjectile);

						player1.currentReloads.replace(player1.currentWeapon, TrapperWeapon.INIT_RELOAD);
					}
				}
			}

			ArrayList<PlayerProjectile> projectilesToDelete1 = new ArrayList<PlayerProjectile>();
			for (int i = 0; i < player1.projectileList.size(); i++) {
				PlayerProjectile projectile = player1.projectileList.get(i);
				if (projectile instanceof TrapperWeapon) {
					projectile.vX *= 0.9;
					projectile.vY *= 0.9;
					projectile.distanceTravelled += timeDelta;
				} else {
					projectile.distanceTravelled += Math.sqrt(projectile.vX * projectile.vX + projectile.vY * projectile.vY);
				}
				double[] actualVelocities2 = new double[4];
				actualVelocities2[0] = projectile.posX;
				actualVelocities2[1] = projectile.posY;
				actualVelocities2[2] = actualVelocities[2];
				actualVelocities2[3] = actualVelocities[3];
				if (projectile.vX * projectile.vX + projectile.vY * projectile.vY < EPSILON) {
					projectile.vX = 0;
					projectile.vY = 0;
				} else {
					projectile.move(map, projectile.vX, projectile.vY);
				}
				actualVelocities2[0] = projectile.posX - actualVelocities2[0];
				actualVelocities2[1] = projectile.posY - actualVelocities2[1];
				if (projectile.distanceTravelled > projectile.range) {
					projectilesToDelete1.add(projectile);
				} else if (projectile.bounceWith(player2, map, timeDelta, actualVelocities2)) {
					projectile.penetration--;
					player2.health -= projectile.damage;
					damageCounters.add(new DamageCounter(player2, projectile.damage));
					if (projectile.penetration <= 0) {
						projectilesToDelete1.add(projectile);
					}
				}
			}
			player1.projectileList.removeAll(projectilesToDelete1);

			// Player 2 projectiles
			player2.currentReloads.replace(player2.currentWeapon, player2.currentReloads.get(player2.currentWeapon) - timeDelta);
			if (player2.keyStatuses.get(KeyEvent.VK_ENTER) == KeyStatus.PRESSED) {
				if (player2.currentReloads.get(player2.currentWeapon) <= 0) {
					if (player2.currentWeapon == Weapon.TRIPLET) {
						PlayerProjectile newProjectile = new TripletWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - TripletWeapon.INIT_SIZE / 2.0,
								player2.posY + player2.height / 2.0 - TripletWeapon.INIT_SIZE / 2.0, player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new TripletWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - TripletWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.cos(Math.toRadians(player2.orientation + 90)) / 2.5,
								player2.posY + player2.height / 2.0 - TripletWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.sin(Math.toRadians(player2.orientation + 90)) / 2.5,
								player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new TripletWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - TripletWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.cos(Math.toRadians(player2.orientation - 90)) / 2.5,
								player2.posY + player2.height / 2.0 - TripletWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.sin(Math.toRadians(player2.orientation - 90)) / 2.5,
								player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						player2.currentReloads.replace(player2.currentWeapon, TripletWeapon.INIT_RELOAD);
					} else if (player2.currentWeapon == Weapon.FLANK) {
						PlayerProjectile newProjectile = new FlankWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.cos(Math.toRadians(player2.orientation + 90)) / 3.0,
								player2.posY + player2.height / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.sin(Math.toRadians(player2.orientation + 90)) / 3.6,
								player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new FlankWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.cos(Math.toRadians(player2.orientation - 90)) / 3.0,
								player2.posY + player2.height / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.sin(Math.toRadians(player2.orientation - 90)) / 3.6,
								player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new FlankWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.cos(Math.toRadians(player2.orientation + 90)) / 3.0,
								player2.posY + player2.height / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.sin(Math.toRadians(player2.orientation + 90)) / 3.6,
								player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation + 180)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation + 180)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new FlankWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.cos(Math.toRadians(player2.orientation - 90)) / 3.0,
								player2.posY + player2.height / 2.0 - FlankWeapon.INIT_SIZE / 2.0
										+ player2.radius * Math.sin(Math.toRadians(player2.orientation - 90)) / 3.6,
								player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation + 180)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation + 180)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						player2.currentReloads.replace(player2.currentWeapon, FlankWeapon.INIT_RELOAD);
					} else if (player2.currentWeapon == Weapon.SPRAYER) {
						PlayerProjectile newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2 - SprayerWeapon.INIT_SIZE / 2,
								player2.posY + player2.height / 2 - SprayerWeapon.INIT_SIZE / 2, player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player2.posY + player2.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation + 22.5)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation + 22.5)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player2.posY + player2.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation - 22.5)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation - 22.5)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player2.posY + player2.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation + 45)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation + 45)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						newProjectile = new SprayerWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2.0 - SprayerWeapon.INIT_SIZE / 2.0,
								player2.posY + player2.height / 2.0 - SprayerWeapon.INIT_SIZE / 2.0, player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation - 45)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation - 45)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						player2.currentReloads.replace(player2.currentWeapon, SprayerWeapon.INIT_RELOAD);
					} else if (player2.currentWeapon == Weapon.TRAPPER) {
						PlayerProjectile newProjectile = new TrapperWeapon(arenaWidth, arenaHeight,
								player2.posX + player2.width / 2 - TrapperWeapon.INIT_SIZE / 2,
								player2.posY + player2.height / 2 - TrapperWeapon.INIT_SIZE / 2, player2);
						newProjectile.vX = Math.cos(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						newProjectile.vY = Math.sin(Math.toRadians(player2.orientation)) * timeDelta * newProjectile.speed;
						player2.projectileList.add(newProjectile);

						player2.currentReloads.replace(player2.currentWeapon, TrapperWeapon.INIT_RELOAD);
					}
				}
			}

			ArrayList<PlayerProjectile> projectilesToDelete2 = new ArrayList<PlayerProjectile>();
			for (int i = 0; i < player2.projectileList.size(); i++) {
				PlayerProjectile projectile = player2.projectileList.get(i);
				if (projectile instanceof TrapperWeapon) {
					projectile.vX *= 0.9;
					projectile.vY *= 0.9;
					projectile.distanceTravelled += timeDelta;
				} else {
					projectile.distanceTravelled += Math.sqrt(projectile.vX * projectile.vX + projectile.vY * projectile.vY);
				}
				double[] actualVelocities2 = new double[4];
				actualVelocities2[0] = projectile.posX;
				actualVelocities2[1] = projectile.posY;
				actualVelocities2[2] = actualVelocities[0];
				actualVelocities2[3] = actualVelocities[1];
				if (projectile.vX * projectile.vX + projectile.vY * projectile.vY < EPSILON) {
					projectile.vX = 0;
					projectile.vY = 0;
				} else {
					projectile.move(map, projectile.vX, projectile.vY);
				}
				actualVelocities2[0] = projectile.posX - actualVelocities2[0];
				actualVelocities2[1] = projectile.posY - actualVelocities2[1];
				if (projectile.distanceTravelled > projectile.range) {
					projectilesToDelete2.add(projectile);
				} else if (projectile.bounceWith(player1, map, timeDelta, actualVelocities2)) {
					projectile.penetration--;
					player1.health -= projectile.damage;
					damageCounters.add(new DamageCounter(player1, projectile.damage));
					if (projectile.penetration <= 0) {
						projectilesToDelete2.add(projectile);
					}
				}
			}
			player2.projectileList.removeAll(projectilesToDelete2);

			// Update damage counters
			ArrayList<DamageCounter> damageCountersToDelete = new ArrayList<DamageCounter>();
			for (DamageCounter dc : damageCounters) {
				dc.framesExisting++;
				if (dc.framesExisting > DamageCounter.FRAMES) {
					damageCountersToDelete.add(dc);
				}
			}
			damageCounters.removeAll(damageCountersToDelete);

			// Check for game ending conditions
			if (player1.health <= 0 && player2.health <= 0) {
				gameState = 3;
			} else if (player1.health <= 0) {
				gameState = 2;
			} else if (player2.health <= 0) {
				gameState = 1;
			}

			// Update the display
			repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		map.draw(g);

		for (PlayerProjectile projectile : player1.projectileList) {
			projectile.draw(g);
		}
		for (PlayerProjectile projectile : player2.projectileList) {
			projectile.draw(g);
		}

		player1.draw(g);
		player2.draw(g);

		for (DamageCounter dc : damageCounters) {
			dc.draw(g);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(arenaWidth, arenaHeight);
	}
}
