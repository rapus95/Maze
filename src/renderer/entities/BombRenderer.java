package renderer.entities;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import maze.Entity;

import org.lwjgl.opengl.GL11;

import renderer.EntityRenderer;
import xor.model.Model;
import xor.model.ModelManager;
import xor.opengl.shader.ShaderAttribute;

public class BombRenderer implements EntityRenderer {

	private Model bomb;

	public BombRenderer() {
		try {
			bomb = new ModelManager().load(new File("models/Bomb.ms3d"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void render(Entity entity) {
		GL11.glColor3d(0, 0, 0);
		bomb.render(Arrays.asList(ShaderAttribute.OLD_POSITION));
	}

}
