package renderer;

import static math.vecmat.Vec.Vec3;
import static math.vecmat.Vec.asTmpBuffer;
import math.vecmat.Vec3;

import org.lwjgl.opengl.GL11;

public class RenderUtils {

	
	public static void renderCube(boolean colored) {
		Vec3 u2 = Vec3(-1, 1, 1), d2 = Vec3(-1, -1, 1), d3 = Vec3(1, -1, 1), u3 = Vec3(1, 1, 1),
				u1 = Vec3(-1, 1, -1), d1 = Vec3(-1, -1, -1), d4 = Vec3(1, -1, -1), u4 = Vec3(1, 1, -1);
			//UP
			if(colored) GL11.glColor3f(0.0f, 1.0f, 0.0f);
			renderFace(u4, u1, u2, u3);
			
			//DOWN
			if(colored) GL11.glColor3f(1.0f, 0.5f, 0.0f);
			renderFace(d3, d2, d1, d4);
			
			//FRONT
			if(colored) GL11.glColor3f(1.0f, 0.0f, 0.0f);
			renderFace(u3, u2, d2, d3);
			
			//BACK
			if(colored) GL11.glColor3f(1.0f, 1.0f, 0.0f);
			renderFace(d4, d1, u1, u4);
			
			//LEFT
			if(colored) GL11.glColor3f(0.0f, 0.0f, 1.0f);
			renderFace(u2, u1, d1, d2);
			
			//RIGHT
			if(colored) GL11.glColor3f(1.0f, 0.0f, 1.0f);
			renderFace(u4, u3, d3, d4);
	}
	
	private static void renderFace(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4){
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3(asTmpBuffer(v1));
		GL11.glVertex3(asTmpBuffer(v2));
		GL11.glVertex3(asTmpBuffer(v3));
		GL11.glVertex3(asTmpBuffer(v4));
		GL11.glEnd();
//		renderNormal(v1, v2, v3);
	}
	
	public static void renderNormal(Vec3 v1, Vec3 v2, Vec3 v3){
		Vec3 n = v2.sub(v1).cross(v3.sub(v1)).normalize();
		Vec3 center = v1.add(v2).add(v3).div(3);
		GL11.glColor3d(0, 0, 0);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3(asTmpBuffer(center));
		GL11.glVertex3(asTmpBuffer(center.add(n)));
		GL11.glEnd();
	}
	
	//private static final Sphere s = new Sphere();
	public static void renderSphere(){
		//s.draw(1, 10, 10);
	}
}
