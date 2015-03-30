package renderer.entities;

import org.lwjgl.opengl.GL11;

import maze.Entity;
import maze.effects.Explosion;
import renderer.EntityRenderer;
import renderer.RenderUtils;

public class ExplosionRenderer implements EntityRenderer {

	@Override
	public void render(Entity entity) {
		GL11.glScaled(1/entity.getSize()/2, 1/entity.getSize()*0.25/2, 1/entity.getSize()/2);
		double time = ((Explosion) entity).getRemaining();
		GL11.glColor3d(1, 0.5 + time / 2, 0);
		RenderUtils.renderCube(false);
	}

}
