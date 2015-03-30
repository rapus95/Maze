package maze;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import math.collision.Physics;
import math.vecmat.Mat;
import math.vecmat.Mat3;
import math.vecmat.PolarVec;
import math.vecmat.Vec3;
import net.Utils;
import static math.vecmat.Vec.*;

public abstract class Entity {

	protected Maze m;

	protected Vec3 pos, up = Vec3(0,1,0);
	protected PolarVec viewDirection = PolarVec.fromList(1, Math.PI / 2, 0);
	protected float speedForward = 0, speedSideward = 0;

	private static final Mat3 rot = Mat.createRotationMarix3(-90, 0, 1, 0);

	public UUID id;

	public Entity(Maze m, Vec3 pos) {
		this.m = m;
		this.pos = pos;
		if (this.pos == null)
			this.pos = Vec3();
	}

	public Entity(Maze m, DataInputStream dataInputStream) throws IOException {
		this.m = m;
		this.pos = Vec3();
		readSync(dataInputStream);
	}

	public Vec3 getPos() {
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
		Vec3 viewDirection = this.viewDirection.<Vec3>toVec();
		System.out.println(viewDirection);
//		viewDirection.set(2, 0);
		viewDirection.z(viewDirection.y());
		viewDirection.set(1, 0);
		// System.out.println(viewDirection);
		pos = pos.addScaled(rot.<Vec3>mul(viewDirection), speedForward * timeDelta / 1000_000_000d).addScaled(viewDirection, speedSideward * timeDelta / 1000_000_000d);
	}

	public void moveOutOfWall() {
		if (!canCollideWithWall())
			return;

		double size = getSize();
		double x = pos.get(0);
		double y = pos.get(1);
		double z = pos.get(2);
		int ix = (int) Math.floor(x + 0.5);
		int iy = (int) Math.floor(y + 0.5);
		int iz = (int) Math.floor(z + 0.5);
		if (pos.withinRectangle(Vec3(ix - 0.5 + size, iy - 0.5 + size, iz - 0.5 + size), Vec3(ix + 0.5 - size, iy + 0.5 - size, iz + 0.5 - size)) && !m.isWall(pos))
			return;
		Vec3 rOuter[] = new Vec3[27], tmp;
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
						Vec3 rInner[] = {
								shortestDistanceToRectangle(xnynzn, xnypzn, xpypzn, xpynzn),
								shortestDistanceToRectangle(xnynzp, xpynzp, xpypzp, xnypzp),
								shortestDistanceToRectangle(xnynzn, xnynzp, xnypzp, xnypzn),
								shortestDistanceToRectangle(xpynzn, xpypzn, xpypzp, xpynzp),
								shortestDistanceToRectangle(xnynzn, xpynzn, xpynzp, xnynzp),
								shortestDistanceToRectangle(xnypzn, xnypzp, xpypzp, xpypzn)};
						rOuter[3*(3*(i+1)+(j+1))+(k+1)] = mixFromHighestComponents(3, rInner);

					}
				}
			}
		}
		this.pos = pos.sub(mixFromHighestComponents(3, rOuter));
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
			Vec3 mid = getPos().add(other.getPos()).div(2);
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

	public Gravity gravityType() {
		return Gravity.STATIC;
	}

	public void readSync(DataInputStream dataInputStream) throws IOException {
		Utils.readVec(dataInputStream, this.pos);
		Utils.readVec(dataInputStream, this.viewDirection);
		Utils.readVec(dataInputStream, this.up);
		this.speedForward = dataInputStream.readFloat();
		this.speedSideward = dataInputStream.readFloat();
	}

	private static final Vec3 GLOBAL_DOWN = Vec3(0, -1, 0);
	public void fall(long dTime) {
		up = up.normalize();
		switch (gravityType()) {
			case DYNAMIC :
			case NONE :
				return;
			case STATIC :
				pos = pos.addScaled(GLOBAL_DOWN, 0.981 * dTime / 1000_000_000d);
				return;
			default :
				break;
		}
	}

}
