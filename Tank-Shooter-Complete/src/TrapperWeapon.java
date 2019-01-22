
public class TrapperWeapon extends PlayerProjectile {

	public static final int INIT_SIZE = 22;
	public static final int INIT_MASS = 22;

	public static final int INIT_DAMAGE = 10;
	public static final double INIT_SPEED = 0.75;
	public static final int INIT_PENETRATION = 2;
	public static final int INIT_RANGE = 10000;
	public static final long INIT_RELOAD = 1000;

	public TrapperWeapon(int arenaWidth, int arenaHeight, double startPosX, double startPosY, Player source) {
		super(arenaWidth, arenaHeight, startPosX, startPosY, INIT_SIZE, INIT_SIZE, INIT_MASS, INIT_DAMAGE, INIT_SPEED, INIT_PENETRATION,
				INIT_RANGE, source);
	}
}
