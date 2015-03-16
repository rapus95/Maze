package renderer;

import org.lwjgl.opengl.GL11;

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
}
