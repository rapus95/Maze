package maze;

import math.matrix.Vec;

public class Physics {

	// public static Vec intersect(Vec p1, Vec p2, Vec p3, Vec point, double
	// dist) {
	// double v1_x = p1.getComponent(0);
	// double v1_y = p1.getComponent(1);
	// double v1_z = p1.getComponent(2);
	// double v2_x = p2.getComponent(0);
	// double v2_y = p2.getComponent(1);
	// double v2_z = p2.getComponent(2);
	// double v3_x = p3.getComponent(0);
	// double v3_y = p3.getComponent(1);
	// double v3_z = p3.getComponent(2);
	// double a = v1_y * (v2_z - v3_z) + v2_y * (v3_z - v1_z) + v3_y * (v1_z -
	// v2_z);
	// double b = v1_z * (v2_x - v3_x) + v2_z * (v3_x - v1_x) + v3_z * (v1_x -
	// v2_x);
	// double c = v1_x * (v2_y - v3_y) + v2_x * (v3_y - v1_y) + v3_x * (v1_y -
	// v2_y);
	// double d = -(v1_x * (v2_y * v3_z - v3_y * v2_z) + v2_x * (v3_y * v1_z -
	// v1_y * v3_z) + v3_x * (v1_y * v2_z - v2_y * v1_z));
	// double planeDist = (a * point.getComponent(0) + b * point.getComponent(1)
	// + c * point.getComponent(2) + d) / Math.sqrt(a * a + b * b + c * c);
	// // System.out.println("Dist:"+planeDist);
	// if (Math.abs(planeDist) >= dist) {
	// return null;
	// }
	// Vec planePoint = point.sub(Vec.fromList(a, b, c).mul(planeDist));
	// if (planePoint == planePoint)
	// return planePoint;
	// Vec s = p2.sub(p1);
	// Vec t = p3.sub(p1);
	// double m1, m2;
	// planePoint = planePoint.sub(p1);
	// if (s.getComponent(0) == 0) {
	// if (s.getComponent(1) == 0) {
	// m1 = planePoint.getComponent(2) / s.getComponent(2);
	// planePoint = planePoint.sub(s.mul(m1));
	// if (t.getComponent(0) == 0) {
	// m2 = planePoint.getComponent(1) / t.getComponent(1);
	// planePoint = planePoint.sub(t.mul(m2));
	// } else {
	// m2 = planePoint.getComponent(0) / t.getComponent(0);
	// planePoint = planePoint.sub(t.mul(m2));
	// }
	// } else {
	// m1 = planePoint.getComponent(1) / s.getComponent(1);
	// planePoint = planePoint.sub(s.mul(m1));
	// if (t.getComponent(0) == 0) {
	// m2 = planePoint.getComponent(2) / t.getComponent(2);
	// planePoint = planePoint.sub(t.mul(m2));
	// } else {
	// m2 = planePoint.getComponent(0) / t.getComponent(0);
	// planePoint = planePoint.sub(t.mul(m2));
	// }
	// }
	// } else {
	// m1 = planePoint.getComponent(0) / s.getComponent(0);
	// planePoint = planePoint.sub(s.mul(m1));
	// if (t.getComponent(1) == 0) {
	// m2 = planePoint.getComponent(2) / t.getComponent(2);
	// planePoint = planePoint.sub(t.mul(m2));
	// } else {
	// m2 = planePoint.getComponent(1) / t.getComponent(1);
	// planePoint = planePoint.sub(t.mul(m2));
	// }
	// }
	// if (-1 < m1 + m2 && m1 + m2 < 1) {
	// System.out.println("m:" + m1 + ";" + m2);
	// return p1.add(s.mul(m1)).add(t.mul(m2));
	// }
	// return null;
	// }

	public static Vec intersect(Vec p1, Vec p2, Vec p3, Vec point, double dist, double border) {

		Vec o = p1;
		Vec s = p2.sub(o);
		Vec t = p3.sub(o);
		double dot = s.dot(t);
		if (dot != 0) {
			o = p3;
			s = p2.sub(o);
			t = p1.sub(o);
			dot = s.dot(t);
		}
		if (dot != 0) {
			o = p2;
			s = p3.sub(o);
			t = p1.sub(o);
			dot = s.dot(t);
		}
		if (dot != 0)
			System.out.println("Warning, not orthogonal axes, dot:" + dot);
		Vec n = s.cross(t);
		Vec p = point.sub(o);

		double d = n.dot(p);
		double pS = s.dot(p);
		double pT = t.dot(p);
		if (Math.abs(d) >= dist)
			return null;
		double borderS = border / s.pNorm(2), borderT = border / t.pNorm(2);
		if (pS > 0 - borderS && pT > 0 - borderT && pS + pT < 1)
			return o.addWithMultiplier(s, pS).addWithMultiplier(t, pT);
		return null;
	}

	public static Vec intersectRect(Vec p1, Vec p2, Vec p3, Vec point, double dist, double border) {

		Vec o = p1;
		Vec s = p2.sub(o);
		Vec t = p3.sub(o);
		double dot = s.dot(t);
		if (dot != 0) {
			o = p3;
			s = p2.sub(o);
			t = p1.sub(o);
			dot = s.dot(t);
		}
		if (dot != 0) {
			o = p2;
			s = p3.sub(o);
			t = p1.sub(o);
			dot = s.dot(t);
		}
		if (dot != 0)
			System.out.println("Warning, not orthogonal axes, dot:" + dot);
		Vec n = s.cross(t);
		Vec p = point.sub(o);

		double d = n.dot(p);
		double pS = s.dot(p);
		double pT = t.dot(p);
		if (Math.abs(d) >= dist)
			return null;
		double borderS = border / s.pNorm(2), borderT = border / t.pNorm(2);
		if (pS > 0 - borderS && pT > 0 - borderT && pS < 1 + borderS && +pT < 1 + borderT)
			return o.addWithMultiplier(s, pS).addWithMultiplier(t, pT);
		return null;
	}

}
