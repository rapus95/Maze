package maze.blocks;

import math.matrix.IVec;
import maze.Block;
import maze.BlockData;
import maze.entities.Bomb;

public class Wall extends Block{
	
	private Wall() {}

	public static final Wall INSTANCE = new Wall();

	@Override
	public double onExplode(BlockData blockData, Bomb bomb, IVec pos, double remaining) {
		return 0;
	}

	
	
}
