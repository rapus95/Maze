package renderer.blocks;

import java.io.File;
import java.io.IOException;

import maze.BlockData;
import renderer.BlockRenderer;
import renderer.RenderUtils;
import xor.opengl.texture.Texture;

public class WallRenderer implements BlockRenderer {

	private Texture t;

	public WallRenderer() {
		try {
			t = Texture.create(new File("texture/strange_wood.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void render(BlockData block) {
		RenderUtils.renderTexturedCube(t);
	}

}
