import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ArrayList;

public class Player extends GameObject {

	public static final int INIT_SIZE = 30;
	public static final int INIT_MASS = 50;

	public static final int INIT_DAMAGE = 8;
	public static final double INIT_SPEED = 0.15;
	public static final double INIT_ROTATE_SPEED = 0.15;
	public static final int INIT_HEALTH = 300;
	
	public static final long INIT_WEAPON_SWITCH_DELAY = 200;

	public int damage = INIT_DAMAGE;
	public double speed = INIT_SPEED;
	public double rotateSpeed = INIT_ROTATE_SPEED;
	public int health = INIT_HEALTH;
	public double orientation = 0;
	public Weapon currentWeapon = Weapon.TRIPLET;
	public HashMap<Weapon, Long> currentReloads = new HashMap<Weapon, Long>();
	public long weaponSwitchDelay = 0;
	public int id;

	public ArrayList<PlayerProjectile> projectileList = new ArrayList<PlayerProjectile>();
	public HashMap<Integer, KeyStatus> keyStatuses = new HashMap<Integer, KeyStatus>();

	public Player(int arenaWidth, int arenaHeight, double startPosX, double startPosY, int id) {
		super(arenaWidth, arenaHeight, 0, 0, startPosX, startPosY, INIT_SIZE, INIT_SIZE, INIT_MASS);
		this.id = id;
		orientation = (id - 1) * 180;
		for (Weapon w : Weapon.values()) {
			currentReloads.put(w, (long) 0);
		}
	}

	public void processKeyEvent(KeyEvent keyEvent, KeyStatus keyStatus) {
		keyStatuses.put(keyEvent.getKeyCode(), keyStatus);
	}

