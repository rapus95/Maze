package maze;

import static math.vecmat.Mat.Mat3;
import static math.vecmat.Vec.Vec3;
import static math.vecmat.Vec.mixFromHighestComponents;

import java.util.UUID;

import math.collision.Physics;
import math.vecmat.Mat;
import math.vecmat.Mat3;
import math.vecmat.Vec3;

public abstract class Entity {

	private static final double MAX_DYNAMIC_CHANGE = 89;

	private static final Vec3 FORWARD = Vec3(1, 0, 0);
	private static final Vec3 UP = Vec3(0, 1, 0);
	private static final Vec3 DOWN = Vec3(0, -1, 0);

	protected Maze m;

	protected Vec3 pos;
	protected Mat3 rotation = Mat3();
	protected Vec3 speed = Vec3(0, 0, 0);

	public UUID id;

	public Entity(Maze m, Vec3 pos) {
		this.m = m;
		this.pos = pos;
		if (this.pos == null)
			this.pos = Vec3();
	}

	public Vec3 getPos() {
		return pos;
	}

	public Vec3 getViewDirection() {
		return rotation.mul(FORWARD);
	}

	public void setViewDirection(Vec3 forward) {
		forward = forward.normalize();
		Vec3 axis = getUp();
		Vec3 oldForward = rotation.mul(FORWARD);
		Vec3 newForawrd = axis.cross(forward).cross(axis);
		axis = newForawrd.cross(oldForward);
		double rot = Math.toDegrees(Math.acos(newForawrd.dot(oldForward)));
		if (rot != 0 && !Double.isNaN(rot)) {
			rotation = rotation.mul(Mat.createRotationMarix3(rot, axis.x(), axis.y(), axis.z()));
		}
	}

	public void tick(long timeDelta) {
		if (gravityType() != Gravity.NONE) {
			this.speed.y(this.speed.y() - timeDelta / 1000_000_000d - 0.1);
		}
		pos = pos.addScaled(rotation.<Vec3> mul(speed), timeDelta / 1000_000_000d);
		if (gravityType() != Gravity.NONE) {
			this.speed.y(this.speed.y() + 0.1);
		}
	}

