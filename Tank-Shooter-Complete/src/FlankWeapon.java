
public class FlankWeapon extends PlayerProjectile {

	public static final int INIT_SIZE = 9;
	public static final int INIT_MASS = 9;

	public static final int INIT_DAMAGE = 3;
	public static final double INIT_SPEED = 0.3;
	public static final int INIT_PENETRATION = 1;
	public static final int INIT_RANGE = 800;
	public static final long INIT_RELOAD = 600;

	public FlankWeapon(int arenaWidth, int arenaHeight, double startPosX, double startPosY, Player source) {
		super(arenaWidth, arenaHeight, startPosX, startPosY, INIT_SIZE, INIT_SIZE, INIT_MASS, INIT_DAMAGE, INIT_SPEED, INIT_PENETRATION,
				INIT_RANGE, source);
	}
}
