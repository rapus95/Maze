package maze.effects;

import math.vecmat.Vec3;
import maze.Effect;
import maze.Entity;
import maze.Gravity;
import maze.Maze;

public class Explosion extends Effect {

	private double toLive;

	public Explosion(Maze m, Vec3 pos) {
		super(m, pos.round());
		toLive = 2;
	}

	@Override
	public void tick(long timeDelta) {
		super.tick(timeDelta);
		toLive -= timeDelta / 1000_000_000d;
	}

	@Override
	public boolean isDead() {
		return toLive < 0;
	}

	@Override
	public boolean onEntityCollide(Entity other) {
		return true;
	}

	public double getRemaining() {
		return toLive;
	}
	
	public boolean canCollideWithWall() {
		return false;
	}

	@Override
	public double getSize() {
		return Math.sqrt(0.5);
	}
	
	@Override
	public Gravity gravityType() {
		return Gravity.NONE;
	}
	
}