	@Override
	public boolean move(Map map, double translateX, double translateY) {
		double step = 0.05;
		double deltaPos = Math.sqrt(translateX * translateX + translateY * translateY);
		double xStep = translateX * step / deltaPos;
		double yStep = translateY * step / deltaPos;

		GameObject[][] mapObjects = map.mapObjects;
		Color[][] terrain = map.map;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		double liquidSpeedRatio = 1;
		boolean stuck = false;
		double distTravelled = 0;
		while (distTravelled < deltaPos) {
			double nextX = posX + liquidSpeedRatio * xStep;
			double nextY = posY + liquidSpeedRatio * yStep;
			int minX = (int) (nextX * map.width / screenSize.getWidth());
			int minY = (int) (nextY * map.height / screenSize.getHeight());
			int maxX = ((nextX + width) * map.width % screenSize.getWidth() == 0)
					? (int) ((nextX + width) * map.width / screenSize.getWidth()) - 1
					: (int) ((nextX + width) * map.width / screenSize.getWidth());
			int maxY = ((nextY + height) * map.height % screenSize.getHeight() == 0)
					? maxY = (int) ((nextY + height) * map.height / screenSize.getHeight()) - 1
					: (int) ((nextY + height) * map.height / screenSize.getHeight());

			boolean xWillCollide = false;
			boolean yWillCollide = false;
			for (int i = Math.max(minY, 0); i <= Math.min(maxY, map.height - 1); i++) {
				int j = Math.max(minX, 0);
				GameObject mo = mapObjects[i][j];
				if (terrain[i][j].equals(Color.BLACK) && pointToRectSqrDist(mo.posX, mo.posX + mo.width, mo.posY, mo.posY + mo.height,
						nextX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
					int minY2 = (int) (posY * map.height / screenSize.getHeight());
					int maxY2 = ((posY + height) * map.height % screenSize.getHeight() == 0)
							? (int) ((posY + height) * map.height / screenSize.getHeight()) - 1
							: (int) ((posY + height) * map.height / screenSize.getHeight());

					for (int i2 = Math.max(minY2, 0); i2 <= Math.min(maxY2, map.height - 1); i2++) {
						GameObject mo2 = mapObjects[i2][j];
						if (terrain[i2][j].equals(Color.BLACK) && pointToRectSqrDist(mo2.posX, mo2.posX + mo2.width, mo2.posY,
								mo2.posY + mo2.height, nextX + width / 2.0, posY + height / 2.0) <= radius * radius) {
							xWillCollide = true;
							break;
						}
					}
					if (xWillCollide) {
						break;
					}
				}
			}
			for (int i = Math.max(minY, 0); i <= Math.min(maxY, map.height - 1); i++) {
				int j = Math.min(maxX, map.width - 1);
				GameObject mo = mapObjects[i][j];
				if (terrain[i][j].equals(Color.BLACK) && pointToRectSqrDist(mo.posX, mo.posX + mo.width, mo.posY, mo.posY + mo.height,
						nextX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
					int minY2 = (int) (posY * map.height / screenSize.getHeight());
					int maxY2 = ((posY + height) * map.height % screenSize.getHeight() == 0)
							? (int) ((posY + height) * map.height / screenSize.getHeight()) - 1
							: (int) ((posY + height) * map.height / screenSize.getHeight());

					for (int i2 = Math.max(minY2, 0); i2 <= Math.min(maxY2, map.height - 1); i2++) {
						GameObject mo2 = mapObjects[i2][j];
						if (terrain[i2][j].equals(Color.BLACK) && pointToRectSqrDist(mo2.posX, mo2.posX + mo2.width, mo2.posY,
								mo2.posY + mo2.height, nextX + width / 2.0, posY + height / 2.0) <= radius * radius) {
							xWillCollide = true;
							break;
						}
					}
					if (xWillCollide) {
						break;
					}
				}
			}
			for (int j = Math.max(minX, 0); j <= Math.min(maxX, map.width - 1); j++) {
				int i = Math.max(minY, 0);
				GameObject mo = mapObjects[i][j];
				if (terrain[i][j].equals(Color.BLACK) && pointToRectSqrDist(mo.posX, mo.posX + mo.width, mo.posY, mo.posY + mo.height,
						nextX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
					int minX2 = (int) (posX * map.width / screenSize.getWidth());
					int maxX2 = ((posX + width) * map.width % screenSize.getWidth() == 0)
							? (int) ((posX + width) * map.width / screenSize.getWidth()) - 1
							: (int) ((posX + width) * map.width / screenSize.getWidth());

					for (int j2 = Math.max(minX2, 0); j2 <= Math.min(maxX2, map.width - 1); j2++) {
						GameObject mo2 = mapObjects[i][j2];
						if (terrain[i][j2].equals(Color.BLACK) && pointToRectSqrDist(mo2.posX, mo2.posX + mo2.width, mo2.posY,
								mo2.posY + mo2.height, posX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
							yWillCollide = true;
							break;
						}
					}
					if (yWillCollide) {
						break;
					}
				}
			}
			for (int j = Math.max(minX, 0); j <= Math.min(maxX, map.width - 1); j++) {
				int i = Math.min(maxY, map.height - 1);
				GameObject mo = mapObjects[i][j];
				if (terrain[i][j].equals(Color.BLACK) && pointToRectSqrDist(mo.posX, mo.posX + mo.width, mo.posY, mo.posY + mo.height,
						nextX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
					int minX2 = (int) (posX * map.width / screenSize.getWidth());
					int maxX2 = ((posX + width) * map.width % screenSize.getWidth() == 0)
							? (int) ((posX + width) * map.width / screenSize.getWidth()) - 1
							: (int) ((posX + width) * map.width / screenSize.getWidth());

					for (int j2 = Math.max(minX2, 0); j2 <= Math.min(maxX2, map.width - 1); j2++) {
						GameObject mo2 = mapObjects[i][j2];
						if (terrain[i][j2].equals(Color.BLACK) && pointToRectSqrDist(mo2.posX, mo2.posX + mo2.width, mo2.posY,
								mo2.posY + mo2.height, posX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
							yWillCollide = true;
							break;
						}
					}
					if (yWillCollide) {
						break;
					}
				}
			}
			if (xWillCollide && yWillCollide) {
				return true;
			}
			boolean xStuck = xWillCollide;
			boolean yStuck = yWillCollide;
			if (!xWillCollide) {
				posX = nextX;
				xStuck = xClip();
			}
			if (!yWillCollide) {
				posY = nextY;
				yStuck = yClip();
			}
			stuck = (xStuck && yStuck) || (xStuck && (posY == 0 || posY == maximumY)) || (yStuck && (posX == 0 || posX == maximumX));

			liquidSpeedRatio = 1;
			minX = (int) (posX * map.width / screenSize.getWidth());
			minY = (int) (posY * map.height / screenSize.getHeight());
			maxX = ((posX + width) * map.width % screenSize.getWidth() == 0)
					? (int) ((posX + width) * map.width / screenSize.getWidth()) - 1
					: (int) ((posX + width) * map.width / screenSize.getWidth());
			maxY = ((posY + height) * map.height % screenSize.getHeight() == 0)
					? maxY = (int) ((posY + height) * map.height / screenSize.getHeight()) - 1
					: (int) ((posY + height) * map.height / screenSize.getHeight());
			for (int i = Math.max(minY, 0); i <= Math.min(maxY, map.height - 1); i++) {
				int j = Math.max(minX, 0);
				if (terrain[i][j].equals(new Color(137, 207, 240))) {
					liquidSpeedRatio = 2.0;
					break;
				}
			}
			for (int i = Math.max(minY, 0); i <= Math.min(maxY, map.height - 1); i++) {
				int j = Math.min(maxX, map.width - 1);
				if (terrain[i][j].equals(new Color(137, 207, 240))) {
					liquidSpeedRatio = 2.0;
					break;
				}
			}
			for (int j = Math.max(minX, 0); j <= Math.min(maxX, map.width - 1); j++) {
				int i = Math.max(minY, 0);
				if (terrain[i][j].equals(new Color(137, 207, 240))) {
					liquidSpeedRatio = 2.0;
					break;
				}
			}
			for (int j = Math.max(minX, 0); j <= Math.min(maxX, map.width - 1); j++) {
				int i = Math.min(maxY, map.height - 1);
				if (terrain[i][j].equals(new Color(137, 207, 240))) {
					liquidSpeedRatio = 2.0;
					break;
				}
			}
			distTravelled += step;
		}
		return stuck;
	}

	@Override
	public void draw(Graphics g) {
		// Barrels
		if (currentWeapon == Weapon.TRIPLET) {
			// Used to prevent excessive rounding
			int xCoords[] = new int[4];
			int yCoords[] = new int[4];

			double barrel1X[] = new double[4];
			double barrel1Y[] = new double[4];
			barrel1X[0] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.5);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.5);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.6);
			barrel1Y[1] = posY + height / 2.0 + (radius * -0.1);
			barrel1Y[2] = posY + height / 2.0 + (radius * -0.1);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.6);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.5);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.5);
			barrel1Y[0] = posY + height / 2.0 + (radius * 0.1);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.6);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.6);
			barrel1Y[3] = posY + height / 2.0 + (radius * 0.1);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.3);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.3);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.7);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.7);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.25);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.25);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.25);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.25);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);
		} else if (currentWeapon == Weapon.FLANK) {
			// Used to prevent excessive rounding
			int xCoords[] = new int[4];
			int yCoords[] = new int[4];

			double barrel1X[] = new double[4];
			double barrel1Y[] = new double[4];
			barrel1X[0] = posX + width / 2.0 + (radius * 0.4);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.4);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.6);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.6);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.55);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.0);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.0);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.55);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.4);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.4);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.6);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.6);
			barrel1Y[0] = posY + height / 2.0 + (radius * 0.0);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.55);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.55);
			barrel1Y[3] = posY + height / 2.0 + (radius * 0.0);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.4);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.4);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.6);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.6);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.55);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.0);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.0);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.55);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation + 180, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.4);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.4);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.6);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.6);
			barrel1Y[0] = posY + height / 2.0 + (radius * 0.0);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.55);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.55);
			barrel1Y[3] = posY + height / 2.0 + (radius * 0.0);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation + 180, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);
		} else if (currentWeapon == Weapon.SPRAYER) {
			// Used to prevent excessive rounding
			int xCoords[] = new int[4];
			int yCoords[] = new int[4];

			double barrel1X[] = new double[4];
			double barrel1Y[] = new double[4];
			barrel1X[0] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.3);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.3);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.275);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.275);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation + 45, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.3);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.3);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.275);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.275);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation - 45, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.3);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.3);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.5);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.5);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.275);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.275);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation + 22.5, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.3);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.3);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.5);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.5);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.275);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.275);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation - 22.5, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);

			barrel1X[0] = posX + width / 2.0 + (radius * 0.5);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.5);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.7);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.7);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.275);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.275);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.275);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);
		} else if (currentWeapon == Weapon.TRAPPER) {
			// Used to prevent excessive rounding
			int xCoords[] = new int[4];
			int yCoords[] = new int[4];

			double barrel1X[] = new double[4];
			double barrel1Y[] = new double[4];
			barrel1X[0] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[1] = posX + width / 2.0 + (radius * 0.1);
			barrel1X[2] = posX + width / 2.0 + (radius * 1.5);
			barrel1X[3] = posX + width / 2.0 + (radius * 1.5);
			barrel1Y[0] = posY + height / 2.0 + (radius * -0.4);
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.4);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.8);
			barrel1Y[3] = posY + height / 2.0 + (radius * -0.8);
			for (int i = 0; i < 4; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation, barrel1X[i], barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.setColor(Color.GRAY);
			g.fillPolygon(xCoords, yCoords, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 4);
		}

		if (id == 1) {
			g.setColor(new Color(255, 215, 0));
		} else if (id == 2) {
			g.setColor(Color.RED);
		}
		// Body
		g.fillOval((int) posX, (int) posY, width, height);
		g.setColor(Color.BLACK);
		g.drawOval((int) posX, (int) posY, width, height);

		// Health bar
		g.setColor(new Color(60, 179, 113));
		g.fillRoundRect((int) (posX - 0.1 * width), (int) (posY - 0.3 * height), (int) (width * 1.2 * health / INIT_HEALTH), (int) (height * 0.2), 2, 2);
		g.setColor(Color.BLACK);
		g.drawRoundRect((int) (posX - 0.1 * width), (int) (posY - 0.3 * height), (int) (width * 1.2), (int) (height * 0.2), 2, 2);
	}
}
