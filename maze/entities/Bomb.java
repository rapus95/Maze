package maze.entities;

import math.vecmat.Vec;
import math.vecmat.Vec3;
import maze.Entity;
import maze.Maze;
import maze.effects.Explosion;

public class Bomb extends Entity {

	private Player player;

	private double power;

	private double time;

	private boolean exploded;

	public Bomb(Maze m, Vec3 pos, Player player, double power, double time) {
		super(m, pos);
		this.player = player;
		this.power = power;
		this.time = time;
	}

	@Override
	public void tick(long timeDelta) {
		if (exploded)
			return;
		super.tick(timeDelta);
		time -= timeDelta / 1000d / 1000d / 1000d;
		if (time < 0) {
			explode();
		}
	}

	public void explode() {
		if (exploded)
			return;
		if (player != null)
			player.onPlacedBombExploded(this);
		double p = m.get(this.pos).onExplode(this, this.pos, power);
		if(p<=0){
			exploded = true;
			return;
		}
		m.spawnEntity(new Explosion(m, this.pos));
		for (int i = 0; i < pos.dim(); i++) {
			double remaining = p;
			Vec3 off = Vec.Vec3();
			off.set(i, 1);
			Vec3 pos = this.pos;
			while(remaining-->0){
				pos = pos.add(off);
				if((remaining = m.get(pos).onExplode(this, pos, remaining))<=0){
					break;
				}
				m.spawnEntity(new Explosion(m, pos));
			}
			remaining = p;
			off.set(i, -1);
			pos = this.pos;
			while(remaining-->0){
				pos = pos.add(off);
				if((remaining = m.get(pos).onExplode(this, pos, remaining))<=0){
					break;
				}
				m.spawnEntity(new Explosion(m, pos));
			}
		}
		exploded = true;
	}
	
	@Override
	public boolean isDead() {
		return exploded;
	}

	@Override
	public boolean onEntityCollide(Entity other) {
		if(other instanceof Explosion){
			explode();
		}
//		}else if(other instanceof Bomb)
//			return true;
		return super.onEntityCollide(other);
	}

	@Override
	public boolean isStatic(Entity other) {
		if(other instanceof Player){
			if (other.getPos().distanceSmaller(this.getPos(), (other.getSize() + this.getSize()) * 0.90))
				return true;
			return !((Player)other).canKickBomb();
		}
		return super.isStatic(other);
	}

	@Override
	public double getSize() {
		return 0.15;
	}
}
