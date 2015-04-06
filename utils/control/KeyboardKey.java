package utils.control;

import org.lwjgl.glfw.GLFW;

public class KeyboardKey implements Key {

	private final int key;
	
	public KeyboardKey(int key){
		this.key = key;
	}
	
	@Override
	public double getState() {
		return GLFW.glfwGetKey(window, key)==GLFW.GLFW_PRESS?1:0;
	}

}
