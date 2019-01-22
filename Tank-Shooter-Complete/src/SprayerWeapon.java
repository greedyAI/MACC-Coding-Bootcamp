
public class SprayerWeapon extends PlayerProjectile {

	public static final int INIT_SIZE = 5;
	public static final int INIT_MASS = 5;

	public static final int INIT_DAMAGE = 2;
	public static final double INIT_SPEED = 0.25;
	public static final int INIT_PENETRATION = 1;
	public static final int INIT_RANGE = 400;
	public static final long INIT_RELOAD = 300;

	public SprayerWeapon(int arenaWidth, int arenaHeight, double startPosX, double startPosY, Player source) {
		super(arenaWidth, arenaHeight, startPosX, startPosY, INIT_SIZE, INIT_SIZE, INIT_MASS, INIT_DAMAGE, INIT_SPEED, INIT_PENETRATION,
				INIT_RANGE, source);
	}
}
