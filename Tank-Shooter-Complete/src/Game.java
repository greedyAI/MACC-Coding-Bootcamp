import java.awt.*;
import javax.swing.*;

// Main game class that launches the initial instance of the Arena
public class Game implements Runnable {

	public void run() {
		// Create the top-level frame in which the game components live
		final JFrame frame = new JFrame("Tank Shooter");
		frame.setLayout(new GridBagLayout());
		frame.setLocation(0, 0);

		// Helps in front-loading the font rendering times
		final JPanel startPanel = new JPanel();
		startPanel.setLayout(new BorderLayout(0, 0));
		JLabel loading = new JLabel("Loading...");
		loading.setHorizontalAlignment(JLabel.CENTER);
		loading.setForeground(Color.BLACK);
		loading.setFont(new Font("GillSans", Font.PLAIN, 64));
		startPanel.add(loading, BorderLayout.CENTER);
		startPanel.setOpaque(false);
		frame.getContentPane().add(startPanel, new GridBagConstraints());

		// Put the frame on the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Instantiate and display our arena
		final Arena court = new Arena();
		frame.setContentPane(court);
		frame.validate();
		court.reset();
	}

	// Main method that launches the game
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}
