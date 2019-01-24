import java.awt.*;
import java.util.Random;

public class Map {
	public int height;
	public int width;
	public Color[][] map;
	public GameObject[][] mapObjects;

	public Map(int width, int height, int arenaWidth, int arenaHeight, Player player1, Player player2) {
		this.height = height;
		this.width = width;
		map = new Color[height][width];
		mapObjects = new GameObject[height][width];
		generateMap(arenaWidth, arenaHeight, player1, player2);
	}

	// Generates a random map with caves/ice based on cellular automata. May end up with the two players being disconnected from each other.
	// To solve this, a BFS or A*-style algorithm may be needed, but is omitted from the code for pedagogical purposes.
	private void generateMap(int arenaWidth, int arenaHeight, Player player1, Player player2) {
		int THRESHOLD_A = 5; // Number of walls around current cell needed to become a wall (empty -> wall)
		int THRESHOLD_B = 5; // Number of walls around current cell needed to remain a wall (wall -> wall)
		int THRESHOLD_C = 0; // Max number of walls around current cell allowed to become a wall (empty -> wall)
		int THRESHOLD_D = 10; // Max number of walls around current cell allowed to become ice (any -> ice)
		int THRESHOLD_E = 8; // Max number of walls around current cell allowed to remain ice (ice -> ice)
		int THRESHOLD_F = 16; // Max number of ice around current cell allowed to remain ice (ice -> ice)
		int THRESHOLD_G = 40; // Initial random seed threshold

		// Initialize random seed
		Random rnd = new Random(3);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				map[i][j] = (rnd.nextInt(100) < THRESHOLD_G) ? Color.BLACK : Color.WHITE;
			}
		}

		// Generate cave structure (ie. the walls)
		for (int iterations = 0; iterations < 5; iterations++) {
			Color[][] newMap = new Color[height][width];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (map[i][j].equals(Color.BLACK)) {
						if (numTerrainAround(i, j, 1, Color.BLACK) >= THRESHOLD_B) {
							newMap[i][j] = Color.BLACK;
						} else {
							newMap[i][j] = Color.WHITE;
						}
					} else {
						if (numTerrainAround(i, j, 1, Color.BLACK) >= THRESHOLD_A
								|| numTerrainAround(i, j, 2, Color.BLACK) <= THRESHOLD_C) {
							newMap[i][j] = Color.BLACK;
						} else {
							newMap[i][j] = Color.WHITE;
						}
					}
				}
			}
			map = newMap;
		}

		// Smooth the cave wall structure and add ice
		for (int iterations = 0; iterations < 10; iterations++) {
			Color[][] newMap = new Color[height][width];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (numTerrainAround(i, j, 1, Color.BLACK) >= THRESHOLD_A) {
						newMap[i][j] = Color.BLACK;
					} else if (numTerrainAround(i, j, 4, Color.BLACK) <= THRESHOLD_D) {
						newMap[i][j] = new Color(137, 207, 240);
					} else {
						newMap[i][j] = Color.WHITE;
					}
				}
			}
			map = newMap;
		}

		// Smooth the ice structure and ensure remaining white space/ice size
		for (int iterations = 0; iterations < 5; iterations++) {
			Color[][] newMap = new Color[height][width];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (map[i][j].equals(new Color(137, 207, 240))) {
						if (numTerrainAround(i, j, 6, Color.BLACK) <= THRESHOLD_E
								&& numTerrainAround(i, j, 3, new Color(137, 207, 240)) >= THRESHOLD_F) {
							newMap[i][j] = new Color(137, 207, 240);
						} else {
							newMap[i][j] = Color.WHITE;
						}
					} else {
						newMap[i][j] = map[i][j];
					}
				}
			}
			map = newMap;
		}

		// Ensure spawn locations are empty
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int leftPos = (int) screenSize.getWidth() * j / width;
				int topPos = (int) screenSize.getHeight() * i / height;
				if ((leftPos >= player1.posX - player1.width && leftPos <= player1.posX + 3 * player1.width
						&& topPos >= player1.posY - player1.height && topPos <= player1.posY + 3 * player1.height)
						|| (leftPos >= player2.posX - 2 * player2.width && leftPos <= player2.posX + 2 * player2.width
								&& topPos >= player2.posY - 2 * player2.height && topPos <= player2.posY + 2 * player2.height)) {
					map[i][j] = Color.WHITE;
				}
			}
		}

		// Create GameObjects for walls and ice
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (map[i][j].equals(Color.BLACK) || map[i][j].equals(new Color(137, 207, 240))) {
					mapObjects[i][j] = new GameObject(arenaWidth, arenaHeight, 0, 0, arenaWidth * j / width,
							arenaHeight * i / height, arenaWidth * (j + 1) / width - arenaWidth * j / width,
							arenaHeight * (i + 1) / height - arenaHeight * i / height, -1);
				}
			}
		}
	}

	// Counts the number of terrain blocks around (x, y) with type "terrain" within radius "radius"
	private int numTerrainAround(int y, int x, int radius, Color terrain) {
		int result = 0;
		int numTested = 0;
		for (int i = Math.max(0, y - radius); i <= Math.min(height - 1, y + radius); i++) {
			for (int j = Math.max(0, x - radius); j <= Math.min(width - 1, x + radius); j++) {
				numTested++;
				if (map[i][j].equals(terrain)) {
					result++;
				}
			}
		}
		return result + (2 * radius + 1) * (2 * radius + 1) - numTested;
	}

	public void draw(Graphics g) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				GameObject go = mapObjects[i][j];
				if (go != null) {
					g.setColor(map[i][j]);
					g.fillRect((int) go.posX, (int) go.posY, go.width, go.height);
				}
			}
		}
	}
}
