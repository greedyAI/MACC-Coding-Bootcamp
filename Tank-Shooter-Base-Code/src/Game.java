
// Imports necessary libraries for Java Swing.
import java.awt.*;
import javax.swing.*;

// Main game class that constructs the widgets/frames of the GUI (graphic user interface) and launches the initial instance of the game arena.
public class Game implements Runnable {

	public void run() {
		// Create the top-level frame in which the game components live.
		final JFrame frame = new JFrame("TODO: Fill your team members' names here (and a title for you game, if you wish)");
		frame.setLayout(new GridBagLayout());
		frame.setLocation(0, 0);

		// Java inherently takes a while to initialize fonts. To prevent lags in-game, the following chunk of code will help in
		// front-loading the font initialization/rendering time by immediately displaying a line of text upon game start.
		final JPanel startPanel = new JPanel();
		startPanel.setLayout(new BorderLayout(0, 0));
		JLabel loading = new JLabel("Loading...");
		loading.setHorizontalAlignment(JLabel.CENTER);
		loading.setForeground(Color.BLACK);
		loading.setFont(new Font("GillSans", Font.PLAIN, 64));
		startPanel.add(loading, BorderLayout.CENTER);
		startPanel.setOpaque(false);
		frame.getContentPane().add(startPanel, new GridBagConstraints());

		// Put the game frame on the screen and set it to span the entire screen width and height.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Instantiate and display our arena.
		final Arena court = new Arena();
		frame.setContentPane(court);
		frame.validate();
		court.reset();
	}

	// Main method that launches the entire game by creating a Game instance to instantiate its GUI elements.
	// WARNING: Do NOT delete this function! This MUST be in your final game/code submission.
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}
