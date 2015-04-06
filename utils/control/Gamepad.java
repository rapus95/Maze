package utils.control;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import math.utils.MathUtils;

import org.lwjgl.glfw.GLFW;

public class Gamepad implements Updateable {

	private final int index;
	private final float[] axis = new float[5];
	private final byte[] buffer = new byte[14];

	public Gamepad(int index) {
		this.index = index;
	}

	@Override
	public void update() {
		FloatBuffer fb = GLFW.glfwGetJoystickAxes(index);
		fb.get(axis);
		ByteBuffer bb = GLFW.glfwGetJoystickButtons(index);
		bb.get(buffer);
	}

	public double getState(int key) {
		if (key < 10) {
			float x = axis[key / 2];
			if (key % 2 == 1) {
				x = -x;
			}
			return MathUtils.clamp(x, 0, 1);
		}
		return buffer[key - 10];
	}

}
