package utils.control;

import org.lwjgl.glfw.GLFW;

public class GamepadKey implements Key {

	private final Gamepad g;
	private final int key;
	
	public GamepadKey(Gamepad g, int key){
		this.key = key;
	}
	
	@Override
	public double getState() {
		return g.getKey(key)==GLFW.GLFW_PRESS?1:0;
	}

}
