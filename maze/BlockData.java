package maze;

import math.vecmat.Vec3;
import maze.entities.Bomb;

public class BlockData {

	public final Block block;
	
	public final Vec3 vec;
	
	public BlockData(Block block, Vec3 vec){
		this.block = block;
		this.vec = vec;
	}

	public double onExplode(Bomb bomb, Vec3 pos, double remaining) {
		return block.onExplode(this, bomb, pos, remaining);
	}
	
}
