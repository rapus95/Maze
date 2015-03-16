package renderer;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import math.matrix.Vec;
import maze.Block;
import maze.BlockData;
import maze.Entity;
import maze.Maze;
import maze.blocks.Air;
import maze.blocks.Wall;
import maze.effects.Explosion;
import maze.entities.Bomb;
import maze.entities.Player;
import renderer.blocks.AirRenderer;
import renderer.blocks.WallRenderer;
import renderer.entities.BombRenderer;
import renderer.entities.ExplosionRenderer;
import renderer.entities.PlayerRenderer;

public class MazeRenderer {

	private Map<Class<? extends Block>, BlockRenderer> rendererMappingBlocks = new HashMap<>();
	private Map<Class<? extends Entity>, EntityRenderer> rendererMappingEntities = new HashMap<>();

	public MazeRenderer() {
		rendererMappingEntities.put(Player.class, new PlayerRenderer());
		rendererMappingEntities.put(Bomb.class, new BombRenderer());
		rendererMappingEntities.put(Explosion.class, new ExplosionRenderer());

		rendererMappingBlocks.put(Wall.class, new WallRenderer());
		rendererMappingBlocks.put(Air.class, new AirRenderer());
	}

	public void render(Maze m) {
		Vec dimensions = m.getDimensions();
		Vec pos = m.currentPlayer().getPos();
		long dieAni = m.currentPlayer().getDieAni();
		if (dieAni == -1)
			dieAni = 0;
		double posX = pos.getComponent(0), posY = dieAni / 1000000000d * 4, posZ = pos.getComponent(1);
		int leftXClip = Math.max(0, (int) (posX - 24.5));
		int rightXClip = Math.min((int) (dimensions.getComponent(0) + 0.5), (int) (posX + 25.5));
		int leftZClip = Math.max(0, (int) (posZ - 24.5));
		int rightZClip = Math.min((int) (dimensions.getComponent(1) + 0.5), (int) (posZ + 25.5));
		Vec leftEnd = new Vec(leftXClip - 0.5, leftZClip - 0.5), rightEnd = new Vec(rightXClip + 0.5, rightZClip + 0.5);
		// Blocks
		Vec currPos;
		GL11.glPushMatrix();
		GL11.glTranslated(-posX, -posY, -posZ);
		for (int x = leftXClip; x < rightXClip; x++) {
			for (int z = leftZClip; z < rightZClip; z++) {
				currPos = new Vec((double) x, z);
				GL11.glPushMatrix();
				GL11.glTranslated(x, 0, z);
				renderBlock(m.get(currPos));
				GL11.glPopMatrix();
			}
		}
		// Entities
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		for (Entity e : m.getEntities()) {
			if (e.getPos().withinRectangle(leftEnd, rightEnd)) {
				GL11.glPushMatrix();
				GL11.glTranslated(e.getPos().getComponent(0), 0, e.getPos().getComponent(1));
				renderEntity(e);
				GL11.glPopMatrix();
			}
		}
		GL11.glPopMatrix();
	}
	public void renderEntity(Entity entity) {
		if (entity == null)
			return;
		Class<?> c = entity.getClass();
		EntityRenderer renderer = null;
		while (c != null && c != Object.class && (renderer = rendererMappingEntities.get(c)) == null)
			c = c.getSuperclass();
		if (renderer != null) {
			renderer.render(entity);
		}
	}

	public void renderBlock(BlockData block) {
		if (block == null)
			return;
		Class<?> c = block.block.getClass();
		BlockRenderer renderer = null;
		while (c != null && c != Object.class && (renderer = rendererMappingBlocks.get(c)) == null)
			c = c.getSuperclass();
		if (renderer != null) {
			renderer.render(block);
		}
	}

}
