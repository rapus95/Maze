package maze;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import math.matrix.Mat;
import math.matrix.PolarVec;
import math.matrix.Vec;
import net.Utils;

public abstract class Entity {

	protected Maze m;

	protected Vec pos, up = Vec.fromList(0, 0, -1);
	protected PolarVec viewDirection = PolarVec.fromList(1, Math.PI / 2, 0);
	protected float speedForward = 0, speedSideward = 0;

	private static final Mat rot = Mat.makeRotMat_Given(3, -90, 0, 1);

	public UUID id;

	public Entity(Maze m, Vec pos) {
		this.m = m;
		this.pos = pos;
		if (this.pos == null)
			this.pos = new Vec(3);
	}

	public Entity(Maze m, DataInputStream dataInputStream) throws IOException {
		this.m = m;
		this.pos = new Vec(3);
		readSync(dataInputStream);
	}

	public Vec getPos() {
		return pos;
	}

	public PolarVec getViewDirection() {
		return viewDirection;
	}

	public void setViewDirection(PolarVec v) {
		this.viewDirection = v.clone();
	}

	public void tick(long timeDelta) {
		// System.out.println(this.viewDirection);
		// System.out.println(this.viewDirection.toVec());
		// System.out.println(dimChanger.mul(this.viewDirection.toVec()));
		Vec viewDirection = this.viewDirection.toVec();
		viewDirection.setComponent(2, 0);
		// System.out.println(viewDirection);
		pos = pos.addWithMultiplier(rot.mul(viewDirection), speedForward * timeDelta / 1000_000_000d).addWithMultiplier(viewDirection, speedSideward * timeDelta / 1000_000_000d);
	}

	// public void moveOutOfWall() {
	// if (!canCollideWithWall())
	// return;
	//
	// double size = getSize();
	// double x = pos.getComponent(0);
	// double y = pos.getComponent(1);
	// double z = pos.getComponent(2);
	// int ix = (int) Math.floor(x + 0.5);
	// int iy = (int) Math.floor(y + 0.5);
	// int iz = (int) Math.floor(z + 0.5);
	// double px = ix - x;
	// double py = iy - y;
	// double pz = iz - z;
	// if (pos.withinRectangle(Vec.fromList(ix - 0.5 + size, iy - 0.5 + size, iz
	// - 0.5 + size), Vec.fromList(ix + 0.5 - size, iy + 0.5 - size, iz + 0.5 -
	// size)) && !m.isWall(pos))
	// return;
	// // System.out.println("px:" + px + ", py:" + py + ", x:" + x + ", y:" +
	// // y + ", ix:" + ix + ", iy:" + iy);
	// if (m.isWall(Vec.fromList(ix, iy, iz))) {
	// if (Math.abs(px) < Math.abs(py) && Math.abs(pz) < Math.abs(py)) {
	// if (py > 0) {
	// y = iy - 0.5 - size;
	// } else {
	// y = iy + 0.5 + size;
	// }
	// pos.setComponent(1, y);
	// } else if (Math.abs(pz) < Math.abs(px)) {
	// if (px > 0) {
	// x = ix - 0.5 - size;
	// } else {
	// x = ix + 0.5 + size;
	// }
	// pos.setComponent(0, x);
	// } else {
	// if (pz > 0) {
	// z = iz - 0.5 - size;
	// } else {
	// z = iz + 0.5 + size;
	// }
	// pos.setComponent(2, z);
	// }
	// } else {
	// double l;
	// if (px < -0.5 + size && m.isWall(Vec.fromList(ix + 1, iy, iz))) {
	// pos.setComponent(0, ix + 0.5 - size);
	// if (py < -0.5 + size && m.isWall(Vec.fromList(ix, iy + 1, iz))) {
	// pos.setComponent(1, iy + 0.5 - size);
	// } else if (py > 0.5 - size && m.isWall(Vec.fromList(ix, iy - 1, iz))) {
	// pos.setComponent(1, iy - 0.5 + size);
	// }
	// } else if (px > 0.5 - size && m.isWall(Vec.fromList(ix - 1, iy, iz))) {
	// pos.setComponent(0, ix - 0.5 + size);
	// if (py < -0.5 + size && m.isWall(Vec.fromList(ix, iy + 1, iz))) {
	// pos.setComponent(1, iy + 0.5 - size);
	// } else if (py > 0.5 - size && m.isWall(Vec.fromList(ix, iy - 1, iz))) {
	// pos.setComponent(1, iy - 0.5 + size);
	// }
	// } else if (py < -0.5 + size && m.isWall(Vec.fromList(ix, iy + 1, iz))) {
	// pos.setComponent(1, iy + 0.5 - size);
	// } else if (py > 0.5 - size && m.isWall(Vec.fromList(ix, iy - 1, iz))) {
	// pos.setComponent(1, iy - 0.5 + size);
	// } else if ((l = l(px, py, -0.5, -0.5)) < size && m.isWall(Vec.fromList(ix
	// + 1, iy + 1, iz))) {
	// pos.setComponent(0, ix + 0.5 - (px + 0.5) / l * size);
	// pos.setComponent(1, iy + 0.5 - (py + 0.5) / l * size);
	// } else if ((l = l(px, py, 0.5, -0.5)) < size && m.isWall(Vec.fromList(ix
	// - 1, iy + 1, iz))) {
	// pos.setComponent(0, ix - 0.5 - (px - 0.5) / l * size);
	// pos.setComponent(1, iy + 0.5 - (py + 0.5) / l * size);
	// } else if ((l = l(px, py, 0.5, 0.5)) < size && m.isWall(Vec.fromList(ix -
	// 1, iy - 1, iz))) {
	// pos.setComponent(0, ix - 0.5 - (px - 0.5) / l * size);
	// pos.setComponent(1, iy - 0.5 - (py - 0.5) / l * size);
	// } else if ((l = l(px, py, -0.5, 0.5)) < size && m.isWall(Vec.fromList(ix
	// + 1, iy - 1, iz))) {
	// pos.setComponent(0, ix + 0.5 - (px + 0.5) / l * size);
	// pos.setComponent(1, iy - 0.5 - (py - 0.5) / l * size);
	// }
	// }
	// }

