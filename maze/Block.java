package maze;

import math.matrix.Vec;
import maze.entities.Bomb;

public class Block {

	public double onExplode(BlockData blockData, Bomb bomb, Vec pos, double remaining) {
		return remaining;
	}

}
