package renderer;

import math.matrix.Mat;
import math.matrix.Vec;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class RenderUtils {

	public static void renderCube(boolean colored) {
		if (colored) {
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(0.0f, 1.0f, 0.0f);
			GL11.glVertex3f(1.0f, 1.0f, -1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
			GL11.glVertex3f(1.0f, 1.0f, 1.0f);
			GL11.glColor3f(1.0f, 0.5f, 0.0f);
			GL11.glVertex3f(1.0f, -1.0f, 1.0f);
			GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
			GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
			GL11.glVertex3f(1.0f, -1.0f, -1.0f);
			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			GL11.glVertex3f(1.0f, 1.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
			GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
			GL11.glVertex3f(1.0f, -1.0f, 1.0f);
			GL11.glColor3f(1.0f, 1.0f, 0.0f);
			GL11.glVertex3f(1.0f, -1.0f, -1.0f);
			GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
			GL11.glVertex3f(1.0f, 1.0f, -1.0f);
			GL11.glColor3f(0.0f, 0.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
			GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
			GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
			GL11.glColor3f(1.0f, 0.0f, 1.0f);
			GL11.glVertex3f(1.0f, 1.0f, -1.0f);
			GL11.glVertex3f(1.0f, 1.0f, 1.0f);
			GL11.glVertex3f(1.0f, -1.0f, 1.0f);
			GL11.glVertex3f(1.0f, -1.0f, -1.0f);
			GL11.glEnd();
		} else
			renderCubeBlack();
	}
	public static void renderCubeBuffers(boolean colored) {
		Vec u2 = Vec.fromList(-1, 1, 1), u1 = Vec.fromList(-1, -1, 1), u4 = Vec.fromList(1, -1, 1), u3 = Vec.fromList(1, 1, 1),
				d2 = Vec.fromList(-1, 1, -1), d1 = Vec.fromList(-1, -1, -1), d4 = Vec.fromList(1, -1, -1), d3 = Vec.fromList(1, 1, -1);
		if (colored) {
			//UP
			GL11.glColor3f(0.0f, 1.0f, 0.0f);
			renderFace(d3, d2, u2, u3);
			
			//DOWN
			GL11.glColor3f(1.0f, 0.5f, 0.0f);
			renderFace(u4, u1, d1, d4);
			
			//FRONT
			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			renderFace(u3, u2, u1, u4);
			
			//BACK
			GL11.glColor3f(1.0f, 1.0f, 0.0f);
			renderFace(d4, d1, d2, d3);
			
			//LEFT
			GL11.glColor3f(0.0f, 0.0f, 1.0f);
			renderFace(u2, d2, d1, u1);
			
			//RIGHT
			GL11.glColor3f(1.0f, 0.0f, 1.0f);
			renderFace(d3, u3, u4, d4);
		} else
			renderCubeBlack();
	}
	
	private static void renderFace(Vec v1, Vec v2, Vec v3, Vec v4){
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3(v1.asTmpDoubleBuffer(true));
		GL11.glVertex3(v2.asTmpDoubleBuffer(true));
		GL11.glVertex3(v3.asTmpDoubleBuffer(true));
		GL11.glVertex3(v4.asTmpDoubleBuffer(true));
		GL11.glEnd();
//		renderNormal(v1, v2, v3);
	}
	
	public static void renderNormal(Vec v1, Vec v2, Vec v3){
		Vec n = v2.sub(v1).cross(v3.sub(v1)).normalize();
		Vec center = v1.add(v2).add(v3).div(3);
		GL11.glColor3d(0, 0, 0);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3(center.asTmpDoubleBuffer(true));
		GL11.glVertex3(center.add(n).asTmpDoubleBuffer(true));
		GL11.glEnd();
	}
	
	private static void renderCubeBlack() {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor3f(0.0f, 0.0f, 0.0f);
		GL11.glVertex3f(1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, -1.0f);
		GL11.glEnd();
	}
	
	public static void renderCube() {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(1.0f, 1.0f, -1.0f);
		GL11.glVertex3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, 1.0f);
		GL11.glVertex3f(1.0f, -1.0f, -1.0f);
		GL11.glEnd();
	}

	private static final Sphere s = new Sphere();
	public static void renderSphere(){
		s.draw(1, 10, 10);
	}
}
