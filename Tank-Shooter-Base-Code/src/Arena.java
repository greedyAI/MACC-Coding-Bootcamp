
// TODO: Feel free to import any other libraries that you need.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Arena extends JPanel {
	public int arenaWidth;
	public int arenaHeight;

	public Player player1;
	public Player player2;

	Timer timer;
	public static int INTERVAL = 35;
	public long lastTick;

	// TODO: Add other variables to keep track of the game state or other game objects (eg. the map) that will be in your game. Don't forget
	// to instantiate them in reset()!

	// Constructor. Called inside Game.java for setting up the Arena on game start.
	public Arena() {
		// Create a timer that calls the tick() function every INTERVAL milliseconds. Every call of the tick() function is a "frame".
		timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		lastTick = System.currentTimeMillis();
		timer.start();

		setFocusable(true);

		// TODO: To recognize key presses, you need to fill in the following.
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

			}

			public void keyReleased(KeyEvent e) {

			}
		});
	}

	// Resets the game to its initial state.
	public void reset() {
		this.removeAll();
		this.setBackground(Color.WHITE);
		this.setOpaque(true);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		arenaWidth = (int) screenSize.getWidth();
		arenaHeight = (int) screenSize.getHeight();

		player1 = new Player(arenaWidth, arenaHeight, 100, 100, 1);
		player2 = new Player(arenaWidth, arenaHeight, arenaWidth - Player.INIT_SIZE - 100, arenaHeight - Player.INIT_SIZE - 100, 2);

		requestFocusInWindow();
	}

	// Function called once per "frame".
	void tick() {
		// While tick() should be called once every INTERVAL amount of time, there's no guarantee of that, particularly if you have a lot
		// of background apps running. Thus, we need to calculate the time difference (timeDelta) between every two calls of the tick()
		// function. Note: 1 divided by this difference is commonly known as the "frames per second", or fps.
		long currentTime = System.currentTimeMillis();
		long timeDelta = currentTime - lastTick;
		lastTick = currentTime;

		// TODO: Update the game state each frame. This can be broken into the following steps:
		// Step 1: Handle the keys pressed during the last frame by both players and calculate their resulting velocities/orientations.
		// Step 2: Move the players and detect/handle player/player collisions and player/terrain collisions.
		// Step 3: Decide whether a bullet should be fired for each player and create new bullet(s) if so. Also, handle reload mechanics.
		// Step 4: Move all bullets via their calculated velocities (up to bullet range). Handle bullet/player & bullet/terrain collisions.
		// Step 5: Decide whether the game has ended. If so, stop the timer and print a message to the screen indicating who's the winner.
		// Note: If you implement other features (eg. weapon swapping, damage counters...etc.), you might also need to add more steps above.

		// Update the display: this function calls paintComponent as part of its execution.
		repaint();
	}

	// TODO: Draw all of the objects in your game.
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		player1.draw(g);
		player2.draw(g);

	}

	// Returns the dimensions of the Arena (for properly resizing the JPanel on the screen).
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(arenaWidth, arenaHeight);
	}
}
