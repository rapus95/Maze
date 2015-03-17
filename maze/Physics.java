package maze;

import math.matrix.Vec;

public class Physics {

	public static Vec intersect(Vec p1, Vec p2, Vec p3, Vec point, double dist) {
		double v1_x = p1.getComponent(0);
		double v1_y = p1.getComponent(1);
		double v1_z = p1.getComponent(2);
		double v2_x = p2.getComponent(0);
		double v2_y = p2.getComponent(1);
		double v2_z = p2.getComponent(2);
		double v3_x = p3.getComponent(0);
		double v3_y = p3.getComponent(1);
		double v3_z = p3.getComponent(2);
		double a = v1_y * (v2_z - v3_z) + v2_y * (v3_z - v1_z) + v3_y * (v1_z - v2_z);
		double b = v1_z * (v2_x - v3_x) + v2_z * (v3_x - v1_x) + v3_z * (v1_x - v2_x);
		double c = v1_x * (v2_y - v3_y) + v2_x * (v3_y - v1_y) + v3_x * (v1_y - v2_y);
		double d = -(v1_x * (v2_y * v3_z - v3_y * v2_z) + v2_x * (v3_y * v1_z - v1_y * v3_z) + v3_x * (v1_y * v2_z - v2_y * v1_z));
		double planeDist = (a * point.getComponent(0) + b * point.getComponent(1) + c * point.getComponent(2) + d) / Math.sqrt(a * a + b * b + c * c);
		if (Math.abs(planeDist) >= dist) {
			return null;
		}
		Vec planePoint = point.sub(Vec.fromList(a, b, c).mul(planeDist));
		Vec s = p2.sub(p1);
		Vec t = p3.sub(p1);
		double m1, m2;
		planePoint = planePoint.sub(p1);
		if (s.getComponent(0) == 0) {
			if (s.getComponent(1) == 0) {
				m1 = planePoint.getComponent(2) / s.getComponent(2);
				planePoint = planePoint.sub(s.mul(m1));
				if (t.getComponent(0) == 0) {
					m2 = planePoint.getComponent(1) / t.getComponent(1);
					planePoint = planePoint.sub(t.mul(m2));
				} else {
					m2 = planePoint.getComponent(0) / t.getComponent(0);
					planePoint = planePoint.sub(t.mul(m2));
				}
			} else {
				m1 = planePoint.getComponent(1) / s.getComponent(1);
				planePoint = planePoint.sub(s.mul(m1));
				if (t.getComponent(0) == 0) {
					m2 = planePoint.getComponent(2) / t.getComponent(2);
					planePoint = planePoint.sub(t.mul(m2));
				} else {
					m2 = planePoint.getComponent(0) / t.getComponent(0);
					planePoint = planePoint.sub(t.mul(m2));
				}
			}
		} else {
			m1 = planePoint.getComponent(0) / s.getComponent(0);
			planePoint = planePoint.sub(s.mul(m1));
			if (t.getComponent(1) == 0) {
				m2 = planePoint.getComponent(2) / t.getComponent(2);
				planePoint = planePoint.sub(t.mul(m2));
			} else {
				m2 = planePoint.getComponent(1) / t.getComponent(1);
				planePoint = planePoint.sub(t.mul(m2));
			}
		}
		if (m1 > 0 && m2 > 0 && m1 + m2 < 1) {
			return p1.add(s.mul(m1)).add(t.mul(m2));
		}
		return null;
	}

}
