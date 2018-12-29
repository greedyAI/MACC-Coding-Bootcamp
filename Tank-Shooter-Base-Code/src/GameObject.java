import java.awt.Graphics;

public class GameObject {
	// Every moving object in our game is a separate GameObject. However, since there are different types of moving objects (eg. players and
	// projectiles), each with their own variables and functions, we don't want to instantiate GameObjects (otherwise, we wouldn't be able
	// to distinguish between a player and a projectile). Instead, we want to create derived (also known as child) classes to implement the
	// separate functionality, and instantiate the derived/child classes instead. The purpose of this GameObject "parent" class is to
	// provide the derived/child classes with a set of useful functions for handling physics interactions (eg. collisions with the terrain
	// or the walls of the arena) between different GameObjects (eg. player/player, player/projectile, or even projectile/projectile). Note:
	// depending on the features you implement, you may not end up using all of the functions provided in this class.

	// Every GameObject contains its own velocity, position, size, and mass information (necessary for moving and handling physics
	// interactions). We store this information in the GameObject class instead of the derived/child classes because every type of
	// GameObject has the same set of velocity, position, size, and mass variables.

	public double vX; // Velocity in the x direction. Positive if moving right. Negative if moving left.
	public double vY; // Velocity in the y direction. Positive if moving down. Negative if moving up. Notice how it's flipped from the usual
						// coordinate system in math!

	public double posX; // Position along the x direction. Ranges from 0 (left edge) to maximumX (see below).
	public double posY; // Position along the y direction. Ranges from 0 (top edge) to maximumY (see below).

	public int width; // Width of the bounding box of the GameObject.
	public int height; // Height of the bounding box of the GameObject.

	public int mass; // Used in realistic physics collision calculations. Ignore it if you don't want to implement that feature.
	public double radius; // Used for circular GameObjects only.

	public int maximumX; // Maximum x position for a GameObject, equal to the arena width subtracted by the game object's width.
	public int maximumY; // Maximum y position for a GameObject, equal to the arena height subtracted by the game object's height.

	// Constructor. All derived (ie. child) classes call this constructor in their own constructors. The resulting derived/child class
	// object can then call the other functions in this class.
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

	// Note: No need to change this function since we're going to override it in the the child classes.
	public boolean move(Map map, double translateX, double translateY) {
		return false;
	}

	// Check if the calling GameObject currently intersects with the obj GameObject.
	public boolean currentlyIntersects(GameObject obj) {
		return (posX + width >= obj.posX && posY + height >= obj.posY && obj.posX + obj.width >= posX && obj.posY + obj.height >= posY);
	}

	// Check if the calling GameObject will intersect with the obj GameObject, after both have moved according to their velocities. A note
	// of caution: what might go wrong if either player moves too fast?
	public boolean willIntersect(GameObject obj) {
		double nextX = posX + vX;
		double nextY = posY + vY;
		double nextObjX = obj.posX + obj.vX;
		double nextObjY = obj.posY + obj.vY;
		return (nextX + width >= nextObjX && nextY + height >= nextObjY && nextObjX + obj.width >= nextX && nextObjY + obj.height >= nextY);
	}

	// Clip the calling GameObject to within the arena's x bounds, if it has moved outside the arena along the x direction.
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

	// Clip the calling GameObject to within the arena's y bounds, if it has moved outside the arena along the y direction.
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

	// If the calling GameObject will move outside the arena along either direction (after moving according to its velocity), this function
	// tells you which of the four edges of the arena it hit. If the calling GameObject will stay within the bounds of the arena, this
	// function returns null.
	public Direction hitEdgeDirection() {
		if (posX + vX < 0) {
			return Direction.LEFT;
		} else if (posX + vX > maximumX) {
			return Direction.RIGHT;
		}

		if (posY + vY < 0) {
			return Direction.UP;
		} else if (posY + vY > maximumY) {
			return Direction.DOWN;
		} else {
			return null;
		}
	}

	// If the calling GameObject will intersect with the "other" GameObject (after both move according to their velocities), this function
	// tells you which of the four sides of the calling GameObject that the "other" GameObject hit. If the calling GameObject will not
	// intersect with the "other" GameObject, this function returns null. Note: this function is great for figuring out when and where two
	// rectangles intersect, but is it a good choice for handling circle/rectangle or circle/circle intersections?
	public Direction hitObjectDirection(GameObject other) {
		if (this.willIntersect(other)) {
			double dx = other.posX + other.width / 2.0 + other.vX - (posX + width / 2.0 + vX);
			double dy = other.posY + other.height / 2.0 + other.vY - (posY + height / 2.0 + vY);

			double theta = Math.acos(dx / (Math.sqrt(dx * dx + dy * dy)));
			double diagTheta = Math.atan2(height / 2.0, width / 2.0);

			if (theta <= diagTheta) {
				return Direction.RIGHT;
			} else if (theta <= Math.PI - diagTheta) {
				if (dy > 0) {
					return Direction.DOWN;
				} else {
					return Direction.UP;
				}
			} else {
				return Direction.LEFT;
			}
		} else {
			return null;
		}
	}

	// Change the calling GameObject's velocity (to simulate a "bouncing" effect) based on which direction it intersected another GameObject
	// or the edge of the arena. If the passed in direction is null, this function does nothing (why is this a good idea?). This function is
	// best used with the hitEdgeDirection and hitObjectDirection functions above.
	public void bounce(Direction d) {
		if (d == null) {
			return;
		}
		// Note: We probably should use a "switch" statement here instead. But for pedagogical purposes it's left as a simple if/else
		// conditional.
		if (d == Direction.UP) {
			vY = Math.abs(vY);
		} else if (d == Direction.DOWN) {
			vY = -Math.abs(vY);
		} else if (d == Direction.LEFT) {
			vX = Math.abs(vX);
		} else if (d == Direction.RIGHT) {
			vX = -Math.abs(vX);
		}
	}

	// TODO: (Challenge!) If you want to implement realistic sphere-sphere collisions that take into account the laws of physics, do so in
	// the function below.
	public boolean bounceWith(GameObject otherObj, Map map, long frames, double[] actualVelocities) {
		return false;
	}

	// Calculate the distance from (pointX, pointY)---perhaps representing the center of a circle---to the closest point on a rectangle
	// bounded by minX (left), maxX (right), minY (top), and maxY (bottom). If the point is inside the rectangle, this function returns 0.
	public double pointToRectSqrDist(double minX, double maxX, double minY, double maxY, double pointX, double pointY) {
		double dx = Math.max(Math.max(minX - pointX, 0), pointX - maxX);
		double dy = Math.max(Math.max(minY - pointY, 0), pointY - maxY);
		return dx * dx + dy * dy;
	}

	// Rotate the point (x, y) "degrees" degrees around (centerX, centerY) in counterclockwise fashion, and return the resulting point in an
	// array of length 2. If the returned array is "result", then (result[0], result[1]) is the final point.
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

	// Note: No need to change this function since we're going to override it in the the child classes.
	public void draw(Graphics g) {
	}
}
