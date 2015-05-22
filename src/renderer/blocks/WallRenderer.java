package renderer.blocks;

import java.nio.file.Paths;

import maze.BlockData;
import renderer.BlockRenderer;
import renderer.RenderUtils;
import window.Texture;

public class WallRenderer implements BlockRenderer {

	Texture t = Texture.create(Paths.get("texture/bad_wood.png"));
	
	@Override
	public void render(BlockData block) {
		RenderUtils.renderTexturedCube(t);
	}

}
