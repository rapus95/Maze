package maze;

import math.matrix.Vec;
import maze.entities.Bomb;

public class BlockData {

	public final Block block;
	
	public final Vec vec;
	
	public BlockData(Block block, Vec vec){
		this.block = block;
		this.vec = vec;
	}

	public double onExplode(Bomb bomb, Vec pos, double remaining) {
		return block.onExplode(this, bomb, pos, remaining);
	}
	
}