	public void moveOutOfWall() {
		if (!canCollideWithWall())
			return;

		double size = getSize();
		double x = pos.getComponent(0);
		double y = pos.getComponent(1);
		double z = pos.getComponent(2);
		int ix = (int) Math.floor(x + 0.5);
		int iy = (int) Math.floor(y + 0.5);
		int iz = (int) Math.floor(z + 0.5);
		if (pos.withinRectangle(Vec.fromList(ix - 0.5 + size, iy - 0.5 + size, iz - 0.5 + size), Vec.fromList(ix + 0.5 - size, iy + 0.5 - size, iz + 0.5 - size)) && !m.isWall(pos))
			return;
		Vec rOuter[] = new Vec[27], tmp;
		int num = 0;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (m.isWall(tmp = pos.add(Vec.fromList(i, j, 0)))) {
						tmp = tmp.round();
						Vec xnynzn = tmp.add(Vec.fromList(-0.5, -0.5, -0.5));
						Vec xpynzn = tmp.add(Vec.fromList(0.5, -0.5, -0.5));
						Vec xnypzn = tmp.add(Vec.fromList(-0.5, 0.5, -0.5));
						Vec xpypzn = tmp.add(Vec.fromList(0.5, 0.5, -0.5));
						Vec xnynzp = tmp.add(Vec.fromList(-0.5, -0.5, 0.5));
						Vec xpynzp = tmp.add(Vec.fromList(0.5, -0.5, 0.5));
						Vec xnypzp = tmp.add(Vec.fromList(-0.5, 0.5, 0.5));
						Vec xpypzp = tmp.add(Vec.fromList(0.5, 0.5, 0.5));
						// System.out.println("Test at:" + tmp + "@" + pos);
						Vec rInner[] = {
								shortestDistanceToRectangle(xnynzn, xnypzn, xpypzn, xpynzn),
								shortestDistanceToRectangle(xnynzp, xpynzp, xpypzp, xnypzp),
								shortestDistanceToRectangle(xnynzn, xnynzp, xnypzp, xnypzn),
								shortestDistanceToRectangle(xpynzn, xpypzn, xpypzp, xpynzp),
								shortestDistanceToRectangle(xnynzn, xpynzn, xpynzp, xnynzp),
								shortestDistanceToRectangle(xnypzn, xnypzp, xpypzp, xpypzn)};
						rOuter[3*(3*(i+1)+(j+1))+(k+1)] = Vec.mixFromHighestComponents(3, rInner);

					}
				}
			}
		}
		// System.out.println(v);
		this.pos = pos.sub(Vec.mixFromHighestComponents(3, rOuter));
	}
	private Vec shortestDistanceToRectangle(Vec p1, Vec p2, Vec p3, Vec p4) {
		double size = getSize();
		Vec pos = getPos();
		Vec v = Physics.intersectRect(p1, p2, p3, pos, size);
		if (v != null/* && (tmp=v.distanceTo(pos)) < size */) {
			v = v.sub(pos);
			return v.normalizeToLength(size).sub(v);
			// return n.mul(-(size - tmp));
		}
		return null;
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
			Vec mid = getPos().add(other.getPos()).div(2);
			this.pos = this.getPos().sub(mid).normalizeRandomIfPoint().mul(shouldDist / 2).add(mid);
			other.pos = other.getPos().sub(mid).normalizeRandomIfPoint().mul(shouldDist / 2).add(mid);
		}
	}

	public boolean isDead() {
		return false;
	}

	public void writeSync(DataOutputStream dataOutputStream) throws IOException {
		Utils.writeVec(dataOutputStream, this.pos);
		Utils.writeVec(dataOutputStream, this.viewDirection);
		Utils.writeVec(dataOutputStream, this.up);
		dataOutputStream.writeFloat(this.speedForward);
		dataOutputStream.writeFloat(this.speedSideward);

	}

	public void readSync(DataInputStream dataInputStream) throws IOException {
		Utils.readVec(dataInputStream, this.pos);
		Utils.readVec(dataInputStream, this.viewDirection);
		Utils.readVec(dataInputStream, this.up);
		this.speedForward = dataInputStream.readFloat();
		this.speedSideward = dataInputStream.readFloat();
	}

	// private static final Mat DOWN = new Mat(3, 3);
	// static{
	// DOWN.setIdentity(-1);
	// }
	public void fall() {
		// pos = pos.add(up.normalizeToLength(0.002));
	}

}
