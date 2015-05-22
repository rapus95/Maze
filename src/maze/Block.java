package maze;

import math.vecmat.Vec3;
import maze.entities.Bomb;

public class Block {

	public double onExplode(BlockData blockData, Bomb bomb, Vec3 pos, double remaining) {
		return remaining;
	}

}
