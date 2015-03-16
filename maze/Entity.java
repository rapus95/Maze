package maze;

import java.util.UUID;

import math.matrix.Mat;
import math.matrix.Vec;

public abstract class Entity {

	protected Maze m;

	protected Vec pos;
	protected Vec viewDirection = new Vec(0.0, 0.0).normalize();
	protected float speedForward = 0, speedSideward = 0;

	private static final Mat rot = Mat.makeRotMat_Given(2, 90, 0, 1);
	private static final Mat dimChanger = new Mat(2, 3);
	
	static{
		dimChanger.setIdentity(0);
		dimChanger.setValue(0, 0, 1);
		dimChanger.setValue(1, 2, 1);
	}

	private boolean isStatic;

	public UUID id;

	public Entity(Maze m, Vec pos) {
		this.m = m;
		this.pos = pos;
		if (this.pos == null)
			this.pos = new Vec(2);
	}

	public Vec getPos() {
		return pos;
	}

	public Vec getViewDirection() {
		return viewDirection;
	}

	public void tick(long timeDelta) {
		Vec viewDirection = dimChanger.mul(this.viewDirection = this.viewDirection.normalize()).normalize();
		
		pos = pos.addWithMultiplier(viewDirection, speedForward * timeDelta / 1000d / 1000 / 1000).addWithMultiplier(rot.mul(viewDirection), speedSideward * timeDelta / 1000d / 1000 / 1000);
	}

	public void moveOutOfWall() {
		if (!canCollideWithWall())
			return;
		double size = getSize();
		double x = pos.getComponent(0);
		double y = pos.getComponent(1);
		int ix = (int) Math.floor(x + 0.5);
		int iy = (int) Math.floor(y + 0.5);
		double px = ix - x;
		double py = iy - y;
		// System.out.println("px:" + px + ", py:" + py + ", x:" + x + ", y:" +
		// y + ", ix:" + ix + ", iy:" + iy);
		if (m.isWall(new Vec((double) ix, iy))) {
			if (Math.abs(px) < Math.abs(py)) {
				if (py > 0) {
					y = iy - 0.5 - size;
				} else {
					y = iy + 0.5 + size;
				}
			} else {
				if (px > 0) {
					x = ix - 0.5 - size;
				} else {
					x = ix + 0.5 + size;
				}
			}
			pos.setComponent(0, x);
			pos.setComponent(1, y);
		} else {
			double l;
			if (px < -0.5 + size && m.isWall(new Vec((double) ix + 1, iy))) {
				pos.setComponent(0, ix + 0.5 - size);
				if (py < -0.5 + size && m.isWall(new Vec((double) ix, iy + 1))) {
					pos.setComponent(1, iy + 0.5 - size);
				} else if (py > 0.5 - size && m.isWall(new Vec((double) ix, iy - 1))) {
					pos.setComponent(1, iy - 0.5 + size);
				}
			} else if (px > 0.5 - size && m.isWall(new Vec((double) ix - 1, iy))) {
				pos.setComponent(0, ix - 0.5 + size);
				if (py < -0.5 + size && m.isWall(new Vec((double) ix, iy + 1))) {
					pos.setComponent(1, iy + 0.5 - size);
				} else if (py > 0.5 - size && m.isWall(new Vec((double) ix, iy - 1))) {
					pos.setComponent(1, iy - 0.5 + size);
				}
			} else if (py < -0.5 + size && m.isWall(new Vec((double) ix, iy + 1))) {
				pos.setComponent(1, iy + 0.5 - size);
			} else if (py > 0.5 - size && m.isWall(new Vec((double) ix, iy - 1))) {
				pos.setComponent(1, iy - 0.5 + size);
			} else if ((l = l(px, py, -0.5, -0.5)) < size && m.isWall(new Vec((double) ix + 1, iy + 1))) {
				pos.setComponent(0, ix + 0.5 - (px + 0.5) / l * size);
				pos.setComponent(1, iy + 0.5 - (py + 0.5) / l * size);
			} else if ((l = l(px, py, 0.5, -0.5)) < size && m.isWall(new Vec((double) ix - 1, iy + 1))) {
				pos.setComponent(0, ix - 0.5 - (px - 0.5) / l * size);
				pos.setComponent(1, iy + 0.5 - (py + 0.5) / l * size);
			} else if ((l = l(px, py, 0.5, 0.5)) < size && m.isWall(new Vec((double) ix - 1, iy - 1))) {
				pos.setComponent(0, ix - 0.5 - (px - 0.5) / l * size);
				pos.setComponent(1, iy - 0.5 - (py - 0.5) / l * size);
			} else if ((l = l(px, py, -0.5, 0.5)) < size && m.isWall(new Vec((double) ix + 1, iy - 1))) {
				pos.setComponent(0, ix + 0.5 - (px + 0.5) / l * size);
				pos.setComponent(1, iy - 0.5 - (py - 0.5) / l * size);
			}
		}
	}

	private static double l(double x1, double y1, double x2, double y2) {
		x1 -= x2;
		y1 -= y2;
		return Math.sqrt(x1 * x1 + y1 * y1);
	}
	public void setForwardSpeed(float speed) {
		this.speedForward = speed;
	}

	public void setSidewardSpeed(float speed) {
		this.speedSideward = speed;
	}

	public Maze getMaze() {
		return m;
	}

	public boolean isStatic(Entity other) {
		return isStatic;
	}

	public double getSize() {
		return 0.25;
	}

	public boolean onEntityCollide(Entity other) {
		return false;
	}

	public boolean canCollideWithWall() {
		return true;
	}

	public void onEntityCollideWith(Entity other) {
		if (onEntityCollide(other) | other.onEntityCollide(this))
			return;
		boolean thisStatic = isStatic(other);
		boolean otherStatic = other.isStatic(this);
		if (thisStatic && otherStatic)
			return;
		double shouldDist = getSize() + other.getSize();
		if (thisStatic || otherStatic) {
			Entity toMove = thisStatic ? other : this;
			Entity stat = thisStatic ? this : other;
			toMove.pos = toMove.getPos().sub(stat.getPos()).randomIfPoint().normalize().mul(shouldDist).add(stat.getPos());
		} else {
			Vec mid = getPos().add(other.getPos()).div(2);
			this.pos = this.getPos().sub(mid).randomIfPoint().normalize().mul(shouldDist / 2).add(mid);
			other.pos = other.getPos().sub(mid).randomIfPoint().normalize().mul(shouldDist / 2).add(mid);
		}
	}

	public boolean isDead() {
		return false;
	}

}
