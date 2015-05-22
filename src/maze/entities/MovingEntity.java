package maze.entities;

import math.vecmat.Vec3;
import maze.Entity;
import maze.Gravity;
import maze.Maze;

public abstract class MovingEntity extends Entity {

	protected Gravity currentGravityMode;

	public MovingEntity(Maze m, Vec3 pos) {
		super(m, pos);
	}

	@Override
	public Gravity gravityType() {
		return this.currentGravityMode;
	}

	public void toggleGravityMode() {
		this.currentGravityMode = this.currentGravityMode.toggle();
	}

}