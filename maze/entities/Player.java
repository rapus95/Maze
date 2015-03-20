package maze.entities;

import math.matrix.Vec;
import maze.Entity;
import maze.Gravity;
import maze.Maze;
import maze.effects.Explosion;

public class Player extends Entity {

	private boolean canPlaceBomb = true;

	private boolean canKickBomb = true;

	private int currBombAmount;

	private int bombAmount = 1;

	private int bombPower = 1;

	private Vec startPos;

	private long dieAni = -1;

	public Player(Maze m, Vec pos) {
		super(m, pos);
		this.startPos = pos;
	}

	public void placeBomb() {
		if (!canPlaceBomb)
			return;
		if (currBombAmount >= bombAmount)
			return;
		m.spawnEntity(new Bomb(m, pos, this, bombPower, 4));
		currBombAmount++;
	}

	public void onPlacedBombExploded(Bomb bomb) {
		currBombAmount--;
	}

	public boolean canKickBomb() {
		return canKickBomb;
	}

	@Override
	public boolean onEntityCollide(Entity other) {
		if (dieAni != -1)
			return true;
		if (other instanceof Explosion)
			die();
		if(other instanceof Bomb){
			other.setViewDirection(this.viewDirection);
			other.setForwardSpeed(Math.max(1, this.speedForward));
		}
		return super.onEntityCollide(other);
	}

	private void die() {
		this.pos = this.startPos;
	}

	@Override
	public boolean isStatic(Entity other) {
		if (other instanceof Bomb) {
			if (other.getPos().distanceToSmaller(this.getPos(), (other.getSize() + this.getSize()) * 0.90))
				return true;
			return false;
		}
		return super.isStatic(other);
	}

	@Override
	public void tick(long timeDelta) {
		super.tick(timeDelta);
		if (dieAni != -1) {
			dieAni += timeDelta;
			if (dieAni >= 2000000000) {
				respawn();
			}
		}
	}

	public void respawn() {
		dieAni = -1;
		this.pos = this.startPos;
	}

	public long getDieAni() {
		return dieAni/2;
	}
	
	@Override
	public Gravity gravityType() {
		return Gravity.STATIC;
	}

}
