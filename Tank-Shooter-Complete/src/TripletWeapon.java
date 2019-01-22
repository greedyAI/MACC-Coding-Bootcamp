
public class TripletWeapon extends PlayerProjectile {

	public static final int INIT_SIZE = 7;
	public static final int INIT_MASS = 7;

	public static final int INIT_DAMAGE = 3;
	public static final double INIT_SPEED = 0.4;
	public static final int INIT_PENETRATION = 1;
	public static final int INIT_RANGE = 600;
	public static final long INIT_RELOAD = 450;

	public TripletWeapon(int arenaWidth, int arenaHeight, double startPosX, double startPosY, Player source) {
		super(arenaWidth, arenaHeight, startPosX, startPosY, INIT_SIZE, INIT_SIZE, INIT_MASS, INIT_DAMAGE, INIT_SPEED, INIT_PENETRATION,
				INIT_RANGE, source);
	}
}
