
// TODO: Feel free to import any other libraries that you need.
import java.awt.*;

public class Map {
	public int height;
	public int width;
	public Color[][] map;
	public GameObject[][] mapObjects;

	// Constructor that instantiates our 2D member arrays and calls the generateMap function to actually generate the map.
	public Map(int width, int height, int courtWidth, int courtHeight, Player player1, Player player2) {
		this.height = height;
		this.width = width;
		map = new Color[height][width];
		mapObjects = new GameObject[height][width];
		generateMap(courtWidth, courtHeight, player1, player2);
	}

	// TODO: (Challenge!) Generates a random map by filling "Color[][] map" up with colors of every block in your map, and filling up
	// "GameObject[][] mapObjects" with a GameObject for each non-empty terrain block in your map (eg. walls/water/ice...etc.).
	private void generateMap(int courtWidth, int courtHeight, Player player1, Player player2) {

	}

	// Iterate through the 2D map of blocks and draw every non-empty terrain block.
	public void draw(Graphics g) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				GameObject go = mapObjects[i][j];
				if (go != null) {
					// go is a non-empty terrain block.
					g.setColor(map[i][j]);
					g.fillRect((int) go.posX, (int) go.posY, go.width, go.height);
				}
			}
		}
	}
}
