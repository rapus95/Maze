package utils.control;

import org.lwjgl.glfw.GLFW;

public class Controller {

	public final Binding forward = new Binding(KeyboardKey.getKey(GLFW.GLFW_KEY_W));
	public final Binding backward = new Binding(KeyboardKey.getKey(GLFW.GLFW_KEY_S));
	public final Binding left = new Binding(KeyboardKey.getKey(GLFW.GLFW_KEY_A));
	public final Binding right = new Binding(KeyboardKey.getKey(GLFW.GLFW_KEY_D));
	public final Binding jump = new Binding(KeyboardKey.getKey(GLFW.GLFW_KEY_SPACE));

	public final Binding rotateRight = new Binding(new CursorKey(0));
	public final Binding rotateLeft = new Binding(new CursorKey(1));
	public final Binding rotateUp = new Binding(new CursorKey(2));
	public final Binding rotateDown = new Binding(new CursorKey(3));
	
	public final Binding shoot = new Binding(KeyboardKey.getKey(GLFW.GLFW_KEY_LEFT_SHIFT));
	
	public final Binding cheet_changeGravity = new Binding(KeyboardKey.getKey(GLFW.GLFW_KEY_E));

	public void update() {
		forward.update();
		backward.update();
		left.update();
		right.update();
		rotateLeft.update();
		rotateRight.update();
		rotateUp.update();
		rotateDown.update();
		jump.update();
		shoot.update();
		cheet_changeGravity.update();
	}
	
	//...
	
}
