package maze;

import math.matrix.IVec;
import maze.entities.Bomb;

public class Block {

	public double onExplode(BlockData blockData, Bomb bomb, IVec pos, double remaining) {
		return remaining;
	}

}
