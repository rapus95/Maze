package renderer.blocks;

import maze.BlockData;

import org.lwjgl.opengl.GL11;

import renderer.BlockRenderer;

public class AirRenderer implements BlockRenderer {

	@Override
	public void render(BlockData block) {
		if(block.vec.getComponent(2)>0)
			return;
		GL11.glColor3d(0.5, 0.5, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(-0.5, -0.5, -0.5);
		GL11.glVertex3d(+0.5, -0.5, -0.5);
		GL11.glVertex3d(+0.5, -0.5, +0.5);
		GL11.glVertex3d(-0.5, -0.5, +0.5);
		GL11.glEnd();
	}

}
