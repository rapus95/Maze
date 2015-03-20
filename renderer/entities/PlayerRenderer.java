package renderer.entities;

import maze.Entity;

import org.lwjgl.opengl.GL11;

import renderer.EntityRenderer;
import renderer.RenderUtils;

public class PlayerRenderer implements EntityRenderer {

	@Override
	public void render(Entity entity) {
		if (entity == entity.getMaze().currentPlayer())
			return;
		GL11.glColor3d(0, 0, 0);
		RenderUtils.renderCube(false);
	}
}
