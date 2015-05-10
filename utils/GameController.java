package utils;

import org.lwjgl.glfw.GLFW;

import control.Binding;
import control.DefaultController;
import control.KeyboardKey;

public class GameController extends DefaultController {

	public final Binding shoot = Binding.createAndDefault(KeyboardKey.getKey(GLFW.GLFW_KEY_LEFT_SHIFT));
	public final Binding cheat_changeGravity = Binding.createAndDefault(KeyboardKey.getKey(GLFW.GLFW_KEY_E));
	
	public GameController() {
		super();
		update();
	}
	
	@Override
	public void update() {
		super.update();
		shoot.update();
		cheat_changeGravity.update();
	}

}