	public void moveOutOfWall(double dt) {
		if (!canCollideWithWall())
			return;

		double size = getSize();
		double x = pos.get(0);
		double y = pos.get(1);
		double z = pos.get(2);
		int ix = (int) Math.floor(x + 0.5);
		int iy = (int) Math.floor(y + 0.5);
		int iz = (int) Math.floor(z + 0.5);
		if (pos.withinRectangle(Vec3(ix - 0.5 + size, iy - 0.5 + size, iz - 0.5 + size), Vec3(ix + 0.5 - size, iy + 0.5 - size, iz + 0.5 - size))
				&& !m.isWall(pos))
			return;
		Vec3 rOuter[] = new Vec3[27], tmp;
		Vec3 up = Vec3();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (m.isWall(tmp = pos.add(Vec3(i, j, k)))) {
						tmp = tmp.round();
						Vec3 xnynzn = tmp.add(Vec3(-0.5, -0.5, -0.5));
						Vec3 xpynzn = tmp.add(Vec3(0.5, -0.5, -0.5));
						Vec3 xnypzn = tmp.add(Vec3(-0.5, 0.5, -0.5));
						Vec3 xpypzn = tmp.add(Vec3(0.5, 0.5, -0.5));
						Vec3 xnynzp = tmp.add(Vec3(-0.5, -0.5, 0.5));
						Vec3 xpynzp = tmp.add(Vec3(0.5, -0.5, 0.5));
						Vec3 xnypzp = tmp.add(Vec3(-0.5, 0.5, 0.5));
						Vec3 xpypzp = tmp.add(Vec3(0.5, 0.5, 0.5));
						// System.out.println("Test at:" + tmp + "@" + pos);
						Vec3 rInner[] = { shortestDistanceToRectangle(xnynzn, xnypzn, xpypzn, xpynzn),
								shortestDistanceToRectangle(xnynzp, xpynzp, xpypzp, xnypzp), shortestDistanceToRectangle(xnynzn, xnynzp, xnypzp, xnypzn),
								shortestDistanceToRectangle(xpynzn, xpypzn, xpypzp, xpynzp), shortestDistanceToRectangle(xnynzn, xpynzn, xpynzp, xnynzp),
								shortestDistanceToRectangle(xnypzn, xnypzp, xpypzp, xpypzn) };
						Vec3 nInner[] = { getNormalOnCollide(xnynzn, xnypzn, xpypzn, xpynzn), getNormalOnCollide(xnynzp, xpynzp, xpypzp, xnypzp),
								getNormalOnCollide(xnynzn, xnynzp, xnypzp, xnypzn), getNormalOnCollide(xpynzn, xpypzn, xpypzp, xpynzp),
								getNormalOnCollide(xnynzn, xpynzn, xpynzp, xnynzp), getNormalOnCollide(xnypzn, xnypzp, xpypzp, xpypzn) };
						up = up.add(Vec3.sumUp(3, nInner));
						rOuter[3 * (3 * (i + 1) + (j + 1)) + (k + 1)] = mixFromHighestComponents(3, rInner);

					}
				}
			}
		}
		Vec3 out = mixFromHighestComponents(3, rOuter);
		if (!up.equals(Vec3(0, 0, 0))) {
			this.speed.y(0);
			if (gravityType() == Gravity.DYNAMIC) {
				Vec3 newDown = up.neg().normalize();
				Vec3 down = rotation.mul(DOWN);
				Vec3 axis = newDown.cross(down);
				double rot = Math.toDegrees(Math.acos(newDown.dot(down)));
				if (rot != 0 && !Double.isNaN(rot) && Math.abs(rot) < MAX_DYNAMIC_CHANGE) {
					double maxrot = 360 * dt;
					if (rot > maxrot)
						rot = maxrot;
					rotation = rotation.mul(Mat.createRotationMarix3(rot, axis.x(), axis.y(), axis.z()));
				}
			}
		}
		this.pos = pos.sub(out);
	}

	private Vec3 shortestDistanceToRectangle(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4) {
		double size = getSize();
		Vec3 pos = getPos();
		Vec3 v = Physics.intersectRect(p1, p2, p3, pos, size);
		if (v != null/* && (tmp=v.distanceTo(pos)) < size */) {
			v = v.sub(pos);
			return v.normalizeToLength(size).sub(v);
		}
		return null;
	}

	private Vec3 getNormalOnCollide(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4) {
		double size = getSize();
		Vec3 pos = getPos();
		Vec3 v = Physics.intersectRect(p1, p2, p3, pos, size);
		if (v != null) {
			return Physics.getNormal(p1, p2, p3);
		}
		return null;
	}

	public void setForwardSpeed(double speed) {
		this.speed.x(speed);
	}

	public void setSidewardSpeed(double speed) {
		this.speed.z(speed);
	}

	public void setUpSpeed(double speed) {
		this.speed.y(speed);
	}

	public Maze getMaze() {
		return m;
	}

	public boolean isStatic(Entity other) {
		return false;
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

	public final void onEntityCollideWith(Entity other) {
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
			toMove.pos = toMove.getPos().sub(stat.getPos()).normalizeRandomIfPoint().mul(shouldDist).add(stat.getPos());
		} else {
			Vec3 mid = getPos().add(other.getPos()).div(2);
			this.pos = this.getPos().sub(mid).normalizeRandomIfPoint().mul(shouldDist / 2).add(mid);
			other.pos = other.getPos().sub(mid).normalizeRandomIfPoint().mul(shouldDist / 2).add(mid);
		}
		this.speed.y(0);
	}

	public boolean isDead() {
		return false;
	}

	public Gravity gravityType() {
		return Gravity.STATIC;
	}

	// public void fall(long dTime) {
	// if (gravityType() != Gravity.NONE)
	// pos = pos.addScaled(getUp(), -0.981 * dTime / 1000_000_000d);
	// }

	public Vec3 getUp() {
		return rotation.<Vec3> mul(UP);
	}

	public void rotate(double d) {
		Vec3 up = rotation.mul(UP);
		rotation = rotation.rotate(d, up);
	}

	public Mat3 getRotation() {
		return rotation;
	}

}
