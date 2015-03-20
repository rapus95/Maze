package renderer.blocks;

import maze.BlockData;
import renderer.BlockRenderer;
import renderer.RenderUtils;

public class WallRenderer implements BlockRenderer {

	@Override
	public void render(BlockData block) {
		RenderUtils.renderCube(true);
	}

}
