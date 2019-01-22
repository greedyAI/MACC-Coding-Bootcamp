import java.awt.*;

public class PlayerProjectile extends GameObject {

	public int damage;
	public double speed;
	public int penetration;
	public int range;

	public Player source;
	public double orientation;

	public int penetrationLeft = 0;
	public int distanceTravelled = 0;

	public PlayerProjectile(int arenaWidth, int arenaHeight, double startPosX, double startPosY, int width, int height,
			int initMass, int initDamage, double initSpeed, int initPenetration, int initRange, Player source) {
		super(arenaWidth, arenaHeight, 0, 0, startPosX, startPosY, width, height, initMass);
		this.damage = initDamage;
		this.speed = initSpeed;
		this.penetration = initPenetration;
		this.range = initRange;
		this.source = source;
		penetrationLeft = penetration;
		orientation = source.orientation;
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
				if (terrain[i][j].equals(Color.BLACK) && pointToRectSqrDist(mo.posX, mo.posX + mo.width, mo.posY,
						mo.posY + mo.height, nextX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
					int minY2 = (int) (posY * map.height / screenSize.getHeight());
					int maxY2 = ((posY + height) * map.height % screenSize.getHeight() == 0)
							? (int) ((posY + height) * map.height / screenSize.getHeight()) - 1
							: (int) ((posY + height) * map.height / screenSize.getHeight());

					for (int i2 = Math.max(minY2, 0); i2 <= Math.min(maxY2, map.height - 1); i2++) {
						GameObject mo2 = mapObjects[i2][j];
						if (terrain[i2][j].equals(Color.BLACK)
								&& pointToRectSqrDist(mo2.posX, mo2.posX + mo2.width, mo2.posY, mo2.posY + mo2.height,
										nextX + width / 2.0, posY + height / 2.0) <= radius * radius) {
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
				if (terrain[i][j].equals(Color.BLACK) && pointToRectSqrDist(mo.posX, mo.posX + mo.width, mo.posY,
						mo.posY + mo.height, nextX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
					int minY2 = (int) (posY * map.height / screenSize.getHeight());
					int maxY2 = ((posY + height) * map.height % screenSize.getHeight() == 0)
							? (int) ((posY + height) * map.height / screenSize.getHeight()) - 1
							: (int) ((posY + height) * map.height / screenSize.getHeight());

					for (int i2 = Math.max(minY2, 0); i2 <= Math.min(maxY2, map.height - 1); i2++) {
						GameObject mo2 = mapObjects[i2][j];
						if (terrain[i2][j].equals(Color.BLACK)
								&& pointToRectSqrDist(mo2.posX, mo2.posX + mo2.width, mo2.posY, mo2.posY + mo2.height,
										nextX + width / 2.0, posY + height / 2.0) <= radius * radius) {
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
				if (terrain[i][j].equals(Color.BLACK) && pointToRectSqrDist(mo.posX, mo.posX + mo.width, mo.posY,
						mo.posY + mo.height, nextX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
					int minX2 = (int) (posX * map.width / screenSize.getWidth());
					int maxX2 = ((posX + width) * map.width % screenSize.getWidth() == 0)
							? (int) ((posX + width) * map.width / screenSize.getWidth()) - 1
							: (int) ((posX + width) * map.width / screenSize.getWidth());

					for (int j2 = Math.max(minX2, 0); j2 <= Math.min(maxX2, map.width - 1); j2++) {
						GameObject mo2 = mapObjects[i][j2];
						if (terrain[i][j2].equals(Color.BLACK)
								&& pointToRectSqrDist(mo2.posX, mo2.posX + mo2.width, mo2.posY, mo2.posY + mo2.height,
										posX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
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
				if (terrain[i][j].equals(Color.BLACK) && pointToRectSqrDist(mo.posX, mo.posX + mo.width, mo.posY,
						mo.posY + mo.height, nextX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
					int minX2 = (int) (posX * map.width / screenSize.getWidth());
					int maxX2 = ((posX + width) * map.width % screenSize.getWidth() == 0)
							? (int) ((posX + width) * map.width / screenSize.getWidth()) - 1
							: (int) ((posX + width) * map.width / screenSize.getWidth());

					for (int j2 = Math.max(minX2, 0); j2 <= Math.min(maxX2, map.width - 1); j2++) {
						GameObject mo2 = mapObjects[i][j2];
						if (terrain[i][j2].equals(Color.BLACK)
								&& pointToRectSqrDist(mo2.posX, mo2.posX + mo2.width, mo2.posY, mo2.posY + mo2.height,
										posX + width / 2.0, nextY + height / 2.0) <= radius * radius) {
							yWillCollide = true;
							break;
						}
					}
					if (yWillCollide) {
						break;
					}
				}
			}
			if (!xWillCollide) {
				posX = nextX;
				if (xClip()) {
					xStep *= -1;
					vX *= -1;
				}
			} else {
				xStep *= -1;
				vX *= -1;
			}
			if (!yWillCollide) {
				posY = nextY;
				if (yClip()) {
					yStep *= -1;
					vY *= -1;
				}
			} else {
				yStep *= -1;
				vY *= -1;
			}

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
		return false;
	}

	@Override
	public void draw(Graphics g) {
		if (source.id == 1) {
			g.setColor(new Color(255, 215, 0));
		} else if (source.id == 2) {
			g.setColor(Color.RED);
		}
		if (this instanceof TrapperWeapon) {
			// Used to prevent excessive rounding
			int xCoords[] = new int[6];
			int yCoords[] = new int[6];

			double barrel1X[] = new double[6];
			double barrel1Y[] = new double[6];
			barrel1X[0] = posX + width / 2.0 - radius;
			barrel1X[1] = posX + width / 2.0 - (radius * 0.167);
			barrel1X[2] = posX + width / 2.0 + (radius * 0.5);
			barrel1X[3] = posX + width / 2.0 + (radius * 0.333);
			barrel1X[4] = posX + width / 2.0 + (radius * 0.5);
			barrel1X[5] = posX + width / 2.0 - (radius * 0.167);
			barrel1Y[0] = posY + height / 2.0;
			barrel1Y[1] = posY + height / 2.0 + (radius * 0.289);
			barrel1Y[2] = posY + height / 2.0 + (radius * 0.866);
			barrel1Y[3] = posY + height / 2.0;
			barrel1Y[4] = posY + height / 2.0 - (radius * 0.866);
			barrel1Y[5] = posY + height / 2.0 - (radius * 0.289);
			for (int i = 0; i < 6; i++) {
				double[] result = rotatePoint(posX + width / 2.0, posY + height / 2.0, orientation, barrel1X[i],
						barrel1Y[i]);
				xCoords[i] = (int) Math.round(result[0]);
				yCoords[i] = (int) Math.round(result[1]);
			}

			g.fillPolygon(xCoords, yCoords, 6);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoords, yCoords, 6);
		} else {
			g.fillOval((int) Math.round(posX), (int) Math.round(posY), width, height);
			g.setColor(Color.BLACK);
			g.drawOval((int) Math.round(posX), (int) Math.round(posY), width, height);
		}
	}
}