package maze.blocks;

import math.vecmat.Vec3;
import maze.Block;
import maze.BlockData;
import maze.entities.Bomb;

public class Wall extends Block{
	
	private Wall() {}

	public static final Wall INSTANCE = new Wall();

	@Override
	public double onExplode(BlockData blockData, Bomb bomb, Vec3 pos, double remaining) {
		return 0;
	}

	
	
}
