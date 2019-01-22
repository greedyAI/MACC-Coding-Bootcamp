import java.awt.Graphics;

public class GameObject {
	public double vX;
	public double vY;

	public double posX;
	public double posY;

	public int width;
	public int height;

	public int mass;
	public double radius;

	public int maximumX;
	public int maximumY;

	public GameObject(int arenaWidth, int arenaHeight, double vX, double vY, double posX, double posY, int width, int height, int mass) {
		this.vX = vX;
		this.vY = vY;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.mass = mass;
		this.maximumX = arenaWidth - width;
		this.maximumY = arenaHeight - height;

		radius = Math.min(width, height) / 2.0;
	}

	public boolean move(Map map, double translateX, double translateY) {
		return false;
	}

	public boolean xClip() {
		if (posX < 0) {
			posX = 0;
			return true;
		} else if (posX > maximumX) {
			posX = maximumX;
			return true;
		}
		return false;
	}

	public boolean yClip() {
		if (posY < 0) {
			posY = 0;
			return true;
		} else if (posY > maximumY) {
			posY = maximumY;
			return true;
		}
		return false;
	}

	public boolean bounceWith(GameObject otherObj, Map map, long frames, double[] actualVelocities) {
		// Check for intersection
		double dx = (posX + radius) - (otherObj.posX + otherObj.radius);
		double dy = (posY + radius) - (otherObj.posY + otherObj.radius);
		double distSquared = dx * dx + dy * dy;
		if (distSquared <= (radius + otherObj.radius) * (radius + otherObj.radius)) {
			// Bounce off
			double dist = Math.sqrt(distSquared);
			double pushDistanceX = dx * (radius + otherObj.radius - dist) / dist;
			double pushDistanceY = dy * (radius + otherObj.radius - dist) / dist;
			double impulseA = 1.0 / mass;
			double impulseB = 1.0 / otherObj.mass;
			if (this.move(map, pushDistanceX * impulseA / (impulseA + impulseB), pushDistanceY * impulseA / (impulseA + impulseB))) {
				otherObj.move(map, -pushDistanceX, -pushDistanceY);
			} else {
				if (otherObj.move(map, -pushDistanceX * impulseB / (impulseA + impulseB), -pushDistanceY * impulseB / (impulseA + impulseB))) {
					this.move(map, pushDistanceX - (pushDistanceX * impulseA / (impulseA + impulseB)),
							pushDistanceY - (pushDistanceY * impulseA / (impulseA + impulseB)));
				}
			}

			// Update velocities using vector mathematics
			double xVelocity = actualVelocities[0] - actualVelocities[2];
			double yVelocity = actualVelocities[1] - actualVelocities[3];
			double pushDistance = Math.sqrt(pushDistanceX * pushDistanceX + pushDistanceY * pushDistanceY);
			if (pushDistance != 0) {
				pushDistanceX /= pushDistance;
				pushDistanceY /= pushDistance;
			} else {
				pushDistanceX = 0;
				pushDistanceY = 0;
			}

			double dotProduct = pushDistanceX * xVelocity + pushDistanceY * yVelocity;

			// Assume 2 is the coefficient of restitution
			double restitutionFactor = (-(1 + 1) * dotProduct) / (impulseA + impulseB);
			double impulseX = pushDistanceX * restitutionFactor;
			double impulseY = pushDistanceY * restitutionFactor;
			if (dotProduct <= 0) {
				vX += impulseX * impulseA;
				vY += impulseY * impulseA;
				otherObj.vX -= impulseX * impulseB;
				otherObj.vY -= impulseY * impulseB;
			}

			// If collided objects are players, calculate body damage that they do to each other
			if (this instanceof Player && otherObj instanceof Player) {
				Player a = (Player) this;
				Player b = (Player) otherObj;
				double maxMomentumA = a.mass * a.speed * frames;
				double maxMomentumB = b.mass * b.speed * frames;
				double maxMomentum = Math.max(maxMomentumA, maxMomentumB);
				double momentumA = Math.sqrt(actualVelocities[0] * actualVelocities[0] + actualVelocities[1] * actualVelocities[1])
						* a.mass;
				double momentumB = Math.sqrt(actualVelocities[2] * actualVelocities[2] + actualVelocities[3] * actualVelocities[3])
						* b.mass;
				double ratioA = momentumA / (momentumA + momentumB);
				a.damage = (int) Math
						.max(Math.round(a.damage * ratioA * Math.sqrt(impulseX * impulseX + impulseY * impulseY) / maxMomentum), 0);
				b.damage = (int) Math
						.max(Math.round(b.damage * (1 - ratioA) * Math.sqrt(impulseX * impulseX + impulseY * impulseY) / maxMomentum), 0);
			}
			return true;
		}
		return false;
	}

	public double pointToRectSqrDist(double minX, double maxX, double minY, double maxY, double pointX, double pointY) {
		double dx = Math.max(Math.max(minX - pointX, 0), pointX - maxX);
		double dy = Math.max(Math.max(minY - pointY, 0), pointY - maxY);
		return dx * dx + dy * dy;
	}

	public double[] rotatePoint(double centerX, double centerY, double degrees, double x, double y) {
		double s = Math.sin(Math.toRadians(degrees));
		double c = Math.cos(Math.toRadians(degrees));
		x -= centerX;
		y -= centerY;
		double xNew = x * c - y * s;
		double yNew = x * s + y * c;
		double[] result = new double[2];
		result[0] = xNew + centerX;
		result[1] = yNew + centerY;
		return result;
	}

	public void draw(Graphics g) {
	}
}
