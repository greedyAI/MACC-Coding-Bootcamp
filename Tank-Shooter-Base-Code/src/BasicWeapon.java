
public class BasicWeapon extends PlayerProjectile {

	// TODO: Set these values as you see fit. However, try not to set negative values for any of these variables and very large values for
	// INIT_SIZE and INIT_SPEED. Why not?
	public static final int INIT_SIZE = 0;
	public static final int INIT_MASS = 0;

	public static final int INIT_DAMAGE = 0;
	public static final double INIT_SPEED = 0;
	public static final int INIT_PENETRATION = 0;
	public static final int INIT_RANGE = 0;
	public static final long INIT_RELOAD = 0;

	// Constructor that calls the super (ie. parent) class's constructor.
	public BasicWeapon(int arenaWidth, int arenaHeight, double startPosX, double startPosY, Player source) {
		super(arenaWidth, arenaHeight, startPosX, startPosY, INIT_SIZE, INIT_SIZE, INIT_MASS, INIT_DAMAGE, INIT_SPEED, INIT_PENETRATION,
				INIT_RANGE, source);
	}
}
