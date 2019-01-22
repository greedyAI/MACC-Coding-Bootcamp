import java.awt.*;

public class DamageCounter {

	public Player target;

	public int damage;

	public static final int FRAMES = 30;
	public int framesExisting;

	public DamageCounter(Player target, int damage) {
		this.target = target;
		this.damage = damage;
	}

	public void draw(Graphics g) {
		if (damage == 0) {
			return;
		}
		String result;
		if (damage < 0) {
			result = "+" + Integer.toString(Math.abs(damage));
		} else {
			result = "-" + Integer.toString(damage);
		}
		g.setColor(new Color(255, 127, 0));
		g.setFont(new Font("GillSans", Font.BOLD, 16));
		g.drawString(result, (int) (target.posX + (0.5 - result.length() / 8.0) * target.width),
				(int) (target.posY - 0.2 * target.height - framesExisting * 1.5));
	}
}
