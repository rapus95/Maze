package renderer.entities;

import org.lwjgl.opengl.GL11;

import maze.Entity;
import renderer.EntityRenderer;
import renderer.RenderUtils;

public class BombRenderer implements EntityRenderer {

	@Override
	public void render(Entity entity) {
		GL11.glTranslated(0, -0.25, 0);
		GL11.glScaled(0.5 * 0.25, 0.5 * 0.25, 0.5 * 0.25);
		GL11.glColor3d(0, 0, 0);
		RenderUtils.renderSphere();
	}

}
