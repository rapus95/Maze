package maze;

import math.matrix.IVec;
import maze.entities.Bomb;

public class BlockData {

	public final Block block;
	
	public final IVec vec;
	
	public BlockData(Block block, IVec vec){
		this.block = block;
		this.vec = vec;
	}

	public double onExplode(Bomb bomb, IVec pos, double remaining) {
		return block.onExplode(this, bomb, pos, remaining);
	}
	
}
