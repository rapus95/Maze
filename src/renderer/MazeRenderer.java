package renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Map;

import math.vecmat.Mat3;
import math.vecmat.Mat4;
import math.vecmat.Vec;
import math.vecmat.Vec3;
import maze.Block;
import maze.BlockData;
import maze.Entity;
import maze.Maze;
import maze.blocks.Air;
import maze.blocks.Wall;
import maze.effects.Explosion;
import maze.entities.Bomb;
import maze.entities.Player;

import org.lwjgl.opengl.GL11;

import renderer.blocks.AirRenderer;
import renderer.blocks.WallRenderer;
import renderer.entities.BombRenderer;
import renderer.entities.ExplosionRenderer;
import renderer.entities.PlayerRenderer;

public class MazeRenderer {

	private static Map<Class<? extends Block>, BlockRenderer> rendererMappingBlocks = new HashMap<>();
	private static Map<Class<? extends Entity>, EntityRenderer> rendererMappingEntities = new HashMap<>();

	static {
		rendererMappingEntities.put(Player.class, new PlayerRenderer());
		rendererMappingEntities.put(Bomb.class, new BombRenderer());
		rendererMappingEntities.put(Explosion.class, new ExplosionRenderer());

		rendererMappingBlocks.put(Wall.class, new WallRenderer());
		rendererMappingBlocks.put(Air.class, new AirRenderer());
	}

	private final Maze maze;

	public MazeRenderer(Maze maze) {
		this.maze = maze;
	}

	public void render() {
		Vec3 dimensions = maze.getDimensions();
		Vec3 pos = maze.currentPlayer().getPos();
		final int renderDistance = 15;
		double posX = pos.get(0), posY = pos.get(1), posZ = pos.get(2);
		int leftXClip = Math.max(-1, (int) (posX - renderDistance + 0.5));
		int rightXClip = Math.min(1 + (int) (dimensions.get(0) + 0.5), (int) (posX + renderDistance + 0.5));
		int leftYClip = Math.max(-1, (int) (posY - renderDistance + 0.5));
		int rightYClip = Math.min(1 + (int) (dimensions.get(1) + 0.5), (int) (posY + renderDistance + 0.5));
		int leftZClip = Math.max(-1, (int) (posZ - renderDistance + 0.5));
		int rightZClip = Math.min(1 + (int) (dimensions.get(2) + 0.5), (int) (posZ + renderDistance + 0.5));
		Vec3 leftEnd = Vec.Vec3(leftXClip - 0.5, leftYClip - 0.5, leftZClip - 0.5), rightEnd = Vec.Vec3(rightXClip + 0.5, rightYClip + 0.5, rightZClip + 0.5);
		// Blocks
		Vec3 currPos;
		for (int x = leftXClip; x < rightXClip; x++) {
			for (int y = leftYClip; y < rightYClip; y++) {
				for (int z = leftZClip; z < rightZClip; z++) {
					currPos = Vec.Vec3(x, y, z);
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					GL11.glScaled(0.5, 0.5, 0.5);
					renderBlock(maze.get(currPos));
					if (!currPos.distanceSmaller(maze.get(currPos).vec, 1))
						System.out.println("WARNING!!!" + currPos + ":" + maze.get(currPos).vec);
					GL11.glPopMatrix();
				}
			}
		}
		DoubleBuffer fb = ByteBuffer.allocateDirect(8 * 16).order(ByteOrder.nativeOrder()).asDoubleBuffer();
		Mat4 m = Mat4.Mat4();
		// Entities
		for (Entity e : maze.getEntities()) {
			if (e.getPos().withinRectangle(leftEnd, rightEnd)) {
				GL11.glPushMatrix();
				GL11.glTranslated(e.getPos().x(), e.getPos().y(), e.getPos().z());
				GL11.glScaled(e.getSize(), e.getSize(), e.getSize());
				//GL11.glRotatef(90, 0, 1, 0);
				Mat3 rot = e.getRotation();
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						m.set(i, j, rot.get(i, j));
					}
				}
				fb.position(0);
				m.writeToGL(fb);
				fb.position(0);
				//GL11.glMultMatrixd(fb);
				
				//GL11.glRotatef(90, 0, 1, 0);
				//GL11.glRotatef(180, 0, 0, 1);
				
				renderEntity(e);
				GL11.glPopMatrix();
			}
		}
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
