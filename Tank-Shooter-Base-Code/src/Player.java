
// TODO: Feel free to import any other libraries that you need.
import java.awt.*;

public class Player extends GameObject {

	// TODO: Set these values as you see fit. However, which variables should you not set as negative numbers? Which variables should you
	// not set as zero? Which variables should you not set as a very large positive number? Why?
	public static final int INIT_SIZE = 30;
	public static final int INIT_MASS = 0;

	public static final int INIT_DAMAGE = 0;
	public static final double INIT_SPEED = 0;
	public static final double INIT_ROTATE_SPEED = 0;
	public static final int INIT_HEALTH = 100;

	// Member variables of the player that you can change over the course of the game.
	public int damage = INIT_DAMAGE;
	public double speed = INIT_SPEED;
	public double rotateSpeed = INIT_ROTATE_SPEED;
	public int health = INIT_HEALTH;
	public double orientation = 0;
	public int id;

	// TODO: You may need to set up extra variables to store the projectiles fired by this player, the reload status/time of this player,
	// the key press/release status of this player, and any other player-related features you decide to implement. Make sure to update the
	// constructor appropriately as well!

	// Constructor that calls the super (ie. parent) class's constructor and instantiates any other player-specific variables.
	public Player(int arenaWidth, int arenaHeight, double startPosX, double startPosY, int id) {
		super(arenaWidth, arenaHeight, 0, 0, startPosX, startPosY, INIT_SIZE, INIT_SIZE, INIT_MASS);
		this.id = id;
	}

	// TODO: This function should move the player and handle any player-terrain interactions.
	@Override
	public boolean move(Map map, double translateX, double translateY) {
		return false;
	}

	@Override
	public void draw(Graphics g) {
		// TODO: Draw the barrel(s) for the player here.

		if (id == 1) {
			g.setColor(new Color(255, 215, 0));
		} else if (id == 2) {
			g.setColor(Color.RED);
		}
		// Body
		g.fillOval((int) posX, (int) posY, width, height);
		g.setColor(Color.BLACK);
		g.drawOval((int) posX, (int) posY, width, height);

		// TODO: Draw the health bar for the player here.

	}
}
