package renderer.entities;

import maze.Entity;
import maze.entities.Player;

import org.lwjgl.opengl.GL11;

import renderer.EntityRenderer;
import renderer.RenderUtils;

public class PlayerRenderer implements EntityRenderer {

	@Override
	public void render(Entity entity) {
		if (entity == entity.getMaze().currentPlayer())
			return;
		long dieAni = ((Player)entity).getDieAni();
		if (dieAni == -1)
			dieAni = 0;
		double posY = dieAni / 1000000000d * 4;
		GL11.glTranslated(0, posY, 0);
		GL11.glScaled(0.5 * 0.5, 0.5 * 0.5, 0.5 * 0.5);
		RenderUtils.renderCube(false);
	}
}
