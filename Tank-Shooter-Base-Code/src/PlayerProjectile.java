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

	// Constructor that calls the super (ie. parent) class's constructor and instantiates any other projectile-specific variables.
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

	// TODO: This function should move the projectile and handle any projectile-terrain interactions.
	@Override
	public boolean move(Map map, double translateX, double translateY) {
		return false;
	}

	// TODO: Draw the projectile/bullet here.
	@Override
	public void draw(Graphics g) {
		
	}
}