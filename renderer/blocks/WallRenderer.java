package renderer.blocks;

import maze.BlockData;

import org.lwjgl.opengl.GL11;

import renderer.BlockRenderer;
import renderer.RenderUtils;

public class WallRenderer implements BlockRenderer {

	@Override
	public void render(BlockData block) {
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		RenderUtils.renderCubeBuffers(true);
	}

}
