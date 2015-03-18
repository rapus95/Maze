package maze;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

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

	private boolean isStatic;

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
		pos = pos.addWithMultiplier(rot.mul(viewDirection), speedForward * timeDelta / 1000_000_000d).addWithMultiplier(viewDirection,
				speedSideward * timeDelta / 1000_000_000d);
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
		if (pos.withinRectangle(Vec.fromList(ix - 0.5 + size, iy - 0.5 + size, iz - 0.5 + size),
				Vec.fromList(ix + 0.5 - size, iy + 0.5 - size, iz + 0.5 - size))
				&& !m.isWall(pos))
			return;
		Vec v = new Vec(3), tmp;
		int num = 0;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// for (int k = -1; k <= 1; k++) {
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
					tmp = shortestDistanceToRectangle(xnynzn, xnypzn, xpypzn, xpynzn);
					if (tmp != null) {
						v = v.add(tmp);
						num++;
					}
					tmp = shortestDistanceToRectangle(xnynzp, xpynzp, xpypzp, xnypzp);
					if (tmp != null) {
						v = v.add(tmp);
						num++;
					}
					tmp = shortestDistanceToRectangle(xnynzn, xnynzp, xnypzp, xnypzn);
					if (tmp != null) {
						v = v.add(tmp);
						num++;
					}
					tmp = shortestDistanceToRectangle(xpynzn, xpypzn, xpypzp, xpynzp);
					if (tmp != null) {
						v = v.add(tmp);
						num++;
					}
					tmp = shortestDistanceToRectangle(xnynzn, xpynzn, xpynzp, xnynzp);
					if (tmp != null) {
						v = v.add(tmp);
						num++;
					}
					tmp = shortestDistanceToRectangle(xnypzn, xnypzp, xpypzp, xpypzn);
					if (tmp != null) {
						v = v.add(tmp);
						num++;
					}
				}
				// }
			}
		}
		// System.out.println(v);
		if (num > 0) {
			this.pos = pos.sub(v.div(num));
		}
	}

	private Vec shortestDistanceToRectangle(Vec p1, Vec p2, Vec p3, Vec p4) {
		Vec n = p2.sub(p1).cross(p3.sub(p1)).normalize();
		// Vec center = p1.add(p2).add(p3).div(3);
		// GL11.glColor3d(0, 0, 0);
		// GL11.glBegin(GL11.GL_LINES);
		// GL11.glVertex3(center.asTmpDoubleBuffer(true));
		// GL11.glVertex3(center.add(n).asTmpDoubleBuffer(true));
		// GL11.glEnd();
		double size = getSize();
		Vec pos = getPos();
		// Vec v = Physics.intersect2(p1, p2, p3, pos, size, size/1.6);
		// Vec v2 = Physics.intersect2(p1, p3, p4, pos, size, size/1.6);
		// double d1 = size, d2;
		// if (v != null) {
		// d2 = v.distanceTo(pos);
		// System.out.println("Dist1:" + d1);
		// if (d1 > d2) {
		// d1 = d2;
		// }
		// }
		// if (v2 != null) {
		// d2 = v2.distanceTo(pos);
		// System.out.println("Dist2:" + d1);
		// if (d1 > d2) {
		// d1 = d2;
		// }
		// }
		// if (d1 < size) {
		// System.out.println("Dist3:" + d1);
		// return n.mul(-(size - d1));
		// }
		// System.out.println("None");
		Vec v = Physics.intersectRect(p1, p2, p3, pos, size, 0/* size/1.6 */);
		//double tmp;
		if (v != null/* && (tmp=v.distanceTo(pos)) < size */) {
			v = v.sub(pos);
			return v.normalizeToLength(size).sub(v);
			// return n.mul(-(size - tmp));
		}
		return null;

		// Vec v3 = v == null ? new Vec(3) :
		// v.sub(pos).normalizeToLength(size).sub(v.sub(pos));
		// if (v2 != null)
		// v3 = v3.add(v2.sub(pos).normalizeToLength(size).sub(v2.sub(pos)));
		// if (v != null || v2 != null)
		// System.out.println("Intersection@" + v + ":" + v2 + "," + p1 +
		// "," +
		// p2 + "," + p3 + "," + p4 + ":" + pos + ":" + v3);
		// return v3;
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
