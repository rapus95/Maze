package renderer.entities;

import org.lwjgl.opengl.GL11;

import maze.Entity;
import maze.effects.Explosion;
import renderer.EntityRenderer;
import renderer.RenderUtils;

public class ExplosionRenderer implements EntityRenderer {

	@Override
	public void render(Entity entity) {
		GL11.glTranslated(0, -0.5, 0);
		GL11.glScaled(0.5, 0.5 * 0.25, 0.5);
		double time = ((Explosion) entity).getRemaining();
		GL11.glColor3d(1, time / 2, 0);
		RenderUtils.renderCube();
	}

}
